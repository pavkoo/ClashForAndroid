package com.github.kr328.clash


import com.github.kr328.clash.core.Clash
import com.github.kr328.clash.design.ModeDesign
import com.github.kr328.clash.util.withClash
import kotlinx.coroutines.isActive
import kotlinx.coroutines.selects.select

class ModeActivity : BaseActivity<ModeDesign>() {
    override suspend fun main() {
        val mode = withClash { queryOverride(Clash.OverrideSlot.Persist).mode }

        val design = ModeDesign(
            this,
            mode,
        )

        setContentDesign(design)

        while (isActive) {
            select<Unit> {
                design.requests.onReceive {
                    when (it) {
                        is ModeDesign.Request.PatchMode -> {
                            withClash {
                                val o = queryOverride(Clash.OverrideSlot.Persist)

                                o.mode = it.mode

                                patchOverride(Clash.OverrideSlot.Persist, o)
                            }
                        }
                    }
                }
            }
        }
    }
}