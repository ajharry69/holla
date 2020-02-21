package com.xently.holla.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.xently.holla.App
import com.xently.holla.databinding.ChatFragmentBinding
import com.xently.holla.showSnackBar

class ChatFragment : Fragment() {

    private var _binding: ChatFragmentBinding? = null
    private val binding: ChatFragmentBinding
        get() = _binding!!

    private val viewModel: ChatViewModel by viewModels {
        ChatViewModelFactory((requireContext().applicationContext as App).chatRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ChatFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.messageContainer.setEndIconOnClickListener {
            showSnackBar(binding.root, "Implement message sending")
        }
        binding.messageContainer.setStartIconOnClickListener {
            showSnackBar(binding.root, "Implement file attachment")
        }
    }
}



