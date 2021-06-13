package com.github.kr328.clash


import androidx.core.net.toUri
import com.github.kr328.clash.design.AboutUCSSDesign
import com.github.kr328.clash.design.util.openInBrowser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.withContext

class AboutUCSSActivity : BaseActivity<AboutUCSSDesign>() {
    override suspend fun main() {
        val design = AboutUCSSDesign(this)

        setContentDesign(design)

        withContext(Dispatchers.IO) {
            val version = packageManager.getPackageInfo(packageName, 0).versionName
            design.setVersion(version)
        }
        while (isActive) {
            select<Unit> {
                design.requests.onReceive {
                    when (it) {
                        AboutUCSSDesign.Request.Privacy -> {
                            val uri = "https://undercurrentss.net/privacy-policy/".toUri()
                            uri.openInBrowser(this@AboutUCSSActivity)
                        }
                        AboutUCSSDesign.Request.Terms -> {
                            val uri = "https://undercurrentss.net/tos/".toUri()
                            uri.openInBrowser(this@AboutUCSSActivity)
                        }
                    }
                }
            }
        }
    }
}