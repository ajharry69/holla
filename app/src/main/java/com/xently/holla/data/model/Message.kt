package com.xently.holla.data.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp

data class Message(
    val id: String,
    val senderId: String,
    val body: String?,
    val type: Type,
    val isSent: Boolean,
    val isRead: Boolean,
    val timeSent: Timestamp
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString(),
        Type.valueOf(parcel.readString()!!),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readParcelable(Timestamp::class.java.classLoader)!!
    )

    /*sealed class Type {
        object Text : Type()
        sealed class Media : Type() {
            object Photo : Media()
            object Video : Media()
            object Document : Media()
        }
    }*/

    enum class Type {
        Text,
        Photo,
        Video,
        Document
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.run {
            writeString(id)
            writeString(senderId)
            writeString(body)
            writeString(type.name)
            writeByte(if (isSent) 1 else 0)
            writeByte(if (isRead) 1 else 0)
            writeParcelable(timeSent, flags)
        }
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Message> {
        override fun createFromParcel(parcel: Parcel): Message = Message(parcel)

        override fun newArray(size: Int): Array<Message?> = arrayOfNulls(size)
    }
}