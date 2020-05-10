package com.xently.holla.data.repository

import com.google.android.gms.tasks.Task
import com.xently.holla.data.Result
import com.xently.holla.data.model.Message
import com.xently.holla.data.model.Contact
import com.xently.holla.data.repository.schema.IMessageRepository
import com.xently.holla.data.source.schema.IMessageDataSource

class MessageRepository internal constructor(
    private val localDataSource: IMessageDataSource,
    private val remoteDataSource: IMessageDataSource
) : IMessageRepository {
    override fun getObservableException() = localDataSource.getObservableException()

    override fun getLocalContact(contact: Contact) = localDataSource.getLocalContact(contact)

    override suspend fun getObservableChats(contact: Contact) =
        localDataSource.getObservableChats(contact)

    override suspend fun sendMessage(message: Message): Result<Unit> {
        val result = remoteDataSource.sendMessage(message)
        return if (result is Result.Success) {
            localDataSource.sendMessage(message)
            Result.Success(Unit)
        } else result as Result.Error
    }

    override suspend fun sendMessages(messages: List<Message>): Result<Unit> {
        val result = remoteDataSource.sendMessages(messages)
        return if (result is Result.Success) {
            localDataSource.sendMessages(messages)
            Result.Success(Unit)
        } else result as Result.Error
    }

    override suspend fun deleteMessage(message: Message): Task<Void>? {
        val result = remoteDataSource.deleteMessage(message)
        if (result != null) localDataSource.deleteMessage(message)
        return result
    }

    override suspend fun getChats(contact: Contact): List<Message> {
        val result = remoteDataSource.getChats(contact)
        localDataSource.sendMessages(result) // Cache messages
        return result
    }
}