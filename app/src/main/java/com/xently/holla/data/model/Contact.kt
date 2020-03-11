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

    companion object CREATOR : Parcelable.Creator<Contact> {
        override fun createFromParcel(parcel: Parcel): Contact = Contact(parcel)

        override fun newArray(size: Int): Array<Contact?> = arrayOfNulls(size)
    }
}