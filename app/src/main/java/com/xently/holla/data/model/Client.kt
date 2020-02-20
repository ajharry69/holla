package com.xently.holla.data.model

import com.google.firebase.Timestamp

data class Client(
    val id: String,
    val name: String,
    val mobileNumber: String,
    val email: String,
    val dateJoined: Timestamp
) {
}