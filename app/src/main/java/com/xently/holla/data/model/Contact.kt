package com.xently.holla.data.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.xently.holla.utils.IData
import com.xently.holla.utils.JSON_CONVERTER
import com.xently.holla.utils.objectFromJson

@Entity
data class Contact(
    @PrimaryKey(autoGenerate = false) var id: String = "",
    var name: String? = null,
    var mobileNumber: String? = null,
    var profilePictureUrl: String? = null,
    var status: String? = "Hey there! I am using Holla",
    @Ignore var fcmToken: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.run {
            writeString(id)
            writeString(name)
            writeString(mobileNumber)
            writeString(profilePictureUrl)
            writeString(fcmToken)
        }
    }

    override fun describeContents(): Int = 0

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (mobileNumber?.hashCode() ?: 0)
        result = 31 * result + (profilePictureUrl?.hashCode() ?: 0)
        result = 31 * result + (status?.hashCode() ?: 0)
        result = 31 * result + (fcmToken?.hashCode() ?: 0)
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Contact

        if (id != other.id) return false
        if (name != other.name) return false
        if (mobileNumber != other.mobileNumber) return false
        if (profilePictureUrl != other.profilePictureUrl) return false
        if (status != other.status) return false
        if (fcmToken != other.fcmToken) return false

        return true
    }

    override fun toString(): String = JSON_CONVERTER.toJson(this)

    companion object CREATOR : Parcelable.Creator<Contact>, IData<Contact> {
        object Fields {
            const val ID = "id"
            const val NAME = "name"
            const val MOBILE = "mobileNumber"
            const val PICTURE = "profilePictureUrl"
            const val STATUS = "status"
            const val FCM_TOKEN = "fcmToken"
        }

        override fun createFromParcel(parcel: Parcel): Contact = Contact(parcel)

        override fun newArray(size: Int): Array<Contact?> = arrayOfNulls(size)

        @JvmStatic
        override fun fromJson(json: String?): Contact? = objectFromJson(json)
    }
}