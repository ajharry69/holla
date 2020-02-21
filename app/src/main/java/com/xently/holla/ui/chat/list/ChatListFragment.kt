package com.xently.holla.ui.chat.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.xently.holla.App
import com.xently.holla.databinding.ChatListFragmentBinding
import com.xently.holla.showSnackBar

class ChatListFragment : Fragment() {

    private var _binding: ChatListFragmentBinding? = null
    private val binding: ChatListFragmentBinding
        get() = _binding!!

    private val viewModel: ChatListViewModel by viewModels {
        ChatListViewModelFactory((requireContext().applicationContext as App).chatRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ChatListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fab.setOnClickListener {
            findNavController()
                .navigate(ChatListFragmentDirections.actionFragmentChatListToContactsFragment())
        }

        showSnackBar(binding.root, "Loading chats...", Snackbar.LENGTH_LONG)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
