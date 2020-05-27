package at.tugraz.ist.sw20.swta1.cheat.ui.chat

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import at.tugraz.ist.sw20.swta1.cheat.ChatActivity
import at.tugraz.ist.sw20.swta1.cheat.R
import at.tugraz.ist.sw20.swta1.cheat.RecyclerItemClickListener
import at.tugraz.ist.sw20.swta1.cheat.bluetooth.BluetoothService
import at.tugraz.ist.sw20.swta1.cheat.bluetooth.BluetoothServiceProvider
import at.tugraz.ist.sw20.swta1.cheat.bluetooth.BluetoothState
import at.tugraz.ist.sw20.swta1.cheat.bluetooth.IBluetoothDevice
import kotlinx.android.synthetic.main.chat_fragment.view.*
import kotlinx.android.synthetic.main.dialog_message_options.*
import kotlinx.android.synthetic.main.item_icon_with_text.view.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.util.*
import java.text.SimpleDateFormat

class ChatFragment : Fragment() {
    companion object {
        fun newInstance() = ChatFragment()
    }

    private lateinit var viewModel: ChatViewModel

    private lateinit var root: View
    private lateinit var chatAdapter: ChatHistoryAdapter
    private lateinit var recyclerView: RecyclerView
    private var chatPartner: IBluetoothDevice? = null
    private var currentEditMessage: ChatEntry? = null
    
    private val RESULT_SELECT_IMAGE = 1
    private val RESULT_CAPTURE_IMAGE = 2

    private var currentPhoto: File? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        viewModel = ViewModelProvider(this).get(ChatViewModel::class.java)
        root =  inflater.inflate(R.layout.chat_fragment, container, false)

        // viewModel.insertMessage(ChatEntry("Hi", true, false, Date()))

        val header = root.item_header_text.findViewById<TextView>(R.id.title)
        chatPartner = BluetoothServiceProvider.getBluetoothService().getConnectedDevice()
        header.text = chatPartner?.name

        BluetoothServiceProvider.getBluetoothService().setOnMessageReceive { chatEntry ->
            chatEntry.isByMe = false
            Log.i("Message", "Message received: ${chatEntry.getMessage()}")
            if (chatEntry.isBySystem && chatEntry.getMessage() == getString(R.string.partner_disconnected))  {
                (activity as ChatActivity).reconnect = false;
            }
            if (chatEntry.isBySystem && chatEntry.getMessage() == getString(R.string.partner_connected))  {
                (activity as ChatActivity).reconnect = true;
            }
            val scrollPosition = viewModel.insertMessage(chatEntry)
            activity!!.runOnUiThread {
                chatAdapter.notifyDataSetChanged()
                if (scrollPosition > -1) {
                    recyclerView.smoothScrollToPosition(scrollPosition)
                }
            }
        }

        BluetoothServiceProvider.getBluetoothService().setOnStateChangeListener { _, newState ->
            val connection_status = root.findViewById<TextView>(R.id.connection_status)

            if (!BluetoothServiceProvider.getBluetoothService().isBluetoothEnabled()) {
                (activity as ChatActivity).goBackToMainActivity()
            }
            activity?.runOnUiThread {
                when (newState) {
                    BluetoothState.CONNECTED -> connection_status.text =
                        getString(R.string.connected_status)
                    BluetoothState.READY -> {
                        connection_status.text = getString(R.string.disconnected_status)
                        if ((activity as ChatActivity).reconnect) {
                            chatPartner?.let {
                                BluetoothServiceProvider.getBluetoothService().connectToDevice(it)
                            }
                        }
                    }
                    else -> {
                    }
                }
            }
        }

        chatAdapter = ChatHistoryAdapter(viewModel.getChatEntries(), this)

        recyclerView = root.findViewById<RecyclerView>(R.id.chat_history).apply {
            layoutManager = LinearLayoutManager(context!!)
            adapter = chatAdapter
        }

        root.item_edit_hint.findViewById<Button>(R.id.btn_cancel_edit).setOnClickListener {
            currentEditMessage = null
            root.item_edit_hint.visibility = View.GONE
            val etMsg = root.item_text_entry_field.findViewById<EditText>(R.id.text_entry)
            etMsg.text.clear()
        }

