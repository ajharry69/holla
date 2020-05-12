package com.xently.holla.utils.databinding.adapters

import android.graphics.drawable.Drawable
import android.text.method.LinkMovementMethod
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide


@BindingAdapter("imageFromUrl", "placeholder", requireAll = false)
fun bindImageFromUrl(view: ImageView, imageUrl: String?, placeholder: Drawable?) {
    if (!imageUrl.isNullOrBlank()) {
        Glide.with(view.context)
            .load(imageUrl)
            .centerCrop()
            .placeholder(placeholder)
            .into(view)
    }
}

@BindingAdapter("renderHtml")
fun bindRenderHtml(view: TextView, description: String?) {
    with(view) {
        if (description != null) {
            linksClickable = true
            text = HtmlCompat.fromHtml(description, HtmlCompat.FROM_HTML_MODE_COMPACT)
            movementMethod = LinkMovementMethod.getInstance()
        } else text = description
    }
}