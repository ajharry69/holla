package com.xently.holla.ui.message

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.xently.holla.App
import com.xently.holla.R
import com.xently.holla.data.Result
import com.xently.holla.data.model.Message
import com.xently.holla.data.model.Contact
import com.xently.holla.databinding.MessageFragmentBinding
import com.xently.xui.Fragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MessageFragment : Fragment(), FirebaseAuth.AuthStateListener {

    private var _binding: MessageFragmentBinding? = null
    private val binding: MessageFragmentBinding
        get() = _binding!!

    private val viewModel: MessageViewModel by viewModels {
        MessageViewModelFactory((requireContext().applicationContext as App).chatRepository)
    }

    private val args: MessageFragmentArgs by navArgs()

    private val contact: Contact
        get() = args.argsContact

    override val toolbarTitle: String?
        get() = contact.name ?: super.toolbarTitle

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = MessageFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        binding.viewPager.removeAllViews()
        _binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding.viewPager) {
            adapter = FragmentPagerAdapter(
                listOf(TitledFragment(MessageListFragment.newInstance(contact), null)),
                childFragmentManager
            )
        }
        binding.messageContainer.removeErrorOnTextChange()
        binding.messageContainer.setEndIconOnClickListener {
            val message: String? = binding.message.text.toString()
            if (message.isNullOrBlank()) {
                binding.messageContainer.setErrorTextAndFocus(R.string.message_required)
                return@setEndIconOnClickListener
            }

            if (message.length > resources.getInteger(R.integer.max_message_size)) {
                binding.messageContainer.setErrorTextAndFocus(R.string.message_too_long)
                return@setEndIconOnClickListener
            }
            hideKeyboard()
            viewModel.viewModelScope.launch(Dispatchers.Main) {
                viewModel.sendMessage(
                    Message(
                        body = message,
                        receiverId = contact.id
                    )
                ).also {
                    if (it is Result.Success<*>) {
                        clearText(binding.message)
                        viewModel.getConversations(contact)
                    }
                }
            }
        }
        binding.messageContainer.setStartIconOnClickListener {
            hideKeyboard()
            showSnackBar("Coming soon!")
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.getObservableException().observe(viewLifecycleOwner, Observer {
            if (it == null) return@Observer
            it.message?.let { it1 -> showSnackBar(it1, Snackbar.LENGTH_LONG) }
        })
    }

    override fun onAuthStateChanged(p0: FirebaseAuth) {

    }
}
