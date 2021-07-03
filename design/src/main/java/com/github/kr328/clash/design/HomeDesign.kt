package com.github.kr328.clash.design


import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.InsetDrawable
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.kr328.clash.core.model.ProxyGroup
import com.github.kr328.clash.core.model.TunnelState
import com.github.kr328.clash.core.util.trafficTotal
import com.github.kr328.clash.design.adapter.ProxyNodeAdapter
import com.github.kr328.clash.design.databinding.DesignAboutBinding
import com.github.kr328.clash.design.databinding.DesignHomeBinding
import com.github.kr328.clash.design.util.layoutInflater
import com.github.kr328.clash.design.util.resolveThemedColor
import com.github.kr328.clash.design.util.root
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class HomeDesign(context: Context) : Design<HomeDesign.Request>(context) {

    enum class Request {
        ToggleStatus,
        OpenProxy,
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

    lateinit var name: String
    lateinit var currentNode: String

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
                request(Request.FetchProxy)
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
//            val ATTRS = intArrayOf(android.R.attr.listDivider)
//
//            val a = context.obtainStyledAttributes(ATTRS)
//            val divider: Drawable? = a.getDrawable(0)
//            val inset = resources.getDimensionPixelSize(R.dimen.item_touch_helper_max_drag_scroll_per_frame)
//            val insetDivider = InsetDrawable(divider, inset, 0, inset, 0)
//            a.recycle()
//
//            val itemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
//            itemDecoration.setDrawable(insetDivider)
//            addItemDecoration(itemDecoration)
        }
    }

    fun request(request: Request) {
        requests.trySend(request)
    }

    suspend fun updateProxy(group: ProxyGroup) {
        adapter.updateSource(group.proxies)
        updateCurrent(group.now)
        setConState(ConState.ED)
        urlTesting = false
        withContext(Dispatchers.Main) {
            updateUrlTestButtonStatus()
        }
    }

    private suspend fun updateCurrent(now: String) {
        withContext(Dispatchers.Main) {
            val c = adapter.states.find { s -> s.name == now }
            binding.currentNode.setSource(c)
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
        updateCurrent(currentNode)
    }
}