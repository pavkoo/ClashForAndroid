package com.github.kr328.clash.ucss

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.kr328.clash.R
import com.github.kr328.clash.common.ucss.http.TradeService
import com.github.kr328.clash.core.model.Proxy
import com.github.kr328.clash.design.util.swapDataSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
        return AccountHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.account_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: AccountHolder, position: Int) {
        val current = service[position]
        holder.check.isSelected = current.selected
        holder.title.text = current.name
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

}
