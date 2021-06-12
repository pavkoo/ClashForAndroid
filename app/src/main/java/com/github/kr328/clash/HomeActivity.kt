package com.github.kr328.clash

import androidx.activity.result.contract.ActivityResultContracts
import com.github.kr328.clash.common.util.intent
import com.github.kr328.clash.common.util.ticker
import com.github.kr328.clash.core.model.Proxy
import com.github.kr328.clash.design.HomeDesign
import com.github.kr328.clash.design.ProxyDesign
import com.github.kr328.clash.design.ui.ToastDuration
import com.github.kr328.clash.store.TipsStore
import com.github.kr328.clash.util.startClashService
import com.github.kr328.clash.util.stopClashService
import com.github.kr328.clash.util.withClash
import com.github.kr328.clash.util.withProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class HomeActivity : BaseActivity<HomeDesign>() {
    override suspend fun main() {
        val design = HomeDesign(this)

        setContentDesign(design)

        launch(Dispatchers.IO) {
            showUpdatedTips(design)
        }

        design.fetch()

        val ticker = ticker(TimeUnit.SECONDS.toMillis(1))

        while (isActive) {
            select<Unit> {
                events.onReceive {
                    when (it) {
                        Event.ActivityStart,
                        Event.ServiceRecreated,
                        Event.ClashStop, Event.ClashStart,
                        Event.ProfileLoaded, Event.ProfileChanged -> design.fetch()
                        else -> Unit
                    }
                }
                design.requests.onReceive {
                    when (it) {
                        HomeDesign.Request.ToggleStatus -> {
                            if (clashRunning)
                                stopClashService()
                            else
                                design.startClash()
                        }
                        HomeDesign.Request.OpenProxy ->
                            startActivity(ModeActivity::class.intent)
                        HomeDesign.Request.OpenProfiles ->
                            startActivity(ProfilesActivity::class.intent)
                        HomeDesign.Request.OpenProviders ->
                            startActivity(ProvidersActivity::class.intent)
                        HomeDesign.Request.OpenLogs ->
                            startActivity(LogsActivity::class.intent)
                        HomeDesign.Request.OpenSettings ->
                            startActivity(SettingsActivity::class.intent)
                        HomeDesign.Request.OpenHelp ->
                            startActivity(HelpActivity::class.intent)
                        HomeDesign.Request.OpenUCAbout ->
                            startActivity(AboutUCSSActivity::class.intent)
                        HomeDesign.Request.OpenAbout ->
                            design.showAbout(queryAppVersionName())
                        HomeDesign.Request.OpenDrawer ->
                            design.openDrawer()
                        HomeDesign.Request.FetchProxy ->
                            fetchProxy()
                        HomeDesign.Request.Ping ->
                            ping()
                        HomeDesign.Request.Select ->
                            selectNode()
                    }
                }
                if (clashRunning) {
                    ticker.onReceive {
                        design.fetchTraffic()
                    }
                }
            }
        }
    }

    private suspend fun selectNode() {
        withClash {
            patchSelector(design?.name!!, design?.currentNode!!)
            design?.changeNode()
        }
    }

    private suspend fun fetchProxy() {
        withClash {
            val names = queryProxyGroupNames(true)
            if (names.isNotEmpty()) {
                design?.name = names[0]
                val group = queryProxyGroup(names[0], uiStore.proxySort)
                design?.updateProxy(group)
            }
        }
    }

    private suspend fun ping() {
        launch {
            withClash {
                design?.name?.let { healthCheck(it) }
            }

            fetchProxy()
        }
    }

    private suspend fun showUpdatedTips(design: HomeDesign) {
        val tips = TipsStore(this)

        if (tips.primaryVersion != TipsStore.CURRENT_PRIMARY_VERSION) {
            tips.primaryVersion = TipsStore.CURRENT_PRIMARY_VERSION

            val pkg = packageManager.getPackageInfo(packageName, 0)

            if (pkg.firstInstallTime != pkg.lastUpdateTime) {
                design.showUpdatedTips()
            }
        }
    }

    private suspend fun HomeDesign.fetch() {
        setClashRunning(clashRunning)

        val state = withClash {
            queryTunnelState()
        }
        val providers = withClash {
            queryProviders()
        }

        setMode(state.mode)
        setHasProviders(providers.isNotEmpty())

        withProfile {
            setProfileName(queryActive()?.name)
        }
    }

    private suspend fun HomeDesign.fetchTraffic() {
        withClash {
            setForwarded(queryTrafficTotal())
        }
    }

    private suspend fun HomeDesign.startClash() {
        setConState(HomeDesign.ConState.ING)
        val active = withProfile { queryActive() }

        if (active == null || !active.imported) {
            showToast(R.string.no_profile_selected, ToastDuration.Long) {
                setAction(R.string.profiles) {
                    startActivity(ProfilesActivity::class.intent)
                }
            }

            return
        }

        val vpnRequest = startClashService()

        try {
            if (vpnRequest != null) {
                val result = startActivityForResult(
                    ActivityResultContracts.StartActivityForResult(),
                    vpnRequest
                )

                if (result.resultCode == RESULT_OK)
                    startClashService()
            }
        } catch (e: Exception) {
            design?.showToast(R.string.unable_to_start_vpn, ToastDuration.Long)
        }
    }

    private suspend fun queryAppVersionName(): String {
        return withContext(Dispatchers.IO) {
            packageManager.getPackageInfo(packageName, 0).versionName
        }
    }
}