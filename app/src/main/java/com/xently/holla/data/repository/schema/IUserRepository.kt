package com.xently.holla.data.repository.schema

import androidx.lifecycle.LiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.xently.holla.data.model.Client

interface IUserRepository {
    val client: Client?

    val observableClient: LiveData<Client>

    fun setClient(client: Client?)

    fun setClient(user: FirebaseUser?)

    fun addClient(client: Client): Task<Void>

    fun addClient(user: FirebaseUser): Task<Void>

    fun updateClient(client: Client): Task<Void>

    fun signOut(): Task<Void>
}