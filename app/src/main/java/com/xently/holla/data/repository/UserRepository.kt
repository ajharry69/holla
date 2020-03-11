package com.xently.holla.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.SetOptions
import com.xently.holla.data.model.Contact
import com.xently.holla.data.repository.schema.IUserRepository
import com.xently.holla.utils.Type
import com.xently.holla.utils.Type.CREATE
import com.xently.holla.utils.Type.UPDATE

class UserRepository internal constructor(private val context: Context) : BaseRepository(),
    IUserRepository {

    private val _observableContact: MutableLiveData<Contact> = MutableLiveData()

    override fun setContact(contact: Contact?) {
        _observableContact.value = contact
    }

    override fun setContact(user: FirebaseUser?) = setContact(user.asClient())

    override val contact: Contact?
        get() = firebaseAuth.currentUser.asClient()

    override val observableContact: LiveData<Contact>
        get() = _observableContact

    override fun addContact(contact: Contact): Task<Void> = saveClient(contact)

    override fun addContact(user: FirebaseUser): Task<Void> {
        val client = user.asClient()!!
        return addContact(client)
    }

    override fun updateContact(contact: Contact): Task<Void> = saveClient(contact, UPDATE)

    override fun signOut(): Task<Void> =
        AuthUI.getInstance().signOut(context).addOnCompleteListener {
            if (it.isSuccessful) setContact(firebaseAuth.currentUser)
        }

    private fun FirebaseUser?.asClient(): Contact? {
        val currentUser = this ?: return null
        return Contact(currentUser.uid, currentUser.displayName, currentUser.phoneNumber)
    }

    private fun FirebaseUser?.asClientOrDefault(default: Contact): Contact = asClient() ?: default

    private fun saveClient(contact: Contact, type: Type = CREATE): Task<Void> = when (type) {
        CREATE -> usersCollection.document(contact.id).set(contact)
        UPDATE -> usersCollection.document(contact.id).set(contact, SetOptions.merge())
    }.addOnSuccessListener {
        setContact(firebaseAuth.currentUser)
    }
}