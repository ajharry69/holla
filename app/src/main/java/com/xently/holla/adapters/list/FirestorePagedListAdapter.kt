package com.xently.holla.adapters.list

import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.xently.xui.adapters.list.OnListItemClickListener

abstract class FirestorePagedListAdapter<M, VH : RecyclerView.ViewHolder>(options: FirestorePagingOptions<M>) :
    FirestorePagingAdapter<M, VH>(options) {

    var listItemClickListener: OnListItemClickListener<M>? = null

    override fun onBindViewHolder(holder: VH, position: Int, model: M) {
        with(holder.itemView) {
            setOnClickListener {
                listItemClickListener?.onListItemClick(model, it)
            }
            setOnLongClickListener {
                listItemClickListener?.onListItemLongClick(model, it) ?: false
            }
        }
    }
}

