package com.xently.holla

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xently.holla.data.repository.schema.IUserRepository
import com.xently.holla.viewmodels.UserViewModel

class MainActivityViewModel(repository: IUserRepository) : UserViewModel(repository)

@Suppress("UNCHECKED_CAST")
class MainActivityViewModelFactory(private val repository: IUserRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        MainActivityViewModel(repository) as T
}