package com.github.kr328.clash

import android.app.Application
import android.content.Context
import com.github.kr328.clash.common.Global
import com.github.kr328.clash.common.compat.currentProcessName
import com.github.kr328.clash.common.log.Log
import com.github.kr328.clash.remote.Remote
import com.github.kr328.clash.service.util.sendServiceRecreated
import com.github.kr328.clash.util.isTablet
import me.jessyan.autosize.AutoSizeConfig

@Suppress("unused")
class MainApplication : Application() {
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)

        Global.init(this)
    }

    private fun adaptScreen() {
        Global.setTablet(applicationContext.isTablet())
        if (Global.isTablet) {
            AutoSizeConfig.getInstance().designWidthInDp = 768
            AutoSizeConfig.getInstance().designHeightInDp = 1024
        } else {
            AutoSizeConfig.getInstance().designWidthInDp = 375
            AutoSizeConfig.getInstance().designHeightInDp = 772
        }
    }

    override fun onCreate() {
        super.onCreate()
        adaptScreen()
    }

    fun finalize() {
        Global.destroy()
    }

    fun start() {
        // Initialize AppCenter
        Tracker.initialize(this)

        val processName = currentProcessName

        Log.d("Process $processName started")

        if (processName == packageName) {
            Remote.launch()
        } else {
            sendServiceRecreated()
        }
    }
}