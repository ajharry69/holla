package com.xently.holla.data.source.local

import android.content.Context
import com.xently.holla.data.Result
import com.xently.holla.data.Source
import com.xently.holla.data.model.Contact
import com.xently.holla.data.model.Message
import com.xently.holla.data.source.schema.IMessageDataSource
import com.xently.holla.data.source.schema.dao.MessageDao

class MessageLocalDataSource internal constructor(
    context: Context,
    private val dao: MessageDao
) : BaseLocalDataSource(context), IMessageDataSource {
    override suspend fun getObservableMessages(contact: Contact) =
        dao.getObservableMessages(contact.id)

    override suspend fun getObservableMessages(contactId: String) =
        dao.getObservableMessages(contactId)

    override suspend fun sendMessage(
        message: Message,
        destination: Source?
    ): Result<Message?> {
        dao.saveMessage(message)
        return Result.Success(getMessage(message.senderId, message.id))
    }

    override suspend fun sendMessages(
        messages: List<Message>,
        destination: Source?
    ): Result<List<Message>> {
        dao.saveMessages(messages)
        return Result.Success(messages)
    }

    override suspend fun deleteMessage(message: Message, source: Source?): Result<Unit> {
        dao.deleteMessage(message)
        return Result.Success(Unit)
    }

    override suspend fun deleteMessage(id: String, source: Source?): Result<Unit> {
        dao.deleteMessage(id)
        return Result.Success(Unit)
    }

    override suspend fun deleteMessages(contactId: String, source: Source?): Result<Unit> {
        dao.deleteMessages(contactId)
        return Result.Success(Unit)
    }

    override suspend fun getMessages(contact: Contact) = dao.getMessages(contact.id)

    override suspend fun getMessages(contactId: String) = dao.getMessages(contactId)

    override suspend fun getMessage(senderId: String, id: String) = dao.getMessage(id)
}