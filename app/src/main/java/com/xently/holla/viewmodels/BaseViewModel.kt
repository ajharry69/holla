package com.xently.holla.viewmodels

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.xently.holla.data.model.Contact
import com.xently.holla.data.repository.schema.IBaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class BaseViewModel(private val repository: IBaseRepository) : ViewModel() {
    suspend fun getLocalContact(contact: Contact) = withContext(Dispatchers.IO) {
        repository.getLocalContact(contact)
    }

    fun getObservableException() = repository.getObservableException()

    suspend fun getBitmap(context: Context, url: String): Bitmap? = withContext(Dispatchers.IO) {
        if (url.isBlank()) null
        else with(Glide.with(context)) {
            asBitmap().load(url).submit().run {
                get().apply {
                    // Cancel pending Glide loads
                    clear(this@run)
                }
            }
        }
    }
}