package com.xently.holla.data.repository

import com.google.android.gms.tasks.Task
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

    override suspend fun sendMessage(message: Message): Result<Message> {
        return remoteDataSource.sendMessage(message).apply {
            data?.let {
                localDataSource.sendMessage(it)
            }
        }
    }

    override suspend fun sendMessages(messages: List<Message>): Result<List<Message>> {
        return remoteDataSource.sendMessages(messages).listData.run {
            localDataSource.sendMessages(this)
        }
    }

    override suspend fun deleteMessage(message: Message, source: Source?): Task<Void>? {
        return when (source) {
            REMOTE -> remoteDataSource.deleteMessage(message, source)
            LOCAL -> localDataSource.deleteMessage(message, source)
            null -> remoteDataSource.deleteMessage(message, source)?.apply {
                localDataSource.deleteMessage(message, source)
            }
        }
    }

    override suspend fun deleteMessage(id: String, source: Source?): Result<Unit> {
        return when (source) {
            REMOTE -> remoteDataSource.deleteMessage(id, source)
            LOCAL -> localDataSource.deleteMessage(id, source)
            null -> remoteDataSource.deleteMessage(id, source).apply {
                localDataSource.deleteMessage(id, source)
            }
        }
    }

    override suspend fun deleteMessages(contactId: String, source: Source?): Result<Unit> {
        return when (source) {
            REMOTE -> remoteDataSource.deleteMessages(contactId, source)
            LOCAL -> localDataSource.deleteMessages(contactId, source)
            null -> remoteDataSource.deleteMessages(contactId, source).apply {
                localDataSource.deleteMessages(contactId, source)
            }
        }
    }

    override suspend fun getMessages(contact: Contact): List<Message> {
        val result = remoteDataSource.getMessages(contact)
        localDataSource.sendMessages(result) // Cache messages
        return result
    }

    override suspend fun getMessages(contactId: String): List<Message> {
        val result = remoteDataSource.getMessages(contactId)
        localDataSource.sendMessages(result) // Cache messages
        return result
    }
}