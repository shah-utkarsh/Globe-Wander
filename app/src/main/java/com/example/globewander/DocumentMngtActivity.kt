package com.example.globewander

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import okhttp3.*
import java.io.File
import java.io.IOException

/**
 * Activity for managing user documents, including uploading and listing files from Firebase Storage.
 */
class DocumentMgmtActivity : AppCompatActivity() {

    private lateinit var uploadButton: Button
    private lateinit var userId: String
    private lateinit var listView: ListView
    private val fileList = mutableListOf<FileItem>()
    private lateinit var fileAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_document)

        // Retrieve user ID passed from previous activity
        userId = intent.getStringExtra("userId") ?: return

        // Initialize UI elements
        uploadButton = findViewById(R.id.button_upload)
        listView = findViewById(R.id.listView_files)

        // Set up file upload button
        uploadButton.setOnClickListener {
            chooseFile()
        }

        // Set up ArrayAdapter and assign it to the ListView
        fileAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1)
        listView.adapter = fileAdapter

        // Populate fileList and update ListView
        listFiles()

        // Set item click listener for ListView to download files when clicked
        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            Log.d("ListViewClick", "Item clicked at position: $position")
            downloadFile(fileList[position])
        }
    }

    /**
     * Launches an intent to choose a file from the device.
     */
    private fun chooseFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent, FILE_SELECT_CODE)
    }

    /**
     * Handles the result from file chooser intent.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == FILE_SELECT_CODE && resultCode == Activity.RESULT_OK) {
            val fileUri = data?.data
            uploadFileToFirebase(fileUri)
        }
    }

    /**
     * Uploads the selected file to Firebase Storage.
     */
    private fun uploadFileToFirebase(fileUri: Uri?) {
        fileUri ?: return

        val storageRef = FirebaseStorage.getInstance().reference.child("uploads/$userId/${fileUri.lastPathSegment}")
        storageRef.putFile(fileUri).addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                saveFileDataToDatabase(uri.toString())
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Upload failed", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Saves file metadata to Firebase Database.
     */
    private fun saveFileDataToDatabase(fileUrl: String) {
        val fileData = mapOf(
            "userId" to userId,
            "fileUrl" to fileUrl
        )

        FirebaseDatabase.getInstance().getReference("UserDocuments")
            .child(userId)
            .push()
            .setValue(fileData)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "File saved successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to save file info", Toast.LENGTH_SHORT).show()
                }
            }
    }

    companion object {
        private const val FILE_SELECT_CODE = 0
    }

    /**
     * Lists all files from Firebase Storage under the user's directory.
     */
    private fun listFiles() {
        val storageRef = FirebaseStorage.getInstance().reference.child("uploads/$userId")
        storageRef.listAll().addOnSuccessListener { listResult: ListResult ->
            fileList.clear() // Clear existing items
            listResult.items.forEach { fileRef ->
                fileRef.downloadUrl.addOnSuccessListener { uri ->
                    val fileName = fileRef.name
                    val fileItem = FileItem(fileName, uri.toString())
                    fileList.add(fileItem)
                    fileAdapter.add(fileName) // Add file name to the ArrayAdapter
                    Log.d("FileList", fileName)
                }
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error fetching files", Toast.LENGTH_SHORT).show()
            Log.e("FileList", "Error fetching files", it)
        }
    }

    /**
     * Downloads the selected file using OkHttp.
     */
    private fun downloadFile(fileItem: FileItem) {
        val fileUrl = fileItem.fileUrl
        val fileName = fileItem.fileName
        val destination = File(getExternalFilesDir(null), fileName) // Change to your desired destination

        Log.d("DownloadFile", "Downloading $fileUrl to $destination")

        val client = OkHttpClient.Builder().build()
        val request = Request.Builder().url(fileUrl).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle download failure
                Log.e("DownloadFile", "Download failed", e)
                runOnUiThread { Toast.makeText(this@DocumentMgmtActivity, "Download failed", Toast.LENGTH_SHORT).show() }
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.use { responseBody ->
                    try {
                        destination.outputStream().use { outputStream ->
                            responseBody.byteStream().copyTo(outputStream)
                        }
                        runOnUiThread { Toast.makeText(this@DocumentMgmtActivity, "Download complete", Toast.LENGTH_SHORT).show() }
                    } catch (e: IOException) {
                        Log.e("DownloadFile", "Error writing file", e)
                        runOnUiThread { Toast.makeText(this@DocumentMgmtActivity, "Error writing file", Toast.LENGTH_SHORT).show() }
                    }
                } ?: runOnUiThread { Toast.makeText(this@DocumentMgmtActivity, "Empty response body", Toast.LENGTH_SHORT).show() }
            }
        })
    }
}
