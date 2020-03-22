@file:Suppress("MemberVisibilityCanBePrivate")

package com.xently.holla.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SnapshotMetadata
import com.google.firebase.storage.FirebaseStorage
import com.xently.holla.FBCollection.MESSAGES
import com.xently.holla.FBCollection.USERS
import com.xently.holla.data.repository.schema.IBaseRepository

abstract class BaseRepository : IBaseRepository {
    protected val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    protected val firebaseFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    protected val firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()

    protected val usersCollection: CollectionReference = firebaseFirestore.collection(USERS)
    protected val messagesCollection: CollectionReference = firebaseFirestore.collection(MESSAGES)

    protected val SnapshotMetadata.source: Source
        get() = if (hasPendingWrites()) Source.LOCAL else Source.REMOTE

    private val observableException = MutableLiveData<Exception>()

    override fun getObservableException(): LiveData<Exception> = observableException

    protected fun setException(ex: Exception?) {
        observableException.postValue(ex)
    }

    enum class Source { REMOTE, LOCAL }
}

inline fun <reified T> QuerySnapshot.getObject(default: T): T {
    for (snapshot in this) {
        if (snapshot.exists()) return snapshot.toObject(T::class.java)
    }

    return default
}

inline fun <reified T> QuerySnapshot.getObjects(): List<T> {
    return toObjects(T::class.java)
}