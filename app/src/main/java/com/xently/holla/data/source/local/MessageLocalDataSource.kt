package com.xently.holla.data.source.local

import android.content.Context
import com.google.android.gms.tasks.Task
import com.xently.holla.data.Result
import com.xently.holla.data.Source
import com.xently.holla.data.model.Contact
import com.xently.holla.data.model.Conversation
import com.xently.holla.data.model.Message
import com.xently.holla.data.model.Type
import com.xently.holla.data.source.schema.IConversationDataSource
import com.xently.holla.data.source.schema.IMessageDataSource
import com.xently.holla.data.source.schema.dao.MessageDao

class MessageLocalDataSource internal constructor(
    context: Context,
    private val dao: MessageDao,
    private val localDataSource: IConversationDataSource?
) : BaseLocalDataSource(context), IMessageDataSource {
    override suspend fun getObservableMessages(contact: Contact) =
        dao.getObservableMessages(contact.id)

    override suspend fun getObservableMessages(contactId: String) =
        dao.getObservableMessages(contactId)

    override suspend fun sendMessage(message: Message): Result<Message> {
        dao.saveMessage(message)
        localDataSource?.saveConversation(
            Conversation(
                id = message.id,
                body = message.body,
                receiverId = message.receiverId,
                senderId = message.senderId,
                type = Type.valueOf(message.type.name),
                mediaUrl = message.mediaUrl,
                sent = message.sent,
                read = message.read,
                deleteFromSender = message.deleteFromSender,
                deleteFromReceiver = message.deleteFromReceiver,
                timeSent = message.timeSent
            )
        )
        return Result.Success(message)
    }

    override suspend fun sendMessages(messages: List<Message>): Result<List<Message>> {
        dao.saveMessages(messages)
        return Result.Success(messages)
    }

    override suspend fun deleteMessage(message: Message, source: Source?): Task<Void>? {
        dao.deleteMessage(message)
        return null
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
}