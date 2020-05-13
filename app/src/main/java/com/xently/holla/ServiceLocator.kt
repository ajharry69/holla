package com.xently.holla

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.Room
import com.xently.holla.data.repository.ContactRepository
import com.xently.holla.data.repository.ConversationRepository
import com.xently.holla.data.repository.MessageRepository
import com.xently.holla.data.repository.UserRepository
import com.xently.holla.data.repository.schema.IContactRepository
import com.xently.holla.data.repository.schema.IConversationRepository
import com.xently.holla.data.repository.schema.IMessageRepository
import com.xently.holla.data.repository.schema.IUserRepository
import com.xently.holla.data.source.local.ContactLocalDataSource
import com.xently.holla.data.source.local.ConversationLocalDataSource
import com.xently.holla.data.source.local.MessageLocalDataSource
import com.xently.holla.data.source.local.UserLocalDataSource
import com.xently.holla.data.source.remote.ContactRemoteDataSource
import com.xently.holla.data.source.remote.ConversationRemoteDataSource
import com.xently.holla.data.source.remote.MessageRemoteDataSource
import com.xently.holla.data.source.remote.UserRemoteDataSource
import com.xently.holla.data.source.schema.IContactDataSource
import com.xently.holla.data.source.schema.IConversationDataSource
import com.xently.holla.data.source.schema.IMessageDataSource
import com.xently.holla.data.source.schema.IUserDataSource
import com.xently.holla.AppDatabase as Database

object ServiceLocator {

    private val lock = Any()

    @Volatile
    private var database: Database? = null

    @Volatile
    var localConversationDataSource: IConversationDataSource? = null
        @VisibleForTesting set

    @Volatile
    var remoteConversationDataSource: IConversationDataSource? = null
        @VisibleForTesting set

    @Volatile
    var localMessageDataSource: IMessageDataSource? = null
        @VisibleForTesting set

    @Volatile
    var remoteMessageDataSource: IMessageDataSource? = null
        @VisibleForTesting set

    @Volatile
    var localUserDataSource: IUserDataSource? = null
        @VisibleForTesting set

    @Volatile
    var remoteUserDataSource: IUserDataSource? = null
        @VisibleForTesting set

    @Volatile
    var localContactDataSource: IContactDataSource? = null
        @VisibleForTesting set

    @Volatile
    var remoteContactDataSource: IContactDataSource? = null
        @VisibleForTesting set

    @Volatile
    var conversationRepository: IConversationRepository? = null
        @VisibleForTesting set

    @Volatile
    var chatRepository: IMessageRepository? = null
        @VisibleForTesting set

    @Volatile
    var userRepository: IUserRepository? = null
        @VisibleForTesting set

    @Volatile
    var contactRepository: IContactRepository? = null
        @VisibleForTesting set

    fun provideConversationRepository(context: Context): IConversationRepository {
        synchronized(this) {
            return conversationRepository ?: createConversationRepository(context)
        }
    }

    fun provideChatRepository(context: Context): IMessageRepository {
        synchronized(this) {
            return chatRepository ?: createChatRepository(context)
        }
    }

    fun provideUserRepository(context: Context): IUserRepository {
        synchronized(this) {
            return userRepository ?: createUserRepository(context)
        }
    }

    fun provideContactRepository(context: Context): IContactRepository {
        synchronized(this) {
            return contactRepository ?: createContactRepository(context)
        }
    }

    private fun createConversationRepository(context: Context): IConversationRepository {
        val (localDataSource, remoteDataSource) = createConversationDataSources(context)
        val repo: IConversationRepository =
            ConversationRepository(localDataSource, remoteDataSource)
        this.conversationRepository = repo
        return repo
    }

    private fun createChatRepository(context: Context): IMessageRepository {
        val (localDataSource, remoteDataSource) = createChatDataSources(context)
        val repo: IMessageRepository = MessageRepository(localDataSource, remoteDataSource)
        this.chatRepository = repo
        return repo
    }

    private fun createUserRepository(context: Context): IUserRepository {
        val (localDataSource, remoteDataSource) = createUserDataSources(context)
        val repo: IUserRepository = UserRepository(remoteDataSource, localDataSource)
        this.userRepository = repo
        return repo
    }

    private fun createContactRepository(context: Context): IContactRepository {
        val (localDataSource, remoteDataSource) = createContactDataSources(context)
        val repo: IContactRepository = ContactRepository(localDataSource, remoteDataSource)
        this.contactRepository = repo
        return repo
    }

    private fun createUserDataSources(context: Context): Pair<UserLocalDataSource, UserRemoteDataSource> {
        val db = database ?: createDatabase(context)
        val local = this.localUserDataSource ?: UserLocalDataSource(context, db)
        val remote = this.remoteUserDataSource ?: UserRemoteDataSource(context)

        this.localUserDataSource = local
        this.remoteUserDataSource = remote

        return Pair(local as UserLocalDataSource, remote as UserRemoteDataSource)
    }

    private fun createContactDataSources(context: Context): Pair<ContactLocalDataSource, ContactRemoteDataSource> {
        val db = database ?: createDatabase(context)
        val local = this.localContactDataSource ?: ContactLocalDataSource(db.contactDao, context)
        val remote = this.remoteContactDataSource ?: ContactRemoteDataSource(context)

        this.localContactDataSource = local
        this.remoteContactDataSource = remote

        return Pair(local as ContactLocalDataSource, remote as ContactRemoteDataSource)
    }

    private fun createChatDataSources(context: Context): Pair<MessageLocalDataSource, MessageRemoteDataSource> {
        val db = database ?: createDatabase(context)

        val local = this.localMessageDataSource
            ?: MessageLocalDataSource(context, db.chatDao)
        val remote =
            this.remoteMessageDataSource ?: MessageRemoteDataSource(context)

        this.localMessageDataSource = local
        this.remoteMessageDataSource = remote

        return Pair(local as MessageLocalDataSource, remote as MessageRemoteDataSource)
    }

    private fun createConversationDataSources(context: Context): Pair<ConversationLocalDataSource, ConversationRemoteDataSource> {
        val db = database ?: createDatabase(context)

        val local = this.localConversationDataSource
            ?: ConversationLocalDataSource(db.conversationDao, context)
        val remote = this.remoteConversationDataSource ?: ConversationRemoteDataSource(context)

        this.localConversationDataSource = local
        this.remoteConversationDataSource = remote

        return Pair(local as ConversationLocalDataSource, remote as ConversationRemoteDataSource)
    }

    private fun createDatabase(context: Context): Database = database ?: synchronized(this) {
        val db = Room.databaseBuilder(context.applicationContext, Database::class.java, "holla.db")
            .fallbackToDestructiveMigration()
            .enableMultiInstanceInvalidation() // Look for other instances of room accessing same db
            .build()
        this.database = db
        db
    }

    @VisibleForTesting
    fun resetRepository() {
        synchronized(lock) {
            // Clear all data to avoid test pollution.
            database?.apply {
                clearAllTables()
                close()
            }
            database = null
            chatRepository = null
            userRepository = null
            contactRepository = null
        }
    }
}