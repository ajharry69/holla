package com.xently.holla

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.google.firebase.storage.StorageReference
import com.xently.holla.data.repository.schema.IChatRepository
import com.xently.holla.data.repository.schema.IContactRepository
import com.xently.holla.data.repository.schema.IUserRepository
import java.io.InputStream

class App : MultiDexApplication() {
    val chatRepository: IChatRepository
        get() = ServiceLocator.provideChatRepository(this)

    val userRepository: IUserRepository
        get() = ServiceLocator.provideUserRepository(this)

    val contactRepository: IContactRepository
        get() = ServiceLocator.provideContactRepository(this)

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }
}

@GlideModule
class AppGlideModule : com.bumptech.glide.module.AppGlideModule() {
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        registry.append(
            StorageReference::class.java, InputStream::class.java,
            FirebaseImageLoader.Factory()
        )
    }
}