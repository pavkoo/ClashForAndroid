package com.github.kr328.clash.design.component

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import com.github.kr328.clash.core.model.Proxy
import com.github.kr328.clash.design.R
import com.github.kr328.clash.design.databinding.ComponentNodeItemBinding
import com.github.kr328.clash.design.util.layoutInflater
import com.github.kr328.clash.design.util.resolveClickableAttrs
import com.github.kr328.clash.design.util.selectableItemBackground

class ProxyNodeView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0
) : FrameLayout(context, attributeSet, defStyleAttr, defStyleRes) {
    private val binding = ComponentNodeItemBinding
        .inflate(context.layoutInflater, this, true)

    var icon: Drawable?
        get() = binding.iconView.background
        set(value) {
            binding.iconView.setImageDrawable(value)
        }

    var text: CharSequence?
        get() = binding.textView.text
        set(value) {
            binding.textView.text = value
        }

    var subtext: CharSequence?
        get() = binding.subtextView.text
        set(value) {
            binding.subtextView.text = value

            if (value == null) {
                binding.subtextView.visibility = View.GONE
            } else {
                binding.subtextView.visibility = View.VISIBLE
            }
        }

    init {
        context.resolveClickableAttrs(
            attributeSet,
            defStyleAttr,
            defStyleRes
        ) {
            isFocusable = focusable(true)
            isClickable = clickable(true)
            background = background() ?: context.selectableItemBackground
        }

        context.theme.obtainStyledAttributes(
            attributeSet,
            R.styleable.ProxyNodeView,
            defStyleAttr,
            defStyleRes
        ).apply {
            try {
                icon = getDrawable(R.styleable.ProxyNodeView_icon)
                text = getString(R.styleable.ProxyNodeView_text)
                subtext = getString(R.styleable.ProxyNodeView_subtext)
            } finally {
                recycle()
            }
        }
    }

    fun setSource(proxy: Proxy?) {
        if (proxy == null) {
            text = ""
            icon = null
            subtext = ""
        } else {
            text = proxy.name
            icon = getResByName(proxy.title)?.let { context.getDrawable(it) }
            subtext = if (proxy.delay in 0..Short.MAX_VALUE) proxy.delay.toString() + "ms" else ""
        }
    }

    fun setDivider(value: Boolean) {
        if (value) {
            binding.divider.visibility = View.VISIBLE
        } else {
            binding.divider.visibility = View.GONE
        }
    }

    private fun getResByName(title: String?): Int? {
        if (title.isNullOrEmpty()) {
            return null
        }
        when {
            title.contains("Taiwan") -> return R.drawable.tw
            title.contains("Japan") -> return R.drawable.jp
            title.contains("Korea") -> return R.drawable.kr
            title.contains("USA") -> return R.drawable.us
            title.contains("Singapore") -> return R.drawable.sg
            title.contains("Hong Kong") -> return R.drawable.hk
            title.contains("UK") -> return R.drawable.gb
            title.contains("Germany") -> return R.drawable.de
            title.contains("Canada") -> return R.drawable.ca
            title.contains("China") -> return R.drawable.cn
            title.contains("Austria") -> return R.drawable.at
            title.contains("Australia") -> return R.drawable.au
            title.contains("Belgium") -> return R.drawable.be
            title.contains("France") -> return R.drawable.fr
            title.contains("Israel") -> return R.drawable.il
            title.contains("India") -> return R.drawable.`in`
            title.contains("Italy") -> return R.drawable.it
            title.contains("Macau") -> return R.drawable.mo
            title.contains("Malaysia") -> return R.drawable.my
            title.contains("Netherlands") -> return R.drawable.nl
            title.contains("Philippines") -> return R.drawable.ph
            title.contains("Poland") -> return R.drawable.pl
            title.contains("Romania") -> return R.drawable.ro
            title.contains("Russia") -> return R.drawable.ru
            title.contains("Thailand") -> return R.drawable.th
            title.contains("Turkey") -> return R.drawable.tr
            title.contains("Vietnam") -> return R.drawable.vn
            else -> return null
        }

    }
}