package com.github.kr328.clash


import com.github.kr328.clash.core.Clash
import com.github.kr328.clash.design.WifiDesign
import com.github.kr328.clash.util.withClash
import kotlinx.coroutines.isActive
import kotlinx.coroutines.selects.select


class WifiActivity : BaseActivity<WifiDesign>() {
    override suspend fun main() {
        val configuration = withClash { queryOverride(Clash.OverrideSlot.Persist) }

        val design = WifiDesign(this, configuration)

        defer {
            withClash {
                patchOverride(Clash.OverrideSlot.Persist, configuration)
            }
        }

        setContentDesign(design)

        while (isActive) {
            select<Unit> {

            }
        }
    }


}