package com.xently.holla.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.xently.holla.data.model.Contact
import com.xently.holla.data.repository.schema.IUserRepository

abstract class UserViewModel(private val repository: IUserRepository) : ViewModel() {
    val contact: Contact?
        get() = repository.contact

    val observableContact: LiveData<Contact>
        get() = repository.observableContact

    fun setClient(contact: Contact?) = repository.setContact(contact)

    fun setClient(user: FirebaseUser?) = repository.setContact(user)

    fun addClient(contact: Contact): Task<Void> = repository.addContact(contact)

    fun addClient(user: FirebaseUser): Task<Void> = repository.addContact(user)

    fun updateClient(contact: Contact): Task<Void> = repository.updateContact(contact)

     fun signOut(): Task<Void> = repository.signOut()
}