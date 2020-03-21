package com.xently.holla.ui.user.profile.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class EditProfileViewModel : ViewModel() {
    // TODO: Implement the ViewModel
}

@Suppress("UNCHECKED_CAST")
class EditProfileViewModelFactory : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = EditProfileViewModel() as T
}