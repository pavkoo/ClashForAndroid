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
        text = proxy?.name
        subtext = if (proxy?.delay in 0..Short.MAX_VALUE) proxy?.delay.toString()+"ms" else ""
    }
}