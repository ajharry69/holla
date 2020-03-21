package com.xently.holla.data.repository.schema

import androidx.lifecycle.LiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.ListenerRegistration
import com.xently.holla.data.model.Chat
import com.xently.holla.data.model.Contact

interface IChatRepository {

    fun getObservableException(): LiveData<Exception>

    /**
     * returns an [LiveData] observable list of chats for [contact] if it's not null otherwise
     * a conversation(last message sent to each contact) list for the current user is returned
     */
    suspend fun getObservableConversations(contact: Contact?): LiveData<List<Chat>>
    suspend fun sendMessage(message: Chat): Task<Void>
    suspend fun deleteMessage(message: Chat): Task<Void>

    /**
     * returns an [LiveData] observable list of chats for [contact] if it's not null otherwise
     * a conversation(last message sent to each contact) list for the current user is returned
     */
    suspend fun getConversations(contact: Contact?): ListenerRegistration?
}