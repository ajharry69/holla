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
import com.xently.holla.data.model.Contact
import com.xently.holla.data.model.Message
import com.xently.holla.databinding.MessageFragmentBinding
import com.xently.holla.ui.list.message.MessageListFragment
import com.xently.xui.Fragment
import com.xently.xui.adapters.viewpager.FragmentPagerAdapter
import com.xently.xui.adapters.viewpager.TitledFragment
import com.xently.xui.utils.ui.fragment.hideKeyboard
import com.xently.xui.utils.ui.fragment.showSnackBar
import com.xently.xui.utils.ui.view.addTextChangeListener
import com.xently.xui.utils.ui.view.clearText
import com.xently.xui.utils.ui.view.setErrorTextAndFocus
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

    private val contact: Contact?
        get() = args.argsContact

    override val toolbarTitle: String?
        get() = contact?.name ?: super.toolbarTitle

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
        binding.run {
            viewPager.run {
                adapter = FragmentPagerAdapter(
                    listOf(TitledFragment(MessageListFragment.newInstance(args.toBundle()), null)),
                    childFragmentManager
                )
            }
            messageContainer.run {
                addTextChangeListener()
                setEndIconOnClickListener {
                    val message = getMessageFromInputs() ?: return@setEndIconOnClickListener
                    hideKeyboard()
                    viewModel.viewModelScope.launch(Dispatchers.Main) {
                        viewModel.sendMessage(message).also {
                            if (it is Result.Success<*>) {
                                clearText(binding.message)
                                viewModel.getMessages(contact)
                            }
                        }
                    }
                }
                setStartIconOnClickListener {
                    hideKeyboard()
                    showSnackBar("Coming soon!")
                }
            }
        }
    }

    private fun getMessageFromInputs(contactId: String = contact?.id ?: args.contactId): Message? {
        val message: String? = binding.message.text.toString()
        if (message.isNullOrBlank()) {
            binding.messageContainer.setErrorTextAndFocus(R.string.message_required)
            return null
        }

        if (message.length > resources.getInteger(R.integer.max_message_size)) {
            binding.messageContainer.setErrorTextAndFocus(R.string.message_too_long)
            return null
        }
        return Message(body = message, receiverId = contactId)
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
