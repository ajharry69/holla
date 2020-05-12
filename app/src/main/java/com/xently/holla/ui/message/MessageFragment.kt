package com.xently.holla.ui.message

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.xently.holla.*
import com.xently.holla.data.Result
import com.xently.holla.data.model.Contact
import com.xently.holla.data.model.MediaFile
import com.xently.holla.data.model.Message
import com.xently.holla.data.model.Type
import com.xently.holla.databinding.MessageFragmentBinding
import com.xently.holla.ui.list.message.MessageListFragment
import com.xently.holla.utils.BitmapUtils.scaledBitmapToMatchView
import com.xently.holla.utils.FileUtils
import com.xently.xui.Fragment
import com.xently.xui.adapters.viewpager.FragmentPagerAdapter
import com.xently.xui.adapters.viewpager.TitledFragment
import com.xently.xui.utils.ui.fragment.hideKeyboard
import com.xently.xui.utils.ui.fragment.showSnackBar
import com.xently.xui.utils.ui.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.joda.time.DateTime
import java.io.File
import java.io.IOException
import java.util.*

class MessageFragment : Fragment(), FirebaseAuth.AuthStateListener {

    private var mediaFilePath: String? = null

    private var mediaFile: MediaFile? = null

    /**
     * Action to take when [Manifest.permission.READ_EXTERNAL_STORAGE] is been granted...
     */
    private val onExternalStoragePermissionGranted = { showImageFilePicker() }

