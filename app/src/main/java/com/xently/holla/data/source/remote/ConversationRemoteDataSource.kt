package com.xently.holla.data.source.remote

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.xently.holla.Log
import com.xently.holla.data.Result
import com.xently.holla.data.getObject
import com.xently.holla.data.model.ChatCreator
import com.xently.holla.data.model.Contact
import com.xently.holla.data.model.Conversation
import com.xently.holla.data.source.schema.IConversationDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ConversationRemoteDataSource internal constructor(context: Context) :
    BaseRemoteDataSource(context), IConversationDataSource {
    private val conversationsCollection: CollectionReference
        get() = getMyConversationsCollection()

    private val observableConversationList = MutableLiveData<List<Conversation>>(null)

    override suspend fun getObservableConversations() = observableConversationList

    override suspend fun saveConversation(conversation: Conversation): Result<Unit> {
        val conversationId = conversationsCollection.document().id
        val msg = conversation.copy(
            id = conversationId,
            senderId = firebaseAuth.currentUser?.uid.toString()
        )
        val result = conversationsCollection.document(conversationId).set(msg).execute()
        return if (result is Result.Success) {
            Result.Success(Unit)
        } else result as Result.Error
    }

    override suspend fun saveConversations(conversations: List<Conversation>): Result<Unit> {
        // TODO: Delete conversation if all conversations in a chat have been deleted
        withContext(Dispatchers.IO) {
            conversations.forEach {
                launch {
                    saveConversation(it)
                }
            }
        }
        return Result.Success(Unit)
    }

    override suspend fun deleteConversation(conversation: Conversation): Task<Void>? {
        // TODO: Use the NEW last sent text as the new conversation if the deleted conversation was the latest
        return conversationsCollection.document(conversation.id).delete().addOnCompleteListener {
            if (it.isSuccessful) runBlocking {
                observableConversationList.deleteConversationIfPresent(conversation)
            }
        }
    }

    override suspend fun getConversations(): List<Conversation> {
        try {
            val currentUser =
                firebaseAuth.currentUser ?: throw Exception("Authentication is required")
            val currentUserId = currentUser.uid
            val conversationListAsSender = withContext(Dispatchers.IO) {
                conversationsCollection.whereEqualTo(ChatCreator.Fields.SENDER, currentUserId)
                    .get().await().toObjects(Conversation::class.java)
            }

            val conversationListAsReceiver = withContext(Dispatchers.IO) {
                conversationsCollection.whereEqualTo(ChatCreator.Fields.RECEIVER, currentUserId)
                    .get().await().toObjects(Conversation::class.java)
            }

            val conversationList =
                (conversationListAsReceiver + conversationListAsSender).getConversations(currentUser)
                    .withContacts(currentUser)

            observableConversationList.refreshList(conversationList)

            return conversationList
        } catch (ex: Exception) {
            setException(ex)
            observableConversationList.refreshList(emptyList())
            return emptyList()
        }
    }

    private suspend fun List<Conversation>.getConversations(user: FirebaseUser) =
        withContext(Dispatchers.Default) {
            // Conversations sent by(not sent to) current user
            val curUserConversations = arrayListOf<Conversation>()
            // Conversations not sent by(sent to) current user
            val noneCurUserConversations = arrayListOf<Conversation>()

            for (conversation in this@getConversations) {
                if (conversation.receiverId == user.uid) {
                    // Get conversations sent to current user
                    noneCurUserConversations += conversation
                } else {
                    // Get conversations sent by current user
                    // Assumption is made that no unauthorized conversation(s) escaped firestore query
                    curUserConversations += conversation
                }
            }

            // Group by conversation receiver(s)
            val curUserConversationsGroup = // curUserConversations.groupBy { it.receiverId }
                withContext(Dispatchers.Default) { curUserConversations.groupBy { it.receiverId } }
            // Group by conversation sender(s)
            val noneCurUserConversationsGroup = // noneCurUserConversations.groupBy { it.senderId }
                withContext(Dispatchers.Default) { noneCurUserConversations.groupBy { it.senderId } }

            // Clear list(s) for re-use
            curUserConversations.clear()
            noneCurUserConversations.clear()

            assert((curUserConversations.size + noneCurUserConversations.size) == 0)

            for (g in curUserConversationsGroup) {
                // For every receiver, get the latest conversation sent by current user
//                curUserConversations += g.value.sortedByDescending { it.timeSent }[0]
                curUserConversations += withContext(Dispatchers.Default) { g.value.sortedByDescending { it.timeSent } }[0]
            }

            for (g in noneCurUserConversationsGroup) {
                // For every sender, get the latest conversation sent to current user
//                noneCurUserConversations += g.value.sortedByDescending { it.timeSent }[0]
                noneCurUserConversations += withContext(Dispatchers.Default) { g.value.sortedByDescending { it.timeSent } }[0]
            }

            val convList = arrayListOf<Conversation>()
            for (c1 in curUserConversations) {
                // From the filtered list, get the latest conversation in a conversation between current
                // user and a receiver
                loop@ for ((i, c2) in noneCurUserConversations.withIndex()) {
                    Log.show("FCMService", "Comp: ${c1.receiverId} to ${c2.senderId}")
                    if (c1.receiverId == c2.senderId) {
                        // Add latest conversation between current user's and his/her friend's
                        convList += (if (c1.timeSent > c2.timeSent) c1 else c2)
                    } else if (i + 1 == noneCurUserConversations.size) {
                        convList += c1
                    }
                }
            }

            convList.sortedByDescending { it.timeSent }
        }

    private suspend fun List<Conversation>.withContacts(user: FirebaseUser): ArrayList<Conversation> {
        val userId = user.uid
        val conversationList = arrayListOf<Conversation>()

        for (conversation in this) {
            when {
                conversation.senderId == userId -> {
                    val sender = getLocalContact(
                        conversation.sender.copy(
                            id = userId,
                            mobileNumber = user.phoneNumber
                        )
                    )
                    val receiver =
                        usersCollection.whereEqualTo(
                            Contact.CREATOR.Fields.ID,
                            conversation.receiverId
                        )
                            .limit(1).get().await()
                    conversationList += conversation.copy(
                        sender = sender,
                        receiver = getLocalContact(receiver.getObject(conversation.receiver))
                    )
                }
                conversation.receiverId == userId -> {
                    val receiver = getLocalContact(
                        conversation.receiver.copy(
                            id = userId,
                            mobileNumber = user.phoneNumber
                        )
                    )
                    val sender =
                        usersCollection.whereEqualTo(
                            Contact.CREATOR.Fields.ID,
                            conversation.senderId
                        )
                            .limit(1).get().await()
                    conversationList += conversation.copy(
                        sender = getLocalContact(sender.getObject(conversation.sender)),
                        receiver = receiver
                    )
                }
            }
        }
        return conversationList
    }

    private suspend fun MutableLiveData<List<Conversation>>.deleteConversationIfPresent(conversation: Conversation) {
        withContext(Dispatchers.Default) {
            value?.filter { it == conversation }?.let { refreshList(it) }
        }
    }

    private fun MutableLiveData<List<Conversation>>.refreshList(list: List<Conversation>?) {
        postValue(list)
    }
}