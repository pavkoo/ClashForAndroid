package com.github.kr328.clash.design


import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.kr328.clash.common.Global
import com.github.kr328.clash.core.model.Proxy
import com.github.kr328.clash.core.model.ProxyGroup
import com.github.kr328.clash.core.model.TunnelState
import com.github.kr328.clash.core.util.trafficTotal
import com.github.kr328.clash.design.adapter.ProxyNodeAdapter
import com.github.kr328.clash.design.databinding.DesignAboutBinding
import com.github.kr328.clash.design.databinding.DesignHomeBinding
import com.github.kr328.clash.design.store.UiStore
import com.github.kr328.clash.design.util.layoutInflater
import com.github.kr328.clash.design.util.resolveThemedColor
import com.github.kr328.clash.design.util.root
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import kotlinx.coroutines.*


class HomeDesign(context: Context, val uiStore: UiStore) : Design<HomeDesign.Request>(context) {

    enum class Request {
        ToggleStatus,
        OpenProxy,
        OpenMode,
        OpenProfiles,
        OpenProviders,
        OpenLogs,
        OpenSettings,
        OpenHelp,
        OpenAbout,
        OpenDrawer,
        FetchProxy,
        Ping,
        Select,
        ForceSelect,
        OpenWifi,
        OpenAccount,
        OpenSupport,
        OpenUCAbout,
    }

    enum class ConState {
        DIS,
        ING,
        ED
    }

    private val binding = DesignHomeBinding
        .inflate(context.layoutInflater, context.root, false)

    private val adapter: ProxyNodeAdapter
        get() = binding.rvProxy.adapter!! as ProxyNodeAdapter
    private var urlTesting: Boolean = false

    var group: String? = null
    var currentNode: String? = null
    var init: Boolean = false

    override val root: View
        get() = binding.root

    suspend fun setProfileName(name: String?) {
        withContext(Dispatchers.Main) {
            binding.profileName = name
        }
    }

    suspend fun setClashRunning(running: Boolean) {
        withContext(Dispatchers.Main) {
            binding.clashRunning = running
            if (running) {
                setConState(ConState.ED)
            } else {
                setConState(ConState.DIS)
            }
        }
    }

    suspend fun setConState(state: ConState) {
        withContext(Dispatchers.Main) {
            binding.conState = state
        }
    }

    suspend fun setForwarded(value: Long) {
        withContext(Dispatchers.Main) {
            binding.forwarded = value.trafficTotal()
        }
    }

    suspend fun setMode(mode: TunnelState.Mode) {
        withContext(Dispatchers.Main) {
            binding.mode = when (mode) {
                TunnelState.Mode.Direct -> context.getString(R.string.rule_mode)
                TunnelState.Mode.Global -> context.getString(R.string.global_mode)
                TunnelState.Mode.Rule -> context.getString(R.string.rule_mode)
                TunnelState.Mode.Script -> context.getString(R.string.rule_mode)
            }
        }
    }

    suspend fun openDrawer() {
        withContext(Dispatchers.Main) {
            binding.drawer.openDrawer(binding.menu)
        }
    }

    suspend fun setHasProviders(has: Boolean) {
        withContext(Dispatchers.Main) {
            binding.hasProviders = has
        }
    }

    suspend fun showAbout(versionName: String) {
        withContext(Dispatchers.Main) {
            val binding = DesignAboutBinding.inflate(context.layoutInflater).apply {
                this.versionName = versionName
            }

            AlertDialog.Builder(context)
                .setView(binding.root)
                .show()
        }
    }

    suspend fun showNotConnected(){
        withContext(Dispatchers.Main) {
            Toast.makeText(context,R.string.connHint,Toast.LENGTH_SHORT).show()
        }
    }

    suspend fun showUpdatedTips() {
        withContext(Dispatchers.Main) {
            MaterialAlertDialogBuilder(context)
                .setTitle(R.string.version_updated)
                .setMessage(R.string.version_updated_tips)
                .setPositiveButton(R.string.ok) { _, _ -> }
                .show()
        }
    }

    init {
        binding.self = this

        binding.colorClashStarted = context.resolveThemedColor(R.attr.colorPrimary)
        binding.colorClashStopped = context.resolveThemedColor(R.attr.colorClashStopped)
        binding.conState = ConState.DIS

        binding.rvProxy.apply {
            adapter = ProxyNodeAdapter {
                currentNode = it
                request(Request.Select)
            }
            layoutManager = LinearLayoutManager(context)
            clipToPadding = false
        }
        init = true
    }

    private fun updateUIFor() {
        GlobalScope.launch {
            withContext(Dispatchers.Main) {
                fitMode()
                val service =
                    Global.user.all.services.find { it.name == Global.user.serviceName }
                val proxy = service?.servers?.map {
                    Proxy(it.name, it.name, "", Proxy.Type.Selector, 65535)
                }
                if (proxy != null) {
                    adapter.updateSource(proxy)
                    val currentNode1 = uiStore.currentNode
                    if (currentNode1.isEmpty()) {
                        if (proxy.isNotEmpty()) {
                            Global.ui.needPatchNode = true
                            updateCurrent(proxy[0].name)
                            request(Request.ForceSelect)
                        }
                    } else {
                        updateCurrent(currentNode1)
                    }
                }
            }
        }
    }

    private suspend fun fitMode() {
        if (uiStore.global) {
            group = "GLOBAL"
            setMode(TunnelState.Mode.Global)
        } else {
            group = "Proxies"
            setMode(TunnelState.Mode.Rule)
        }
    }

    fun request(request: Request) {
        requests.trySend(request)
    }

    suspend fun updatePing(group: ProxyGroup) {
        adapter.updateSource(group.proxies)
        updateCurrent(group.now)
        urlTesting = false
        withContext(Dispatchers.Main) {
            updateUrlTestButtonStatus()
        }
    }

    private suspend fun updateCurrent(now: String) {
        currentNode = now
        Global.ui.currentNode = currentNode
        uiStore.currentNode = now
        withContext(Dispatchers.Main) {
            val c = adapter.states.find { s -> s.name == now }
            binding.currentNode.setSource(c)
            adapter?.notifyDataSetChanged()
        }
    }


    fun requestUrlTesting() {
        urlTesting = true

        requests.trySend(Request.Ping)

        updateUrlTestButtonStatus()
    }

    private fun updateUrlTestButtonStatus() {
        if (urlTesting) {
            binding.tvPing.visibility = View.GONE
            binding.urlTestProgressView.setStart(true)
        } else {
            binding.tvPing.visibility = View.VISIBLE
            binding.urlTestProgressView.setStart(false)
        }
    }

    suspend fun changeNode() {
        updateCurrent(currentNode!!)
    }

    suspend fun updateAllNodes() {
        if (init) {
            init = false
            updateUIFor()
        }
        if (Global.ui.switchMode) {
            Global.ui.switchMode = false
            fitMode()
            if (uiStore.global) {
                Global.ui.needPatchNode = true
                request(Request.ForceSelect)
            }
        }
        if (Global.ui.switchAccount) {
            Global.ui.switchAccount = false
            updateUIFor()
        }
    }
}