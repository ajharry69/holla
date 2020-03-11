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
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.xently.holla.R
import com.xently.holla.data.model.Message
import com.xently.holla.databinding.MessageItemBinding

class MessageListAdapter(options: FirestoreRecyclerOptions<Message>) :
    FirestoreRecyclerAdapter<Message, MessageListAdapter.MessageViewHolder>(options) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return MessageViewHolder(
            MessageItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int, model: Message) {
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

        fun bind(message: Message) {
            val isSender = FirebaseAuth.getInstance().currentUser?.uid == message.senderId
            setIsSender(isSender)
        }
    }
}