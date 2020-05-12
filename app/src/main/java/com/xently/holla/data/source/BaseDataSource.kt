package com.xently.holla.data.source

import android.content.Context
import android.provider.ContactsContract.CommonDataKinds
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SnapshotMetadata
import com.google.firebase.storage.FirebaseStorage
import com.xently.holla.FBCollection
import com.xently.holla.FBCollection.MESSAGES
import com.xently.holla.Log
import com.xently.holla.data.Result
import com.xently.holla.data.Source
import com.xently.holla.data.model.Contact
import com.xently.holla.data.source.schema.IBaseDataSource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await

abstract class BaseDataSource internal constructor(private val context: Context) : IBaseDataSource {

    private val observableException = MutableLiveData<Exception>()

    private val firebaseFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val chatsCollection = firebaseFirestore.collection(FBCollection.CHATS)

    protected val firebaseAuth = FirebaseAuth.getInstance()
    protected val firebaseStorage = FirebaseStorage.getInstance()
    protected val usersCollection = firebaseFirestore.collection(FBCollection.USERS)

    protected val SnapshotMetadata.source: Source
        get() = if (hasPendingWrites()) Source.LOCAL else Source.REMOTE

    /**
     * @throws AssertionError if [FirebaseAuth.getCurrentUser] returns `null`
     */
    @Throws(AssertionError::class)
    protected fun getMyMessagesCollection(contactId: String): CollectionReference {
        return chatsCollection.document(firebaseAuth.currentUser!!.uid)
            .collection(MESSAGES).document(contactId).collection(MESSAGES)
    }

    /**
     * @throws AssertionError if [FirebaseAuth.getCurrentUser] returns `null`
     */
    @Throws(AssertionError::class)
    protected fun getMyConversationsCollection(): CollectionReference {
        return chatsCollection.document(firebaseAuth.currentUser!!.uid)
            .collection(FBCollection.CONVERSATIONS)
    }

    /**
     * @throws AssertionError if [FirebaseAuth.getCurrentUser] returns `null`
     */
    @Throws(AssertionError::class)
    protected fun getMyUnreadCountsCollection(): CollectionReference {
        return chatsCollection.document(firebaseAuth.currentUser!!.uid)
            .collection(FBCollection.UNREAD_COUNT)
    }

    @Throws(AssertionError::class)
    protected fun getChatMateMessagesCollection(mateId: String): CollectionReference {
        return chatsCollection.document(mateId).collection(MESSAGES)
    }

    @Throws(AssertionError::class)
    protected fun getChatMateConversationsCollection(mateId: String): CollectionReference {
        return chatsCollection.document(mateId).collection(FBCollection.CONVERSATIONS)
    }

    @Throws(AssertionError::class)
    protected fun getChatMateUnreadCountsCollection(mateId: String): CollectionReference {
        return chatsCollection.document(mateId).collection(FBCollection.UNREAD_COUNT)
    }

    protected fun setException(ex: Exception?) {
        if (ex is CancellationException) return
        observableException.postValue(ex)
    }

    protected suspend fun <T> Task<T>.execute(onError: ((Exception) -> Unit)? = null): Result<T> {
        return try {
            Result.Success(await())
        } catch (ex: Exception) {
            Log.show(BaseDataSource::class.java.simpleName, ex.message, ex, Log.Type.ERROR)
            setException(ex)
            onError?.invoke(ex)
            Result.Error(ex)
        }
    }

    protected suspend fun <T> Task<T>.executeTask(onError: ((Exception) -> Unit)? = null): T? {
        return try {
            await()
        } catch (ex: Exception) {
            Log.show(BaseDataSource::class.java.simpleName, ex.message, ex, Log.Type.ERROR)
            setException(ex)
            onError?.invoke(ex)
            null
        }
    }

    override fun getObservableException(): LiveData<Exception> = observableException

    override fun getLocalContact(contact: Contact): Contact {
        var contact1 = contact
        context.contentResolver.query(
            CommonDataKinds.Phone.CONTENT_URI,
            null,
            "${CommonDataKinds.Phone.NORMALIZED_NUMBER} LIKE ?",
            arrayOf(contact1.mobileNumber),
            null
        )?.use {
            while (it.moveToNext()) {
                val name = it.getString(it.getColumnIndex(CommonDataKinds.Phone.DISPLAY_NAME)).run {
                    // Use mobile number as the name if contact is not been saved!
                    if (isNullOrBlank()) contact.mobileNumber else this
                }
                contact1 = contact1.copy(name = name)
            }
        }

        return contact1
    }
}