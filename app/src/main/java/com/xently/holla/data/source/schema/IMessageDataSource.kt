package com.xently.holla.data.source.schema

import androidx.lifecycle.LiveData
import com.xently.holla.data.Result
import com.xently.holla.data.Source
import com.xently.holla.data.model.Contact
import com.xently.holla.data.model.Message

interface IMessageDataSource : IBaseDataSource {

    /**
     * returns an observable [LiveData] list of chats for [contact] if it's not null otherwise
     * a conversation(last message sent to each contact) list for the current user is returned
     */
    suspend fun getObservableMessages(contact: Contact): LiveData<List<Message>>
    suspend fun getObservableMessages(contactId: String): LiveData<List<Message>>

    /**
     * @param destination specifies where [message] is to be sent. `null` means send to both
     * (remote & cache)
     */
    suspend fun sendMessage(message: Message, destination: Source? = null): Result<Message?>

    /**
     * @param destination specifies where [messages] are to be sent. `null` means send to both
     * (remote & cache)
     */
    suspend fun sendMessages(
        messages: List<Message>,
        destination: Source? = null
    ): Result<List<Message>>

    /**
     * @param source specifies from where [message] is to be deleted. `null` means delete from
     * all(remote & cache) sources
     */
    suspend fun deleteMessage(message: Message, source: Source? = null): Result<Unit>

    /**
     * @param source specifies from where message with [id] is to be deleted. `null` means delete
     * from all(remote & cache) sources
     */
    suspend fun deleteMessage(id: String, source: Source? = null): Result<Unit>

    /**
     * @param source specifies from where messages with [contactId] as either the receiver or sender
     * is to be deleted. `null` means delete from all(remote & cache) sources
     */
    suspend fun deleteMessages(contactId: String, source: Source? = null): Result<Unit>
    suspend fun getMessages(contact: Contact): List<Message>
    suspend fun getMessages(contactId: String): List<Message>
    suspend fun getMessage(senderId: String, id: String): Message?
}