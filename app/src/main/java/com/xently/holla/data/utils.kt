package com.xently.holla.data

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot

enum class Source { REMOTE, LOCAL }

inline fun <reified T> QuerySnapshot.getObject(default: T): T {
    for (snapshot in this) {
        if (snapshot.exists()) return snapshot.toObject(T::class.java)
    }

    return default
}

inline fun <reified T> QuerySnapshot.getObjects(): List<T> {
    return toObjects(T::class.java)
}

inline fun <reified T> DocumentSnapshot.getObject(): T? {
    return toObject(T::class.java)
}