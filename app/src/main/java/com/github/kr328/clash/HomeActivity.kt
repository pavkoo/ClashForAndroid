package com.github.kr328.clash

import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import com.github.kr328.clash.common.Global
import com.github.kr328.clash.common.util.intent
import com.github.kr328.clash.common.util.ticker
import com.github.kr328.clash.core.bridge.ClashException
import com.github.kr328.clash.core.model.FetchStatus
import com.github.kr328.clash.core.model.Proxy
import com.github.kr328.clash.design.HomeDesign
import com.github.kr328.clash.design.ProxyDesign
import com.github.kr328.clash.design.ui.ToastDuration
import com.github.kr328.clash.design.util.openInBrowser
import com.github.kr328.clash.design.util.resolveThemedColor
import com.github.kr328.clash.design.util.resolveThemedResourceId
import com.github.kr328.clash.design.util.showExceptionToast
import com.github.kr328.clash.service.model.Profile
import com.github.kr328.clash.store.TipsStore
import com.github.kr328.clash.ucss.AccountActivity
import com.github.kr328.clash.ucss.LoginActivity
import com.github.kr328.clash.util.startClashService
import com.github.kr328.clash.util.stopClashService
import com.github.kr328.clash.util.withClash
import com.github.kr328.clash.util.withProfile
import kotlinx.coroutines.*
import kotlinx.coroutines.selects.select
import java.util.concurrent.TimeUnit

class HomeActivity : BaseActivity<HomeDesign>() {
    override suspend fun main() {
        val design = HomeDesign(this)

        setContentDesign(design)

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
                        HomeDesign.Request.OpenWifi ->
                            startActivity(WifiActivity::class.intent)
                        HomeDesign.Request.OpenUCAbout ->
                            startActivity(AboutUCSSActivity::class.intent)
                        HomeDesign.Request.OpenAccount ->
                            startActivity(AccountActivity::class.intent)
                        HomeDesign.Request.OpenAbout ->
                            design.showAbout(queryAppVersionName())
                        HomeDesign.Request.OpenDrawer ->
                            design.openDrawer()
                        HomeDesign.Request.OpenSupport -> {
                            val uri = "https://my.undercurrentss.net/submitticket.php".toUri()
                            uri.openInBrowser(this@HomeActivity)
                        }
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
                val des = "Proxies"
                var found = false
                for (name in names) {
                    if (name == des) {
                        found = true
                        break
                    }
                }
                if (found) {
                    design?.name = des
                } else {
                    design?.name = names[0]
                }
                val group = queryProxyGroup(design?.name!!, uiStore.proxySort)
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

        if (active == null || !active.imported || !active.active || active.name != Global.user.serviceId.toString()) {
//            showToast(R.string.noService, ToastDuration.Long) {
//                setAction(R.string.profiles) {
//                    startActivity(LoginActivity::class.intent)
//                }
//            }
            fetchOrCreateProfile()
            return
        }

        realStart()
    }

    private suspend fun fetchOrCreateProfile() {
        val list = withProfile {
            queryAll()
        }
        //更新
        for (profile in list) {
            if (profile.name == Global.user.serviceId.toString()) {
                withProfile {
                    Global.user.currentUuid = profile.uuid
                    update(profile.uuid)
                    setActive(profile)
                    realStart()
                }
                return
            }
        }

        //创建
        val uuid = withProfile { create(Profile.Type.Url, Global.user.serviceId.toString()) }
        val original = withProfile { queryByUUID(uuid) } ?: return

//        val profile = original.copy(source = "https://feedneo.com/files/b4aB7wjxan/clash.yml")
        val profile = original.copy(source = Global.user.subUri)
        try {
            withProfile {
                patch(profile.uuid, profile.name, profile.source, profile.interval)
                coroutineScope {
                    commit(profile.uuid) {
                        launch {
                            updateStatus(it)
                        }
                    }
                    release(uuid)
                    setActive(profile)
                }
            }
        } catch (e: Exception) {
            design?.setClashRunning(false)
        } finally {
            realStart()
        }
    }

    private suspend fun updateStatus(status: FetchStatus) {
        var text: String? = null
        text = when (status.action) {
            FetchStatus.Action.FetchConfiguration -> {
                getString(
                    com.github.kr328.clash.design.R.string.format_fetching_configuration,
                    status.args[0]
                )
            }
            FetchStatus.Action.FetchProviders -> {
                getString(
                    com.github.kr328.clash.design.R.string.format_fetching_provider,
                    status.args[0]
                )
            }
            FetchStatus.Action.Verifying -> {
                getString(com.github.kr328.clash.design.R.string.verifying)
            }
        }
        design?.showToast(text, ToastDuration.Long)
    }


    override fun onStopped(cause: String?) {
        super.onStopped(cause)
        if (cause == "No profile selected") {
            launch {
                withProfile {
                    delete(Global.user.currentUuid)
                }
            }
        }
    }

    private suspend fun realStart() {
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