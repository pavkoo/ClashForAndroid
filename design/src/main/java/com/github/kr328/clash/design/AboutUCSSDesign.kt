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
import com.github.kr328.clash.design.databinding.DesignAboutUcssBinding
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

class AboutUCSSDesign(
    context: Context
) : Design<AboutUCSSDesign.Request>(context) {
    enum class Request {
        Privacy,
        Terms
    }

    private val binding = DesignAboutUcssBinding
        .inflate(context.layoutInflater, context.root, false)

    override val root: View = binding.root


    init {
        binding.self = this
        binding.activityBarLayout.applyFrom(context)

    }


    fun request(request: Request) {
        requests.trySend(request)
    }

    suspend fun setVersion(version: String?) {
        withContext(Dispatchers.Main) {
            binding.version = version
        }
    }

}