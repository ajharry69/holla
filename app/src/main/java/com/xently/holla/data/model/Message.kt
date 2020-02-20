package com.xently.holla.data.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp

data class Message(
    val id: Int,
    val sender: Client,
    val sentAt: Timestamp,
    val type: Type,
    val isSent: Boolean,
    val isRead: Boolean
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        TODO("sender"),
        parcel.readParcelable(Timestamp::class.java.classLoader)!!,
        TODO("type"),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte()
    )

    sealed class Type {
        object Text : Type()
        sealed class Media : Type() {
            object Photo : Media()
            object Video : Media()
            object Document : Media()
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeParcelable(sentAt, flags)
        parcel.writeByte(if (isSent) 1 else 0)
        parcel.writeByte(if (isRead) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Message> {
        override fun createFromParcel(parcel: Parcel): Message {
            return Message(parcel)
        }

        override fun newArray(size: Int): Array<Message?> {
            return arrayOfNulls(size)
        }
    }
}