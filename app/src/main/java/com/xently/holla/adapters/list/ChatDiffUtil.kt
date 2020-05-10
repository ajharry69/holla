package com.xently.holla.adapters.list

import androidx.recyclerview.widget.DiffUtil
import com.xently.holla.data.model.Message

internal class ChatDiffUtil : DiffUtil.ItemCallback<Message>() {
    override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean = oldItem == newItem
}