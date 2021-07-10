package com.github.kr328.clash.design

import android.content.Context
import android.view.View
import com.github.kr328.clash.core.model.TunnelState
import com.github.kr328.clash.design.databinding.DesignModeBinding
import com.github.kr328.clash.design.store.UiStore
import com.github.kr328.clash.design.util.applyFrom
import com.github.kr328.clash.design.util.layoutInflater
import com.github.kr328.clash.design.util.root

class ModeDesign(
    context: Context,
    overrideMode: TunnelState.Mode?,
    val uiStore: UiStore
) : Design<ModeDesign.Request>(context) {
    sealed class Request {
        data class PatchMode(val mode: TunnelState.Mode?) : Request()
    }

    private val binding = DesignModeBinding
        .inflate(context.layoutInflater, context.root, false)

    override val root: View = binding.root

    init {
        binding.self = this
        binding.activityBarLayout.applyFrom(context)

        binding.check = overrideMode == TunnelState.Mode.Global
        binding.ivGlobal.isSelected = binding.check
        binding.ivSmart.isSelected = !binding.check
    }


    fun toggle() {
        binding.check = !binding.check
        binding.ivGlobal.isSelected = binding.check
        binding.ivSmart.isSelected = !binding.check
        uiStore.global = binding.check
        if (binding.check) {
            requests.trySend(Request.PatchMode(TunnelState.Mode.Global))
        } else {
            requests.trySend(Request.PatchMode(TunnelState.Mode.Rule))
        }
    }
}