        (recyclerView.layoutManager as LinearLayoutManager).stackFromEnd = true

        initSendButton()
        initPictureSendButton()
        initConnectionButton()

        return root
    }

    private fun deleteChatEntry(chatEntry: ChatEntry) {
        chatEntry.setDeleted()
        chatAdapter.notifyDataSetChanged()
        BluetoothServiceProvider.getBluetoothService().sendMessage(chatEntry)

    }

    private fun initConnectionButton() {
        val connectionStatus = root.findViewById<TextView>(R.id.connection_status)
        connectionStatus.setOnClickListener {
            if(BluetoothServiceProvider.getBluetoothService().state == BluetoothState.CONNECTED) {
                (activity as ChatActivity).disconnect()
            }
        }
    }

    private fun initSendButton() {
        val btnSend = root.item_text_entry_field.findViewById<Button>(R.id.btn_send)
        val etMsg = root.item_text_entry_field.findViewById<EditText>(R.id.text_entry)

        btnSend.setOnClickListener {
            val text = etMsg.text.toString().trim()
            if (BluetoothServiceProvider.getBluetoothService().state != BluetoothState.CONNECTED) {
                Toast.makeText(context, getString(R.string.sending_message_disconnected), Toast.LENGTH_SHORT).show()
            } else if (text.isNotBlank()) {
                var chatEntry = currentEditMessage
                if(chatEntry != null) {
                    chatEntry.edit(text)
                    currentEditMessage = null
                    root.item_edit_hint.visibility = View.GONE
                } else {
                    chatEntry = ChatEntry(text, true, false, Date())
                }
                val scrollPosition = viewModel.insertMessage(chatEntry)
                chatAdapter.notifyDataSetChanged()
                if(scrollPosition != -1) {
                    recyclerView.smoothScrollToPosition(scrollPosition)
                }
                BluetoothServiceProvider.getBluetoothService().sendMessage(chatEntry)
                etMsg.text.clear()
            }
        }
    }
    
    private fun initPictureSendButton() {
        val imageBtn = root.item_text_entry_field.findViewById<ImageButton>(R.id.image_select)
    
        imageBtn.setOnClickListener {
            
            val layout = layoutInflater.inflate(R.layout.dialog_choose_image_src, null) as View
            val dialog = AlertDialog.Builder(context!!).create()
            dialog.setView(layout)
            
            layout.findViewById<ImageButton>(R.id.dialog_image_src_camera).setOnClickListener {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

                val photoFile: File? = try {
                    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                    val storageDir: File? = context!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

                    val file = File.createTempFile(
                            "IMG_${timeStamp}",
                            ".jpg",
                            storageDir
                    )
                    
                    currentPhoto = File(storageDir, file.name)
                    file
                } catch (ex: IOException) {
                    dialog.cancel()
                    Toast.makeText(context, context!!.getString(R.string.image_creation_failed), Toast.LENGTH_SHORT).show()
                    null
                }

                photoFile?.also {
                    val photoUri = FileProvider.getUriForFile(context!!, context!!.packageName + ".provider", it);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                    dialog.cancel()
                    startActivityForResult(intent, RESULT_CAPTURE_IMAGE)
                }
            }
    
            layout.findViewById<ImageButton>(R.id.dialog_image_src_gallery).setOnClickListener {
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                intent.type = "image/*"
                dialog.cancel()
                startActivityForResult(intent, RESULT_SELECT_IMAGE)
            }
            
            dialog.show()
        }
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    
        fun sendImage (bitmap: Bitmap) {
            if (BluetoothServiceProvider.getBluetoothService().state != BluetoothState.CONNECTED) {
                Toast.makeText(context, context!!.getString(R.string.sending_message_disconnected), Toast.LENGTH_SHORT).show()
            } else {
                val builder = AlertDialog.Builder(context!!)
                builder.setTitle(context!!.getString(R.string.send_image_dialog_title))
                builder.setMessage(context!!.getString(R.string.send_image_dialog))
                
                Log.d("Image", "Dim: ${bitmap.width}x${bitmap.height}")
    
                builder.setPositiveButton(context!!.getString(R.string.dialog_option_yes)) { _, _ ->
                    Thread {
                        val bos = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, bos)
                        val array: ByteArray = bos.toByteArray()
                        bitmap.recycle()
                        Log.d("Image", "Image compressed, size ${array.size}")
            
                        val chatEntry = ChatEntry("", array, true, false, Date())
                        val index = viewModel.insertMessage(chatEntry)
            
                        activity!!.runOnUiThread {
                            val etMsg = root.item_text_entry_field.findViewById<EditText>(R.id.text_entry)
                            etMsg.text.clear()
                            chatAdapter.notifyDataSetChanged()
                            recyclerView.smoothScrollToPosition(index)
                        }

                        BluetoothServiceProvider.getBluetoothService().sendMessage(chatEntry)
                    }.start()
                }
    
                builder.setNegativeButton(context!!.getString(R.string.dialog_option_no)){_,_ -> }
    
                val dialog: AlertDialog = builder.create()
                dialog.show()
            }
        }
        
        if(resultCode == RESULT_OK) {
            if(requestCode == RESULT_SELECT_IMAGE && data != null) {
                Log.d("Image", "Image selected from gallery")
                sendImage(MediaStore.Images.Media.getBitmap(context?.contentResolver, data.data!!))
            } else if(requestCode == RESULT_CAPTURE_IMAGE && currentPhoto != null) {
                Log.d("Image", "Image taken with camera")

                sendImage(MediaStore.Images.Media.getBitmap(context!!.contentResolver,
                    FileProvider.getUriForFile(context!!, context!!.packageName + ".provider", currentPhoto!!)))
                
                currentPhoto?.delete()
            }
        }
    }
    
    fun showContextMenu(message: ChatEntry) {
        if (message.isByMe && !message.isDeleted()) {
            val layout = layoutInflater.inflate(R.layout.dialog_message_options, null) as View
            val builder = AlertDialog.Builder(activity!!)
            builder.setView(layout)
            layout.findViewById<TextView>(R.id.message_dialog_title).text = getString(R.string.chat_options_title)

            var btnEdit = layout.findViewById<Button>(R.id.message_dialog_edit).item_text
            btnEdit.text = getString(R.string.edit)
            layout.findViewById<ImageView>(R.id.message_dialog_edit).item_icon.setImageResource(R.drawable.ic_edit)

            var btnDelete = layout.findViewById<Button>(R.id.message_dialog_delete).item_text
            btnDelete.text = getString(R.string.delete)
            layout.findViewById<ImageView>(R.id.message_dialog_delete).item_icon.setImageResource(R.drawable.ic_delete)

            val dialog: AlertDialog = builder.create()

            if (message.isImage()) {
                layout.findViewById<View>(R.id.message_dialog_edit).visibility = View.GONE
                btnDelete.setOnClickListener {
                    deleteChatEntry(message)
                }
            } else {
                btnEdit.setOnClickListener {
                    currentEditMessage = message
                    root.item_edit_hint.visibility = View.VISIBLE
                    root.item_edit_hint.findViewById<TextView>(R.id.tv_edit_text).text =
                            message.getMessageShortened(context!!)
                    val etMsg =
                            root.item_text_entry_field.findViewById<EditText>(R.id.text_entry)
                    etMsg.setText(message.getMessage())
                    dialog.dismiss()
                }
                btnDelete.setOnClickListener {
                    deleteChatEntry(message)
                    dialog.dismiss()
                }
            }
        

            dialog.show()
        }
    }

    fun showFullImage(image: Bitmap) {
        root.findViewById<ConstraintLayout>(R.id.cl_chat_fragment).visibility = View.GONE
        val imageView = root.findViewById<ImageView>(R.id.chat_history_full_image)
        imageView.setImageBitmap(image)
        imageView.visibility = View.VISIBLE
    }

    fun hideFullImage() {
        root.findViewById<ConstraintLayout>(R.id.cl_chat_fragment).visibility = View.VISIBLE
        root.findViewById<ImageView>(R.id.chat_history_full_image).visibility = View.GONE
    }
}