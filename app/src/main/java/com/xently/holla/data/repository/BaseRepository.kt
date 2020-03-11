@file:Suppress("MemberVisibilityCanBePrivate")

package com.xently.holla.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.xently.holla.FBCollection.MESSAGES
import com.xently.holla.FBCollection.USERS

abstract class BaseRepository {
    protected val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    protected val firebaseFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    protected val firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()

    protected val usersCollection: CollectionReference = firebaseFirestore.collection(USERS)
    protected val messagesCollection: CollectionReference = firebaseFirestore.collection(MESSAGES)
}