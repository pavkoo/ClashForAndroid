package com.github.kr328.clash.common

import android.app.Application
import com.github.kr328.clash.common.ucss.UiInfo
import com.github.kr328.clash.common.ucss.http.UserInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel

object Global : CoroutineScope by CoroutineScope(Dispatchers.IO) {
    val application: Application
        get() = application_

    val user: UserInfo
        get() = user_

    val ui: UiInfo
        get() = uiInfo_

    private lateinit var application_: Application
    private lateinit var user_: UserInfo
    private lateinit var uiInfo_: UiInfo

    fun init(application: Application) {
        this.application_ = application
        setUser(UserInfo())
        setUI(UiInfo())
    }

    fun setUser(user: UserInfo) {
        this.user_ = user
    }

    private fun setUI(ui: UiInfo) {
        this.uiInfo_ = ui
    }

    fun destroy() {
        cancel()
    }

}