package com.github.kr328.clash.design

import android.content.Context
import android.net.wifi.WifiManager
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.github.kr328.clash.common.Global
import com.github.kr328.clash.core.model.ConfigurationOverride
import com.github.kr328.clash.design.databinding.DesignWifiBinding
import com.github.kr328.clash.design.util.applyFrom
import com.github.kr328.clash.design.util.layoutInflater
import com.github.kr328.clash.design.util.root

class WifiDesign(
    context: Context,
    val configuration: ConfigurationOverride
) : Design<WifiDesign.Request>(context) {
    enum class Request {
        Toggle
    }

    private val binding = DesignWifiBinding
        .inflate(context.layoutInflater, context.root, false)

    override val root: View = binding.root


    init {
        binding.self = this
        binding.activityBarLayout.applyFrom(context)

        binding.checked = configuration.allowLan != false

        refreshView()
    }


    fun request(request: Request) {
        binding.checked = !binding.checked!!
        configuration.allowLan = binding.checked
        refreshView()
    }

    fun updateIp(ip: String) {
        binding.ip = ip
    }

    private fun refreshView() {
        if (!binding.checked!!) {
            binding.ip = ""
            binding.port.text = ""
            binding.type.text = ""
        } else {
            binding.port.text = "7890"
            binding.type.text = "en0"
            getIp()
        }
    }


    private fun getIp() {
        val wifiManager =
            Global.application.applicationContext.getSystemService(AppCompatActivity.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo
        val ipAddress = wifiInfo.ipAddress
        val ip =
            (ipAddress and 0xff).toString() + "." + (ipAddress shr 8 and 0xff) + "." + (ipAddress shr 16 and 0xff) + "." + (ipAddress shr 24 and 0xff)
        updateIp(ip)
    }
}