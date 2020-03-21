package com.xently.holla.adapters.list

import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.xently.xui.adapters.list.OnListItemClickListener

abstract class FirestoreListAdapter<M, VH : RecyclerView.ViewHolder>(options: FirestoreRecyclerOptions<M>) :
    FirestoreRecyclerAdapter<M, VH>(options) {

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