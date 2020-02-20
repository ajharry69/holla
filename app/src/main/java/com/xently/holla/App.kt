package com.xently.holla

import androidx.multidex.MultiDexApplication
import com.xently.holla.data.repository.schema.IChatRepository
import com.xently.holla.data.repository.schema.IUserRepository

class App : MultiDexApplication() {
    val chatRepository: IChatRepository
        get() = ServiceLocator.provideChatRepository(this)

    val userRepository: IUserRepository
        get() = ServiceLocator.provideUserRepository(this)
}