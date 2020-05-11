package com.xently.holla.data.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp

@Entity
data class Conversation(
    val id: String = "",
    val body: String? = null,
    val receiverId: String = "",
    val senderId: String = "", // Will be picked from the currently signed in user's ID
    val type: Type = Type.Text,
    val mediaUrl: String? = null,
    val sent: Boolean = true,
    val read: Boolean = false,
    val deleteFromSender: Boolean = false,
    val deleteFromReceiver: Boolean = false,
    val timeSent: Timestamp = Timestamp.now(),
    @PrimaryKey(autoGenerate = false) val mateId: String = "",
    @Embedded(prefix = "mate_") val mate: Contact? = null
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
        parcel.readParcelable(Timestamp::class.java.classLoader)!!,
        parcel.readString()!!,
        parcel.readParcelable(Contact::class.java.classLoader)
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
            writeString(mateId)
            writeParcelable(mate, flags)
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
        result = 31 * result + mateId.hashCode()
        result = 31 * result + mate.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Conversation

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
        if (mateId != other.mateId) return false
        if (mate != other.mate) return false

        return true
    }

    companion object CREATOR : ChatCreator<Conversation> {

        override fun createFromParcel(parcel: Parcel) = Conversation(parcel)

        override fun newArray(size: Int): Array<Conversation?> = arrayOfNulls(size)
    }
}