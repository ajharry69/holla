package com.xently.holla.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.xently.holla.data.model.Client
import com.xently.holla.data.repository.schema.IUserRepository

abstract class UserViewModel(private val repository: IUserRepository) : ViewModel() {
    val client: Client?
        get() = repository.client

    val observableClient: LiveData<Client>
        get() = repository.observableClient

    fun setClient(client: Client?) = repository.setClient(client)

    fun setClient(user: FirebaseUser?) = repository.setClient(user)

    fun addClient(client: Client): Task<Void> = repository.addClient(client)

    fun addClient(user: FirebaseUser): Task<Void> = repository.addClient(user)

    fun updateClient(client: Client): Task<Void> = repository.updateClient(client)

     fun signOut(): Task<Void> = repository.signOut()
}