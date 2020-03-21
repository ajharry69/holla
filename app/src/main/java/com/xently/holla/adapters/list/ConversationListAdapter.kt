package com.xently.holla.adapters.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.xently.holla.adapters.list.ConversationListAdapter.ChatViewHolder
import com.xently.holla.data.model.Chat
import com.xently.holla.databinding.ConversationItemBinding
import com.xently.xui.adapters.list.ListAdapter

class ConversationListAdapter : ListAdapter<Chat, ChatViewHolder>(ChatDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        return ChatViewHolder(
            ConversationItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.bind(getItem(position))
    }

    inner class ChatViewHolder internal constructor(private val binding: ConversationItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(chat: Chat) {

        }
    }
}