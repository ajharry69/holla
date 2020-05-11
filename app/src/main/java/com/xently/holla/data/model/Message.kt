package com.xently.holla.data.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Exclude

@Entity
data class Message(
    @PrimaryKey(autoGenerate = false) var id: String = "",
    var body: String? = null,
    var receiverId: String = "",
    var senderId: String = "", // Will be picked from the currently signed in user's ID
    var type: Type = Type.Text,
    var mediaUrl: String? = null,
    var sent: Boolean = true,
    var read: Boolean = false,
    var deleteFromSender: Boolean = false,
    var deleteFromReceiver: Boolean = false,
    var timeSent: Timestamp = Timestamp.now()
) : Parcelable {
    @get:Exclude
    val isSender: Boolean
        get() = FirebaseAuth.getInstance().currentUser?.uid == senderId

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

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.run {
            writeString(id)
            writeString(body)
            writeString(receiverId)
            writeString(senderId)
            writeString(mediaUrl)
            writeByte(if (sent) 1 else 0)
            writeByte(if (read) 1 else 0)
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
        result = 31 * result + sent.hashCode()
        result = 31 * result + read.hashCode()
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
        if (sent != other.sent) return false
        if (read != other.read) return false
        if (deleteFromSender != other.deleteFromSender) return false
        if (deleteFromReceiver != other.deleteFromReceiver) return false
        if (timeSent != other.timeSent) return false

        return true
    }

    companion object CREATOR : ChatCreator<Message> {

        override fun createFromParcel(parcel: Parcel): Message = Message(parcel)

        override fun newArray(size: Int): Array<Message?> = arrayOfNulls(size)
    }
}