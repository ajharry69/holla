package com.xently.holla.adapters.diffutils

import androidx.recyclerview.widget.DiffUtil
import com.xently.holla.data.model.Conversation

internal class ConversationDiffUtil : DiffUtil.ItemCallback<Conversation>() {
    override fun areItemsTheSame(oldItem: Conversation, newItem: Conversation): Boolean =
        oldItem.mateId == newItem.mateId

    override fun areContentsTheSame(oldItem: Conversation, newItem: Conversation): Boolean =
        oldItem == newItem
}