    /**
     * Action to take when [Manifest.permission.CAMERA] is been granted...
     */
    private val onCameraPermissionGranted = { showCamera() }

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
                    sendMessage(message)
                }
                setStartIconOnClickListener {
                    hideKeyboard()
                    requestFeaturePermission(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        PERMISSION_REQUEST_CODE_READ_STORAGE,
                        onExternalStoragePermissionGranted
                    )
                }
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.getObservableException().observe(viewLifecycleOwner, Observer {
            if (it == null) return@Observer
            it.message?.let { it1 -> showSnackBar(it1, Snackbar.LENGTH_LONG) }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val withMediaFile: (image: Uri?, filePath: String?) -> Unit = { image, file ->
            image?.also { uri ->
                binding.image.run {
                    showViews(binding.imageContainer)
                    try {
                        if (file == null) {
                            setImageURI(uri)
                        } else {
                            setImageBitmap(scaledBitmapToMatchView(this, file))
                        }
                    } catch (ex: Exception) {
                        setImageURI(uri)
                        Log.show("MainActivity", ex.message, ex, Log.Type.ERROR)
                    }
                }
                dumpImageMetaData(uri)
            }
        }

        data?.data?.run {
            when (requestCode) {
                INTENT_REQUEST_CODE_IMAGE_PICKER -> {
                    mediaFilePath = FileUtils.getFile(requireContext(), this)?.absolutePath
                    withMediaFile.invoke(this, mediaFilePath)
                }
                INTENT_REQUEST_CODE_CAMERA -> {
                    withMediaFile.invoke(this, mediaFilePath)
                }
                else -> Unit
            }
            mediaFile = MediaFile(uri = this)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        val isPermissionGranted =
            grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED

        when (requestCode) {
            PERMISSION_REQUEST_CODE_READ_STORAGE -> {
                if (isPermissionGranted) onExternalStoragePermissionGranted.invoke()
            }
            PERMISSION_REQUEST_CODE_CAMERA -> {
                if (isPermissionGranted) onCameraPermissionGranted.invoke()
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onAuthStateChanged(p0: FirebaseAuth) {

    }

    private fun sendMessage(message: Message) {
        viewModel.run {
            viewModelScope.launch(Dispatchers.Main) {
                sendMessage(message).also {
                    if (it is Result.Success<*>) {
                        clearText(binding.message)
                        hideViews(binding.imageContainer)
                        viewModel.getMessages(message.receiverId) // TODO: Delete...
                    }
                }
            }
        }
    }

    private fun getMessageFromInputs(
        contactId: String = contact?.id ?: args.contactId,
        file: MediaFile? = mediaFile
    ): Message? {
        val message: String? = binding.message.text.toString()
        if (message.isNullOrBlank()) {
            binding.messageContainer.setErrorTextAndFocus(R.string.message_required)
            return null
        }

        /*if (message.length > resources.getInteger(R.integer.max_message_size)) {
            binding.messageContainer.setErrorTextAndFocus(R.string.message_too_long)
            return null
        }*/
        val mimeType: String = (file?.uri?.let {
            requireContext().contentResolver.getType(it)
        } ?: "text/plain").toLowerCase(Locale.ROOT)
        val type: Type = when {
            Regex("^image[^\\s]+").matches(mimeType) -> Type.Photo
            Regex("^text[^\\s]+").matches(mimeType) -> Type.Text
            Regex("^application[^\\s]+").matches(mimeType) -> Type.Document
            Regex("^video[^\\s]+").matches(mimeType) -> Type.Video
            else -> Type.Text
        }
        return Message(body = message, receiverId = contactId, type = type, mediaFile = file)
    }

    private fun showImageFilePicker() {
        Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "image/*"
            addCategory(Intent.CATEGORY_OPENABLE)
            if (resolveActivity(requireContext().packageManager) != null) {
                startActivityForResult(this, INTENT_REQUEST_CODE_IMAGE_PICKER)
            }
        }
    }

    private fun showCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->

            if (intent.resolveActivity(requireContext().packageManager) != null) {
                // Attempt to create a temporary profile picture file
                val tempPhoto: File? = try {
                    val tempImageFile = createTempImageFile()
                    // Retrieve the file's absolute path
                    mediaFilePath = tempImageFile.first
                    // Retrieve the file
                    tempImageFile.second
                } catch (ex: IOException) {
                    // An error occurred during the attempt
                    null
                }

                // Temporary profile picture file was successfully created
                tempPhoto?.also {
                    val photoUri = FileProvider.getUriForFile(
                        requireContext(),
                        // Without this(authority) a FileUriExposedException will be thrown on
                        // Android 7.0(API level 24) and higher
                        "${BuildConfig.APPLICATION_ID}.files",
                        it
                    )

                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                    startActivityForResult(intent, INTENT_REQUEST_CODE_CAMERA)
                }
            }
        }
    }

    /**
     * Creates a temporary file in this app's external files dir within [Environment.DIRECTORY_PICTURES]
     * to hold user profile image taken from the camera
     * @param name temporary name to save the photo/image with
     * @return Pair<String, File>. String=File's path & File=File created
     */
    @Throws(IOException::class)
    private fun createTempImageFile(name: String = "${FirebaseAuth.getInstance().currentUser?.uid}_${DateTime.now().millis}"): Pair<String, File> {
        val dir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        val file = File.createTempFile(name, ".jpg", dir)
        return Pair(file.absolutePath, file)
    }

    private fun dumpImageMetaData(uri: Uri) {

        Log.show(LOG_TAG, "Photo URI: $uri")

        val cursor: Cursor? = requireContext().contentResolver.query(
            uri,
            null,
            null,
            null,
            null,
            null
        )

        cursor?.use {
            if (it.moveToFirst()) {

                // Note it's called "Display Name".  This is
                // provider-specific, and might not necessarily be the file name.
                val displayName: String =
                    it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                Log.show(LOG_TAG, "Display Name: $displayName")

                val sizeIndex: Int = it.getColumnIndex(OpenableColumns.SIZE)

                val size: String = if (!it.isNull(sizeIndex)) {
                    it.getString(sizeIndex)
                } else {
                    "Unknown"
                }

                Log.show(LOG_TAG, "Size: $size")
            }
        }
    }

    companion object {
        private val LOG_TAG = MessageFragment::class.java.simpleName
        private const val INTENT_REQUEST_CODE_IMAGE_PICKER: Int = 123
        private const val INTENT_REQUEST_CODE_CAMERA: Int = 321
        private const val PERMISSION_REQUEST_CODE_READ_STORAGE = 1234
        private const val PERMISSION_REQUEST_CODE_CAMERA = 4321
    }
}
