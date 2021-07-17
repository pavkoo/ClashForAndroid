package com.github.kr328.clash.ucss

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.kr328.clash.R
import com.github.kr328.clash.common.Global
import com.github.kr328.clash.common.ucss.http.TradeService
import com.github.kr328.clash.core.model.Proxy
import com.github.kr328.clash.design.util.swapDataSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.CharacterIterator
import java.text.StringCharacterIterator

class AccountNodeAdapter(
    private val clicked: (TradeService) -> Unit,
) : RecyclerView.Adapter<AccountNodeAdapter.AccountHolder>() {
    class AccountHolder(val view: View) : RecyclerView.ViewHolder(view) {
        var check: TextView = view.findViewById(R.id.v_check)
        var title: TextView = view.findViewById(R.id.text_total)
        var subTitle: TextView = view.findViewById(R.id.text_dual)
        var progress: ProgressBar = view.findViewById(R.id.pb)
        var progressInfo: TextView = view.findViewById(R.id.tv_pb)
    }

    var service: List<TradeService> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountHolder {
        val layoutId = if (Global.ui.isTablet) R.layout.account_item_land else R.layout.account_item
        return AccountHolder(
            LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        )
    }

    override fun onBindViewHolder(holder: AccountHolder, position: Int) {
        val current = service[position]
        holder.check.isSelected = current.selected
        holder.subTitle.text = current.nextduedate
        holder.title.text = current.name
        holder.progress.progress = current.progress
        holder.progressInfo.text = humanReadableByteCountBin(current.remain)
        holder.view.apply {
            setOnClickListener {
                clicked(current)
            }
        }
    }

    override fun getItemCount(): Int {
        return service.size
    }

    suspend fun updateSource(proxies: List<TradeService>) {
        withContext(Dispatchers.Main) {
            swapDataSet(::service, proxies, false)
        }
    }

    private fun humanReadableByteCountSI(bytes: Long): String? {
        var bytes = bytes
        if (-1000 < bytes && bytes < 1000) {
            return "$bytes B"
        }
        val ci: CharacterIterator = StringCharacterIterator("kMGTPE")
        while (bytes <= -999950 || bytes >= 999950) {
            bytes /= 1000
            ci.next()
        }
        return java.lang.String.format("%.1f %cB", bytes / 1000.0, ci.current())
    }


    fun humanReadableByteCountBin(bytes: Long): String? {
        val absB = if (bytes == Long.MIN_VALUE) Long.MAX_VALUE else Math.abs(bytes)
        if (absB < 1024) {
            return "$bytes B"
        }
        var value = absB
        val ci: CharacterIterator = StringCharacterIterator("KMGTPE")
        var i = 40
        while (i >= 0 && absB > 0xfffccccccccccccL shr i) {
            value = value shr 10
            ci.next()
            i -= 10
        }
        value *= java.lang.Long.signum(bytes).toLong()
        return String.format("%.1f %ciB", value / 1024.0, ci.current())
    }
}
