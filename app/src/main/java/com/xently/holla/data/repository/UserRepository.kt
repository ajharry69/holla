package com.xently.holla.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.xently.holla.FBCollection.USERS
import com.xently.holla.data.model.Client
import com.xently.holla.data.repository.schema.IUserRepository
import com.xently.holla.utils.Type
import com.xently.holla.utils.Type.CREATE
import com.xently.holla.utils.Type.UPDATE

class UserRepository internal constructor(private val context: Context) : IUserRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: CollectionReference = FirebaseFirestore.getInstance().collection(USERS)

    private val _observableClient: MutableLiveData<Client> = MutableLiveData()

    override fun setClient(client: Client?) {
        _observableClient.value = client
    }

    override fun setClient(user: FirebaseUser?) = setClient(user.asClient())

    override val client: Client?
        get() = auth.currentUser.asClient()

    override val observableClient: LiveData<Client>
        get() = _observableClient

    override fun addClient(client: Client): Task<Void> = saveClient(client)

    override fun addClient(user: FirebaseUser): Task<Void> {
        val client = user.asClient()!!
        return addClient(client)
    }

    override fun updateClient(client: Client): Task<Void> = saveClient(client, UPDATE)

    override fun signOut(): Task<Void> =
        AuthUI.getInstance().signOut(context).addOnCompleteListener {
            if (it.isSuccessful) setClient(auth.currentUser)
        }

    private fun FirebaseUser?.asClient(): Client? {
        val currentUser = this ?: return null
        return Client(currentUser.uid, currentUser.displayName, currentUser.phoneNumber)
    }

    private fun FirebaseUser?.asClientOrDefault(default: Client): Client = asClient() ?: default

    private fun saveClient(client: Client, type: Type = CREATE): Task<Void> = when (type) {
        CREATE -> db.document(client.id).set(client)
        UPDATE -> db.document(client.id).set(client, SetOptions.merge())
    }.addOnSuccessListener {
        setClient(auth.currentUser)
    }
}