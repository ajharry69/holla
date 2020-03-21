package com.xently.holla.data.model

import android.os.Parcel
import android.os.Parcelable

data class Contact(
    val id: String = "",
    val name: String? = null,
    val mobileNumber: String? = null,
    val profilePictureUrl: String? = null,
    val status: String? = "Hey there! I am using Holla"
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(mobileNumber)
        parcel.writeString(profilePictureUrl)
    }

    override fun describeContents(): Int = 0

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (mobileNumber?.hashCode() ?: 0)
        result = 31 * result + (profilePictureUrl?.hashCode() ?: 0)
        result = 31 * result + (status?.hashCode() ?: 0)
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

        return true
    }

    companion object CREATOR : Parcelable.Creator<Contact> {
        object Fields {
            const val ID = "id"
            const val NAME = "name"
            const val MOBILE = "mobileNumber"
            const val PICTURE = "profilePictureUrl"
            const val STATUS = "status"
        }

        override fun createFromParcel(parcel: Parcel): Contact = Contact(parcel)

        override fun newArray(size: Int): Array<Contact?> = arrayOfNulls(size)
    }
}