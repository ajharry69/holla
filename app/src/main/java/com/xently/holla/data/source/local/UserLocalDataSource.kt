package com.xently.holla.data.source.local

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.xently.holla.data.Result
import com.xently.holla.data.model.Contact
import com.xently.holla.data.source.schema.IUserDataSource
import com.xently.holla.utils.Type

class UserLocalDataSource internal constructor(context: Context) : BaseLocalDataSource(context),
    IUserDataSource {
    override val observableContact: LiveData<Contact>
        get() = MutableLiveData(null)

    override suspend fun saveContact(type: Type) = Result.Success(Unit)

    override suspend fun signOut() = Result.Success(Unit)

    override suspend fun updateFCMToken(token: String?) = Unit
}