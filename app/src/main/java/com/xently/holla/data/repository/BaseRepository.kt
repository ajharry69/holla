@file:Suppress("MemberVisibilityCanBePrivate")

package com.xently.holla.data.repository

import android.content.Context
import android.provider.ContactsContract
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SnapshotMetadata
import com.google.firebase.storage.FirebaseStorage
import com.xently.holla.FBCollection.MESSAGES
import com.xently.holla.FBCollection.USERS
import com.xently.holla.Log
import com.xently.holla.data.Result
import com.xently.holla.data.model.Contact
import com.xently.holla.data.repository.schema.IBaseRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await

abstract class BaseRepository internal constructor(private val context: Context) : IBaseRepository {
    protected val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    protected val firebaseFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    protected val firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()

    protected val usersCollection: CollectionReference = firebaseFirestore.collection(USERS)
    protected val messagesCollection: CollectionReference = firebaseFirestore.collection(MESSAGES)

    protected val SnapshotMetadata.source: Source
        get() = if (hasPendingWrites()) Source.LOCAL else Source.REMOTE

    private val observableException = MutableLiveData<Exception>()

    override fun getObservableException(): LiveData<Exception> = observableException

    override fun getLocalContact(contact: Contact): Contact {
        var contact1 = contact
        context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            "${ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER} LIKE ?",
            arrayOf(contact1.mobileNumber),
            null
        )?.use {
            while (it.moveToNext()) {
                val name: String =
                    it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                contact1 = contact1.copy(name = name)
            }
        }

        return contact1
    }

    fun FirebaseUser?.getContact(fcmToken: String? = null): Contact? {
        val user = this ?: return null
        return Contact(user.uid, user.displayName, user.phoneNumber, fcmToken = fcmToken)
    }

    protected fun setException(ex: Exception?) {
        if (ex is CancellationException) return
        observableException.postValue(ex)
    }

    protected suspend fun <T> Task<T>.execute(onError: ((Exception) -> Unit)? = null): Result<T> {
        return try {
            Result.Success(await())
        } catch (ex: Exception) {
            Log.show("FCMService", ex.message, ex, Log.Type.ERROR) // TODO
            setException(ex)
            onError?.invoke(ex)
            Result.Error(ex)
        }
    }

    enum class Source { REMOTE, LOCAL }
}