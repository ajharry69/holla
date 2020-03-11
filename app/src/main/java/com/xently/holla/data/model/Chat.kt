package com.xently.holla.data.model

import android.os.Parcel
import android.os.Parcelable

data class Chat(
    val uid: String,
    val id: String,
    val name: String,
    val messages: List<Message>
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createTypedArrayList(Message)?.toList() ?: emptyList()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uid)
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeTypedArray(messages.toTypedArray(), flags)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Chat> {
        override fun createFromParcel(parcel: Parcel): Chat = Chat(parcel)

        override fun newArray(size: Int): Array<Chat?> = arrayOfNulls(size)
    }
}