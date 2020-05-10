package com.xently.holla.data.source.remote

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.SetOptions
import com.google.firebase.iid.FirebaseInstanceId
import com.xently.holla.data.Result
import com.xently.holla.data.model.Contact
import com.xently.holla.data.source.schema.IUserDataSource
import com.xently.holla.utils.Type
import kotlinx.coroutines.tasks.await

class UserRemoteDataSource internal constructor(private val context: Context) :
    BaseRemoteDataSource(context), IUserDataSource {

    private val _observableContact = MutableLiveData<Contact>(firebaseAuth.currentUser.getContact())

    override val observableContact: LiveData<Contact>
        get() = _observableContact

    override suspend fun saveContact(type: Type): Result<Unit> {
        val user = firebaseAuth.currentUser
        val token = FirebaseInstanceId.getInstance().instanceId.await()
        val contact = user.getContact(token.token)!!

        val task = when (type) {
            Type.CREATE -> usersCollection.document(contact.id).set(contact)
            Type.UPDATE -> usersCollection.document(contact.id).set(contact, SetOptions.merge())
        }.execute()
        setContact(firebaseAuth.currentUser)
        return if (task is Result.Success) {
            Result.Success(Unit)
        } else task as Result.Error
    }

    override suspend fun signOut(): Result<Unit> {
        val task = AuthUI.getInstance().signOut(context).execute()
        setContact(firebaseAuth.currentUser)
        updateFCMToken(null)
        return if (task is Result.Success) {
            Result.Success(Unit)
        } else task as Result.Error
    }

    override suspend fun updateFCMToken(token: String?) {
        val userId = firebaseAuth.currentUser?.uid ?: return
        usersCollection.document(userId).update(Contact.CREATOR.Fields.FCM_TOKEN, token)
    }

    private fun FirebaseUser?.getContact(fcmToken: String? = null): Contact? {
        val user = this ?: return null
        return Contact(user.uid, user.displayName, user.phoneNumber, fcmToken = fcmToken)
    }

    private fun setContact(user: FirebaseUser?) {
        _observableContact.value = user.getContact()
    }
}