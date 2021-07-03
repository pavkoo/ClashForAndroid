package com.github.kr328.clash.common

import android.app.Application
import com.github.kr328.clash.common.ucss.http.UserInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel

object Global : CoroutineScope by CoroutineScope(Dispatchers.IO) {
    val application: Application
        get() = application_

    val user: UserInfo
        get() = user_

    val isTablet: Boolean
        get() = isTablet_

    private lateinit var application_: Application
    private lateinit var user_: UserInfo
    private var isTablet_: Boolean = false

    fun init(application: Application) {
        this.application_ = application
        setUser(UserInfo())
    }

    fun setUser(user: UserInfo) {
        this.user_ = user
    }

    fun destroy() {
        cancel()
    }

    fun setTablet(value: Boolean) {
        this.isTablet_ = value
    }
}