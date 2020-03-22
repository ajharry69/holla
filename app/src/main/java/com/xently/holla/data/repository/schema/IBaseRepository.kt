package com.xently.holla.data.repository.schema

import androidx.lifecycle.LiveData

interface IBaseRepository {
    fun getObservableException(): LiveData<Exception>
}