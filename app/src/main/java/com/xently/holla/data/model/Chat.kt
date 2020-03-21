package com.xently.holla.data.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp

data class Chat(
    val id: String = "",
    val body: String? = null,
    val receiverId: String = "",
    val senderId: String = "", // Will be picked from the currently signed in user's ID
    val type: Type = Type.Text,
    val mediaUrl: String? = null,
    val isSent: Boolean = true,
    val isRead: Boolean = false,
    val deleteFromSender: Boolean = false,
    val deleteFromReceiver: Boolean = false,
    val timeSent: Timestamp = Timestamp.now()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString(),
        parcel.readString()!!,
        parcel.readString()!!,
        Type.valueOf(parcel.readString()!!),
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
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
            writeString(body)
            writeString(receiverId)
            writeString(senderId)
            writeString(type.name)
            writeString(mediaUrl)
            writeByte(if (isSent) 1 else 0)
            writeByte(if (isRead) 1 else 0)
            writeByte(if (deleteFromSender) 1 else 0)
            writeByte(if (deleteFromReceiver) 1 else 0)
            writeParcelable(timeSent, flags)
        }
    }

    override fun describeContents(): Int = 0

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + (body?.hashCode() ?: 0)
        result = 31 * result + receiverId.hashCode()
        result = 31 * result + senderId.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + mediaUrl.hashCode()
        result = 31 * result + isSent.hashCode()
        result = 31 * result + isRead.hashCode()
        result = 31 * result + deleteFromSender.hashCode()
        result = 31 * result + deleteFromReceiver.hashCode()
        result = 31 * result + timeSent.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Chat

        if (id != other.id) return false
        if (body != other.body) return false
        if (receiverId != other.receiverId) return false
        if (senderId != other.senderId) return false
        if (type != other.type) return false
        if (mediaUrl != other.mediaUrl) return false
        if (isSent != other.isSent) return false
        if (isRead != other.isRead) return false
        if (deleteFromSender != other.deleteFromSender) return false
        if (deleteFromReceiver != other.deleteFromReceiver) return false
        if (timeSent != other.timeSent) return false

        return true
    }

    companion object CREATOR : Parcelable.Creator<Chat> {
        object Fields {
            const val ID = "id"
            const val BODY = "body"
            const val RECEIVER = "receiverId"
            const val SENDER = "senderId"
            const val TYPE = "type"
            const val MEDIA_URL = "mediaUrl"
            const val SENT = "isSent"
            const val READ = "isRead"
            const val DELETE_FROM_SENDER = "deleteFromSender"
            const val DELETE_FROM_RECEIVER = "deleteFromReceiver"
            const val TIME_SENT = "timeSent"
        }

        override fun createFromParcel(parcel: Parcel): Chat = Chat(parcel)

        override fun newArray(size: Int): Array<Chat?> = arrayOfNulls(size)
    }
}