package com.xently.holla.adapters.list

import androidx.recyclerview.widget.DiffUtil
import com.xently.holla.data.model.Chat

internal class ChatDiffUtil : DiffUtil.ItemCallback<Chat>() {
    override fun areItemsTheSame(oldItem: Chat, newItem: Chat): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean = oldItem == newItem
}