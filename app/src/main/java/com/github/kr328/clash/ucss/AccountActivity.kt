package com.github.kr328.clash.ucss

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.github.kr328.clash.R
import com.github.kr328.clash.databinding.ActivityAccountBinding
import com.github.kr328.clash.design.ui.Surface
import com.github.kr328.clash.design.util.applyFrom
import com.github.kr328.clash.design.util.setOnInsertsChangedListener


class AccountActivity : AppCompatActivity() {
    val surface = Surface()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.setOnInsertsChangedListener {
            if (surface.insets != it) {
                surface.insets = it
            }
        }
        theme.applyStyle(R.style.AppThemeDark, true)
        val binding: ActivityAccountBinding = DataBindingUtil.setContentView(
            this, R.layout.activity_account
        )
        binding.activityBarLayout.applyFrom(this)

        
    }


    fun logout() {

    }
}