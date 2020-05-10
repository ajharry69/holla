package com.xently.holla.data

import com.google.firebase.firestore.QuerySnapshot


inline fun <reified T> QuerySnapshot.getObject(default: T): T {
    for (snapshot in this) {
        if (snapshot.exists()) return snapshot.toObject(T::class.java)
    }

    return default
}

inline fun <reified T> QuerySnapshot.getObjects(): List<T> {
    return toObjects(T::class.java)
}