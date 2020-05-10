package com.xently.holla.data.source.schema

import androidx.lifecycle.LiveData
import com.xently.holla.data.model.Contact

interface IBaseDataSource {
    fun getObservableException(): LiveData<Exception>
    fun getLocalContact(contact: Contact): Contact
}