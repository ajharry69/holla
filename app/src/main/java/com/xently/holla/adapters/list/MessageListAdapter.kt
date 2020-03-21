package com.xently.holla.adapters.list

import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RotateDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.xently.holla.R
import com.xently.holla.adapters.list.MessageListAdapter.MessageViewHolder
import com.xently.holla.data.model.Chat
import com.xently.holla.databinding.MessageItemBinding
import com.xently.xui.adapters.list.ListAdapter

class MessageListAdapter : ListAdapter<Chat, MessageViewHolder>(ChatDiffUtil()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return MessageViewHolder(
            MessageItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.bind(getItem(position))
    }

    inner class MessageViewHolder(val binding: MessageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val mGreen300 = ContextCompat.getColor(itemView.context, R.color.material_green_300)
        private val mGray300 = ContextCompat.getColor(itemView.context, R.color.material_gray_300)

        private fun setIsSender(isSender: Boolean) {
            val color: Int
            if (isSender) {
                color = mGreen300
                binding.leftArrow.visibility = View.GONE
                binding.rightArrow.visibility = View.VISIBLE
                binding.messageContainer.gravity = Gravity.END
            } else {
                color = mGray300
                binding.leftArrow.visibility = View.VISIBLE
                binding.rightArrow.visibility = View.GONE
                binding.messageContainer.gravity = Gravity.START
            }
            (binding.message.background as GradientDrawable).setColor(color)
            val colorFilter = BlendModeColorFilterCompat
                .createBlendModeColorFilterCompat(color, BlendModeCompat.SRC)
            (binding.leftArrow.background as RotateDrawable).drawable?.colorFilter =
                colorFilter
            (binding.rightArrow.background as RotateDrawable).drawable?.colorFilter =
                colorFilter
        }

        fun bind(chat: Chat) {
            val isSender = FirebaseAuth.getInstance().currentUser?.uid == chat.senderId
            setIsSender(isSender)
        }
    }
}

