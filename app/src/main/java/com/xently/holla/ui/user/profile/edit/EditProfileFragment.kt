package com.xently.holla.ui.user.profile.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.xently.holla.databinding.EditProfileFragmentBinding

class EditProfileFragment : Fragment() {

    private val viewModel: EditProfileViewModel by viewModels {
        EditProfileViewModelFactory()
    }

    private var _binding: EditProfileFragmentBinding? = null
    private val binding: EditProfileFragmentBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = EditProfileFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        fun newInstance() =
            EditProfileFragment()
    }
}
