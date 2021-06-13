package com.github.kr328.clash.ucss

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.kr328.clash.R
import com.github.kr328.clash.common.Global
import com.github.kr328.clash.common.Global.user
import com.github.kr328.clash.common.log.Log
import com.github.kr328.clash.common.ucss.http.*
import com.github.kr328.clash.common.util.intent
import com.github.kr328.clash.databinding.ActivityAccountBinding
import com.github.kr328.clash.design.store.UiStore
import com.github.kr328.clash.design.ui.Surface
import com.github.kr328.clash.design.util.applyFrom
import com.github.kr328.clash.design.util.setOnInsertsChangedListener
import com.github.kr328.clash.util.stopClashService
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class AccountActivity : AppCompatActivity() {
    val surface = Surface()
    private lateinit var binding: ActivityAccountBinding

    private val adapter: AccountNodeAdapter
        get() = binding.rvAccount.adapter!! as AccountNodeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.setOnInsertsChangedListener {
            if (surface.insets != it) {
                surface.insets = it
            }
        }
        theme.applyStyle(R.style.AppThemeDark, true)
        binding = DataBindingUtil.setContentView(
            this, R.layout.activity_account
        )
        binding.activityBarLayout.applyFrom(this)

        initView()
    }

    @SuppressLint("CheckResult")
    private fun initView() {
        binding.email = Global.user.email
        binding.rvAccount.apply {
            adapter = AccountNodeAdapter {
                if (it.serviceid == Global.user.serviceId) {
                    return@AccountNodeAdapter
                }
                binding.loading = true
                val api = Api.createReq(UserApi::class.java)
                api.subscription(it.serviceid)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ res ->
                        val store = UiStore(this@AccountActivity)
                        user.subUri = res.data.url
                        user.serviceId = it.serviceid
                        val gson = Gson()
                        store.userInfo = gson.toJson(user)
                        refresh(it.serviceid)
                        stopClashService()
                        binding.loading = false
                    }, {
                        binding.loading = false
                    })

            }
            layoutManager = LinearLayoutManager(this@AccountActivity)
        }

        binding.loading = true
        val api = Api.createReq(UserApi::class.java)
        api.userService(Global.user.userid)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    binding.loading = false
                    if (it.isOk) {
                        GlobalScope.launch(Dispatchers.Main) {
                            for (datum in it.data) {
                                datum.selected = Global.user.serviceId == datum.serviceid
                            }
                            adapter.updateSource(it.data)
                        }
                    }
                }, {
                    binding.loading = false
                    Log.e("account", it)
                }
            )
    }

    private fun refresh(serviceid: Int) {
        for (tradeService in adapter.service) {
            tradeService.selected = serviceid == tradeService.serviceid
        }
        adapter.notifyDataSetChanged()
    }


    fun logout() {
        val store = UiStore(this)
        store.userInfo = ""
        Global.setUser(UserInfo())
        stopClashService()
        startActivity(LoginActivity::class.intent)
        finish()
    }
}
