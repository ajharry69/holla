package com.xently.holla.data.model

import android.os.Parcel
import android.os.Parcelable

data class Chat(val id: Int, val name: String, val messages: List<Message>) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.createTypedArrayList(Message)?.toList() ?: emptyList()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeTypedArray(messages.toTypedArray(), flags)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Chat> {
        override fun createFromParcel(parcel: Parcel): Chat = Chat(parcel)

        override fun newArray(size: Int): Array<Chat?> = arrayOfNulls(size)
    }

}