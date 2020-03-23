package com.xently.holla.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.SetOptions
import com.google.firebase.iid.FirebaseInstanceId
import com.xently.holla.data.model.Contact
import com.xently.holla.data.model.Contact.CREATOR.Fields
import com.xently.holla.data.repository.schema.IUserRepository
import com.xently.holla.utils.Type
import com.xently.holla.utils.Type.CREATE
import com.xently.holla.utils.Type.UPDATE
import kotlinx.coroutines.tasks.await

class UserRepository internal constructor(private val context: Context) : BaseRepository(context),
    IUserRepository {

    private val _observableContact = MutableLiveData<Contact>(firebaseAuth.currentUser.getContact())

    override val observableContact: LiveData<Contact>
        get() = _observableContact

    override suspend fun saveContact(type: Type): Result<Void> {
        val user = firebaseAuth.currentUser
        val token = FirebaseInstanceId.getInstance().instanceId.await()
        val contact = user.getContact(token.token)!!

        val task = when (type) {
            CREATE -> usersCollection.document(contact.id).set(contact)
            UPDATE -> usersCollection.document(contact.id).set(contact, SetOptions.merge())
        }.execute()
        setContact(firebaseAuth.currentUser)
        return task
    }

    override suspend fun signOut(): Result<Void> {
        val task = AuthUI.getInstance().signOut(context).execute()
        setContact(firebaseAuth.currentUser)
        updateFCMToken(null)
        return task
    }

    override suspend fun updateFCMToken(token: String?) {
        val userId = firebaseAuth.currentUser?.uid ?: return
        usersCollection.document(userId).update(Fields.FCM_TOKEN, token)
    }

    private fun setContact(user: FirebaseUser?) {
        _observableContact.value = user.getContact()
    }
}