package com.xently.holla.data.repository.schema

import androidx.lifecycle.LiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.xently.holla.data.model.Contact

interface IUserRepository : IBaseRepository {
    val contact: Contact?

    val observableContact: LiveData<Contact>

    fun setContact(contact: Contact?)

    fun setContact(user: FirebaseUser?)

    fun addContact(contact: Contact): Task<Void>

    fun addContact(user: FirebaseUser): Task<Void>

    fun updateContact(contact: Contact): Task<Void>

    fun signOut(): Task<Void>
}