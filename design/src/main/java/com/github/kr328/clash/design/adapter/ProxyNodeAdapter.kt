package com.github.kr328.clash.design.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.kr328.clash.core.model.Proxy
import com.github.kr328.clash.design.component.ProxyNodeView
import com.github.kr328.clash.design.util.swapDataSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProxyNodeAdapter(
    private val clicked: (String) -> Unit,
) : RecyclerView.Adapter<ProxyNodeAdapter.Holder>() {
    class Holder(val view: ProxyNodeView) : RecyclerView.ViewHolder(view)

    var states: List<Proxy> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(ProxyNodeView(parent.context))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val current = states[position]

        holder.view.apply {
            setSource(current)

            setOnClickListener {
                clicked(current.name)
            }
        }
    }

    override fun getItemCount(): Int {
        return states.size
    }


    suspend fun updateSource(proxies: List<Proxy>) {
        withContext(Dispatchers.Main) {
            swapDataSet(::states, proxies, false)
        }
    }

}
