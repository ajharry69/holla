package com.xently.holla.ui.message.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.xently.holla.App
import com.xently.holla.databinding.MessageListFragmentBinding
import com.xently.holla.showSnackBar

class MessageListFragment : Fragment(), FirebaseAuth.AuthStateListener {

    private var _binding: MessageListFragmentBinding? = null
    private val binding: MessageListFragmentBinding
        get() = _binding!!

    private val viewModel: MessageListViewModel by viewModels {
        MessageListViewModelFactory((requireContext().applicationContext as App).chatRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = MessageListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = LinearLayoutManager(requireContext()).apply {
            reverseLayout = true
            stackFromEnd = true
        }
        binding.listView.apply {
            setHasFixedSize(true)
            setLayoutManager(layoutManager)
            addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
                if (bottom < oldBottom) {
                    binding.listView.postDelayed({
                        binding.listView.smoothScrollToPosition(
                            0
                        )
                    }, 100)
                }
            }
        }
        binding.messageContainer.setEndIconOnClickListener {
            showSnackBar(binding.root, "Implement message sending")
        }
        binding.messageContainer.setStartIconOnClickListener {
            showSnackBar(binding.root, "Implement file attachment")
        }
    }

    override fun onAuthStateChanged(p0: FirebaseAuth) {

    }

    companion object {
        private val sChatCollection = FirebaseFirestore.getInstance().collection("messages")

        /**
         * Get the last 50 chat messages ordered by timestamp .
         */
        private val sChatQuery =
            sChatCollection.orderBy("sentAt", Query.Direction.DESCENDING).limit(50)
    }
}



