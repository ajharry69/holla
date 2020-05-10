package com.xently.holla.data.source.schema

import androidx.lifecycle.LiveData
import com.google.android.gms.tasks.Task
import com.xently.holla.data.Result
import com.xently.holla.data.model.Message
import com.xently.holla.data.model.Contact

interface IMessageDataSource : IBaseDataSource {

    /**
     * returns an observable [LiveData] list of chats for [contact] if it's not null otherwise
     * a conversation(last message sent to each contact) list for the current user is returned
     */
    suspend fun getObservableChats(contact: Contact): LiveData<List<Message>>
    suspend fun sendMessage(message: Message): Result<Unit>
    suspend fun sendMessages(messages: List<Message>): Result<Unit>
    suspend fun deleteMessage(message: Message): Task<Void>?

    /**
     * returns an [LiveData] observable list of chats for [contact] if it's not null otherwise
     * a conversation(last message sent to each contact) list for the current user is returned
     */
    suspend fun getChats(contact: Contact): List<Message>
}