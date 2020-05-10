package com.xently.holla.data.source.local

import android.content.Context
import com.google.android.gms.tasks.Task
import com.xently.holla.data.Result
import com.xently.holla.data.model.Message
import com.xently.holla.data.model.Contact
import com.xently.holla.data.model.Conversation
import com.xently.holla.data.model.Chat
import com.xently.holla.data.source.schema.IMessageDataSource
import com.xently.holla.data.source.schema.IConversationDataSource
import com.xently.holla.data.source.schema.dao.MessageDao

class MessageLocalDataSource internal constructor(
    context: Context,
    private val dao: MessageDao,
    private val localDataSource: IConversationDataSource?
) : BaseLocalDataSource(context), IMessageDataSource {
    override suspend fun getObservableChats(contact: Contact) = dao.getObservableMessages(contact.id)

    override suspend fun sendMessage(message: Message): Result<Unit> {
        dao.saveMessage(message)
        localDataSource?.saveConversation(
            Conversation(
                id = message.id,
                body = message.body,
                receiverId = message.receiverId,
                senderId = message.senderId,
                type = Chat.Type.valueOf(message.type.name),
                mediaUrl = message.mediaUrl,
                sent = message.sent,
                read = message.read,
                deleteFromSender = message.deleteFromSender,
                deleteFromReceiver = message.deleteFromReceiver,
                timeSent = message.timeSent,
                sender = message.sender,
                receiver = message.receiver
            )
        )
        return Result.Success(Unit)
    }

    override suspend fun sendMessages(messages: List<Message>): Result<Unit> {
        // TODO: Delete conversation if all messages in a chat have been deleted
        dao.saveMessages(messages)
        return Result.Success(Unit)
    }

    override suspend fun deleteMessage(message: Message): Task<Void>? {
        // TODO: Use the NEW last sent text as the new conversation if the deleted message was the latest
        dao.deleteMessage(message)
        return null
    }

    override suspend fun getChats(contact: Contact) = dao.getMessages(contact.id)
}