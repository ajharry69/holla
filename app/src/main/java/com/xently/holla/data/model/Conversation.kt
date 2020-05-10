package com.xently.holla.data.model

import android.os.Parcel
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude

@Entity
data class Conversation(
    @PrimaryKey(autoGenerate = false) override val id: String = "",
    override val body: String? = null,
    override val receiverId: String = "",
    override val senderId: String = "", // Will be picked from the currently signed in user's ID
    override val type: Type = Type.Text,
    override val mediaUrl: String? = null,
    override val sent: Boolean = true,
    override val read: Boolean = false,
    override val deleteFromSender: Boolean = false,
    override val deleteFromReceiver: Boolean = false,
    override val timeSent: Timestamp = Timestamp.now(),
    @Ignore @get:Exclude override val sender: Contact = Contact(id = senderId),
    @Ignore @get:Exclude override val receiver: Contact = Contact(id = receiverId)
) : Chat(
    id,
    body,
    receiverId,
    senderId,
    type,
    mediaUrl,
    sent,
    read,
    deleteFromSender,
    deleteFromReceiver,
    timeSent,
    sender,
    receiver
) {

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
        parcel.readParcelable(Contact::class.java.classLoader)!!,
        parcel.readParcelable(Contact::class.java.classLoader)!!
    )

    companion object CREATOR : ChatCreator<Conversation> {

        override fun createFromParcel(parcel: Parcel): Conversation = Conversation(parcel)

        override fun newArray(size: Int): Array<Conversation?> = arrayOfNulls(size)
    }
}