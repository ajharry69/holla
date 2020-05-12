package com.xently.holla.adapters.list

import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RotateDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.recyclerview.widget.RecyclerView
import com.xently.holla.R
import com.xently.holla.adapters.diffutils.MessageDiffUtil
import com.xently.holla.data.model.Message
import com.xently.holla.data.model.Type
import com.xently.holla.databinding.MessageItemBinding
import com.xently.holla.databinding.MessageItemImageBinding
import com.xently.xui.adapters.list.ListAdapter

class MessageListAdapter : ListAdapter<Message, MessageViewHolder>(MessageDiffUtil()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            Type.Photo.ordinal -> MessageImageViewHolder(
                MessageItemImageBinding.inflate(
                    inflater,
                    parent,
                    false
                )
            )
            else -> MessageTextViewHolder(
                MessageItemBinding.inflate(
                    inflater,
                    parent,
                    false
                )
            )
        }
    }

    override fun getItemViewType(position: Int) = getItem(position).type.ordinal

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.bind(getItem(position))
    }
}

abstract class MessageViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bind(message: Message)

    protected fun setIsSender(
        isSender: Boolean,
        leftArrow: View,
        rightArrow: View,
        message: View,
        messageContainer: RelativeLayout
    ) {
        val color: Int
        if (isSender) {
            color = ContextCompat.getColor(itemView.context, R.color.material_green_300)
            leftArrow.visibility = View.GONE
            rightArrow.visibility = View.VISIBLE
            messageContainer.gravity = Gravity.END
        } else {
            color = ContextCompat.getColor(itemView.context, R.color.material_gray_300)
            leftArrow.visibility = View.VISIBLE
            rightArrow.visibility = View.GONE
            messageContainer.gravity = Gravity.START
        }
        (message.background as GradientDrawable).setColor(color)
        val colorFilter =
            BlendModeColorFilterCompat.createBlendModeColorFilterCompat(color, BlendModeCompat.SRC)
        (leftArrow.background as RotateDrawable).drawable?.colorFilter = colorFilter
        (rightArrow.background as RotateDrawable).drawable?.colorFilter = colorFilter
    }
}

class MessageTextViewHolder(val binding: MessageItemBinding) : MessageViewHolder(binding.root) {

    override fun bind(message: Message) {
        binding.run {
            chat = message
            setIsSender(message.isSender, leftArrow, rightArrow, this.message, messageContainer)
        }
    }
}

class MessageImageViewHolder(val binding: MessageItemImageBinding) :
    MessageViewHolder(binding.root) {

    override fun bind(message: Message) {
        binding.run {
            chat = message
            setIsSender(message.isSender, leftArrow, rightArrow, this.message, messageContainer)
        }
    }
}
