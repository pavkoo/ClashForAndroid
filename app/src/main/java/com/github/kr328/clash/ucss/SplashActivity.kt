package com.github.kr328.clash.ucss

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.kr328.clash.HomeActivity
import com.github.kr328.clash.MainApplication
import com.github.kr328.clash.R
import com.github.kr328.clash.common.Global
import com.github.kr328.clash.common.Global.application
import com.github.kr328.clash.common.ucss.http.UserInfo
import com.github.kr328.clash.design.store.UiStore
import com.google.gson.Gson


class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val store = UiStore(this)
        var userInfo = store.userInfo
        if (userInfo.isEmpty()) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            val gson = Gson()
            val user = gson.fromJson(userInfo, UserInfo::class.java)
            if (user == null || user.token.isEmpty()) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Global.setUser(user)
                (Global.application as MainApplication).start()
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
        }
    }
}