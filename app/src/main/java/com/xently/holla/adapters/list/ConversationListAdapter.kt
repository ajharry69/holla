package com.xently.holla.adapters.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.xently.holla.adapters.diffutils.ConversationDiffUtil
import com.xently.holla.adapters.list.ConversationListAdapter.ConversationViewHolder
import com.xently.holla.data.model.Conversation
import com.xently.holla.databinding.ConversationItemBinding
import com.xently.xui.adapters.list.ListAdapter

class ConversationListAdapter :
    ListAdapter<Conversation, ConversationViewHolder>(ConversationDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        return ConversationViewHolder(
            ConversationItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.bind(getItem(position))
    }

    inner class ConversationViewHolder internal constructor(private val binding: ConversationItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(conversation: Conversation) {
            binding.conversation = conversation
        }
    }
}