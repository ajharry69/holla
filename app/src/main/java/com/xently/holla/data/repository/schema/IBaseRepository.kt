package com.xently.holla.data.repository.schema

import androidx.lifecycle.LiveData
import com.xently.holla.data.model.Contact

interface IBaseRepository {
    fun getObservableException(): LiveData<Exception>
    fun getLocalContact(contact: Contact): Contact
}