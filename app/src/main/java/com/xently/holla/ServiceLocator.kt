package com.xently.holla

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.xently.holla.data.repository.ChatRepository
import com.xently.holla.data.repository.ContactRepository
import com.xently.holla.data.repository.UserRepository
import com.xently.holla.data.repository.schema.IChatRepository
import com.xently.holla.data.repository.schema.IContactRepository
import com.xently.holla.data.repository.schema.IUserRepository

object ServiceLocator {
    @Volatile
    var chatRepository: IChatRepository? = null
        @VisibleForTesting set

    @Volatile
    var userRepository: IUserRepository? = null
        @VisibleForTesting set

    @Volatile
    var contactRepository: IContactRepository? = null
        @VisibleForTesting set

    fun provideChatRepository(context: Context): IChatRepository {
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

    private fun createChatRepository(context: Context): IChatRepository {
        val repo: IChatRepository = ChatRepository()
        this.chatRepository = repo
        return repo
    }

    private fun createUserRepository(context: Context): IUserRepository {
        val repo: IUserRepository = UserRepository(context)
        this.userRepository = repo
        return repo
    }

    private fun createContactRepository(context: Context): IContactRepository {
        val repo: IContactRepository = ContactRepository(context)
        this.contactRepository = repo
        return repo
    }
}