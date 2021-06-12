package com.github.kr328.clash.design

import android.content.Context
import android.content.res.ColorStateList
import android.view.View
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.github.kr328.clash.core.model.Proxy
import com.github.kr328.clash.core.model.TunnelState
import com.github.kr328.clash.design.adapter.ProxyAdapter
import com.github.kr328.clash.design.adapter.ProxyPageAdapter
import com.github.kr328.clash.design.component.ProxyMenu
import com.github.kr328.clash.design.component.ProxyViewConfig
import com.github.kr328.clash.design.databinding.DesignModeBinding
import com.github.kr328.clash.design.databinding.DesignProxyBinding
import com.github.kr328.clash.design.model.ProxyState
import com.github.kr328.clash.design.store.UiStore
import com.github.kr328.clash.design.util.applyFrom
import com.github.kr328.clash.design.util.layoutInflater
import com.github.kr328.clash.design.util.resolveThemedColor
import com.github.kr328.clash.design.util.root
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ModeDesign(
    context: Context,
    overrideMode: TunnelState.Mode?
) : Design<ModeDesign.Request>(context) {
    sealed class Request {
        data class PatchMode(val mode: TunnelState.Mode?) : Request()
    }

    private val binding = DesignModeBinding
        .inflate(context.layoutInflater, context.root, false)

    override val root: View = binding.root

    suspend fun showModeSwitchTips() {
        withContext(Dispatchers.Main) {
            Toast.makeText(context, R.string.mode_switch_tips, Toast.LENGTH_LONG).show()
        }
    }

    init {
        binding.self = this
        binding.activityBarLayout.applyFrom(context)

        binding.check = overrideMode == TunnelState.Mode.Global
    }


    fun toggle() {
        binding.check = !binding.check
        if (binding.check) {
            requests.trySend(Request.PatchMode(TunnelState.Mode.Global))
        } else {
            requests.trySend(Request.PatchMode(TunnelState.Mode.Rule))
        }
    }
}