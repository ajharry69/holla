package com.xently.holla.data.source.schema

import androidx.lifecycle.LiveData
import com.xently.holla.data.model.Contact

interface IBaseDataSource {
    fun getObservableException(): LiveData<Exception>

    /**
     * @return new contact with [contact]'s name replaced with name as the contact is saved by
     * the user in their phone books
     */
    fun getLocalContact(contact: Contact): Contact
}