package com.xently.holla.data.model

import android.os.Parcel
import android.os.Parcelable

data class Client(
    val id: String,
    val name: String?,
    val mobileNumber: String?,
    val profilePictureUrl: String? = null
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

    companion object CREATOR : Parcelable.Creator<Client> {
        override fun createFromParcel(parcel: Parcel): Client = Client(parcel)

        override fun newArray(size: Int): Array<Client?> = arrayOfNulls(size)
    }
}