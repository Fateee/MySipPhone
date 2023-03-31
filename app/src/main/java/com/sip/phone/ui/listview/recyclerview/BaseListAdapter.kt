package com.sip.phone.ui.listview.recyclerview

import android.content.Context
import android.os.Parcelable
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sip.phone.ui.listview.customview.BaseCustomView
import com.sip.phone.ui.listview.customview.ICustomView
import java.io.Serializable

abstract class BaseListAdapter : RecyclerView.Adapter<BaseViewHolder>() {

    var data : ArrayList<Serializable> = arrayListOf()
    var itemClickCallback : OnItemClickCallback? = null
    var itemLongClickCallback : OnItemLongClickCallback? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(createItemView(parent.context) as ICustomView<Serializable>)
    }

    abstract fun createItemView(context: Context): View

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(data[position],position,data.size)
        if (holder.itemView is BaseCustomView<*, *>) {
            holder.itemView.setActionListener { action, view, viewModel ->
                itemClickCallback?.onItemClick(view,viewModel,position)
            }
            holder.itemView.setOnLongClickListener { v ->
                itemLongClickCallback?.onItemLongClick(v, data[position], position)
                false
            }
        }
    }

}

interface OnItemClickCallback {
    fun onItemClick(itemView: View, data: Serializable, position: Int)
}
interface OnItemLongClickCallback {
    fun onItemLongClick(itemView: View?, data: Serializable, position: Int)
}