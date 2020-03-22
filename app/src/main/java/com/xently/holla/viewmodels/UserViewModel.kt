package com.xently.holla.viewmodels

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.xently.holla.data.model.Contact
import com.xently.holla.data.repository.schema.IUserRepository

abstract class UserViewModel(private val repository: IUserRepository) : BaseViewModel(repository) {
    val contact: Contact?
        get() = repository.contact

    val observableContact = repository.observableContact

    fun setClient(contact: Contact?) = repository.setContact(contact)

    fun setClient(user: FirebaseUser?) = repository.setContact(user)

    fun addClient(contact: Contact) = repository.addContact(contact)

    fun addClient(user: FirebaseUser) = repository.addContact(user)

    fun updateClient(contact: Contact) = repository.updateContact(contact)

    fun signOut(): Task<Void> = repository.signOut()
}