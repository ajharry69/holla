package com.xently.holla.ui.user.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xently.holla.data.repository.schema.IUserRepository

class ProfileViewModel(private val repository: IUserRepository) : ViewModel() {
    // TODO: Implement the ViewModel
}

class ProfileViewModelFactory(private val repository: IUserRepository) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        ProfileViewModel(repository) as T
}
