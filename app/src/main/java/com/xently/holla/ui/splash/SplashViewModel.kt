package com.xently.holla.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xently.holla.data.repository.schema.IUserRepository
import com.xently.holla.viewmodels.UserViewModel

class SplashViewModel(repository: IUserRepository) : UserViewModel(repository)

class SplashViewModelFactory(private val repository: IUserRepository) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = SplashViewModel(repository) as T
}