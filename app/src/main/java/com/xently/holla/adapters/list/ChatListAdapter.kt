package com.xently.holla.adapters.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.xently.holla.R
import com.xently.holla.data.model.Chat
import com.xently.holla.databinding.ChatItemBinding
import com.xently.holla.databinding.ContactItemBinding

class ChatListAdapter(options: FirestoreRecyclerOptions<Chat>) :
    FirestoreRecyclerAdapter<Chat, ChatListAdapter.ChatViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        return ChatViewHolder(
            ChatItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int, model: Chat) {
        holder.bind(getItem(position))
    }

    inner class ChatViewHolder internal constructor(private val binding: ChatItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(chat: Chat) {

        }
    }
}