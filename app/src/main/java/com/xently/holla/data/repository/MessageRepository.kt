package com.xently.holla.data.repository

import com.xently.holla.data.Result
import com.xently.holla.data.Source
import com.xently.holla.data.Source.LOCAL
import com.xently.holla.data.Source.REMOTE
import com.xently.holla.data.data
import com.xently.holla.data.listData
import com.xently.holla.data.model.Contact
import com.xently.holla.data.model.Message
import com.xently.holla.data.repository.schema.IMessageRepository
import com.xently.holla.data.source.schema.IMessageDataSource

class MessageRepository internal constructor(
    private val localDataSource: IMessageDataSource,
    private val remoteDataSource: IMessageDataSource
) : IMessageRepository {
    override fun getObservableException() = localDataSource.getObservableException()

    override fun getLocalContact(contact: Contact) = localDataSource.getLocalContact(contact)

    override suspend fun getObservableMessages(contact: Contact) =
        localDataSource.getObservableMessages(contact)

    override suspend fun getObservableMessages(contactId: String) =
        localDataSource.getObservableMessages(contactId)

    override suspend fun sendMessage(message: Message, destination: Source?): Result<Message?> {
        return when (destination) {
            REMOTE -> remoteDataSource.sendMessage(message, destination)
            LOCAL -> localDataSource.sendMessage(message, destination)
            null -> remoteDataSource.sendMessage(message, destination).apply {
                data?.let { sendMessage(it, LOCAL) }
            }
        }
    }

    override suspend fun sendMessages(
        messages: List<Message>,
        destination: Source?
    ): Result<List<Message>> {
        return when (destination) {
            REMOTE -> remoteDataSource.sendMessages(messages, destination)
            LOCAL -> localDataSource.sendMessages(messages, destination)
            null -> remoteDataSource.sendMessages(messages, destination).listData.run {
                sendMessages(this, LOCAL)
            }
        }
    }

    override suspend fun deleteMessage(message: Message, source: Source?): Result<Unit> {
        return when (source) {
            REMOTE -> remoteDataSource.deleteMessage(message, source)
            LOCAL -> localDataSource.deleteMessage(message, source)
            null -> remoteDataSource.deleteMessage(message, source).apply {
                deleteMessage(message, LOCAL)
            }
        }
    }

    override suspend fun deleteMessage(id: String, source: Source?): Result<Unit> {
        return when (source) {
            REMOTE -> remoteDataSource.deleteMessage(id, source)
            LOCAL -> localDataSource.deleteMessage(id, source)
            null -> remoteDataSource.deleteMessage(id, source).apply {
                deleteMessage(id, LOCAL)
            }
        }
    }

    override suspend fun deleteMessages(contactId: String, source: Source?): Result<Unit> {
        return when (source) {
            REMOTE -> remoteDataSource.deleteMessages(contactId, source)
            LOCAL -> localDataSource.deleteMessages(contactId, source)
            null -> remoteDataSource.deleteMessages(contactId, source).apply {
                deleteMessages(contactId, LOCAL)
            }
        }
    }

    override suspend fun getMessages(contact: Contact): List<Message> {
        return remoteDataSource.getMessages(contact).apply {
            sendMessages(this, LOCAL) // Cache messages
        }
    }

    override suspend fun getMessages(contactId: String): List<Message> {
        return remoteDataSource.getMessages(contactId).apply {
            sendMessages(this, LOCAL) // Cache messages
        }
    }

    override suspend fun getMessage(senderId: String, id: String): Message? {
        return remoteDataSource.getMessage(senderId, id)?.let {
            localDataSource.run {
                sendMessage(it, LOCAL)
                getMessage(senderId, id)
            }
        }
    }
}