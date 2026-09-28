package com.example.mda

import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.mda.databinding.ActivityMainBinding
import com.example.mda.ui.MainViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private var selectedFileUri: Uri? = null

    private val selectPdfLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            selectedFileUri = it
            val fileName = getFileName(it)
            binding.tvSelectedFile.text = "Selected: $fileName"
            binding.btnUpload.isEnabled = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {
        binding.btnSelectPdf.setOnClickListener {
            selectPdfLauncher.launch("application/pdf")
        }

        binding.btnUpload.setOnClickListener {
            selectedFileUri?.let { uri ->
                uploadPdf(uri)
            }
        }

        binding.btnAsk.setOnClickListener {
            val question = binding.etQuestion.text.toString()
            if (question.isNotEmpty()) {
                viewModel.askQuestion(question)
            } else {
                Toast.makeText(this, "Please enter a question", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnUpload.isEnabled = !isLoading && selectedFileUri != null
            binding.btnAsk.isEnabled = !isLoading
        }

        viewModel.uploadStatus.observe(this) { result ->
            result?.onSuccess {
                Toast.makeText(this, "Upload successful!", Toast.LENGTH_SHORT).show()
                binding.tvError.visibility = View.GONE
            }?.onFailure {
                binding.tvError.text = "Upload failed: ${it.message}"
                binding.tvError.visibility = View.VISIBLE
            }
        }

        viewModel.askResponse.observe(this) { result ->
            result?.onSuccess {
                binding.tvAnswer.text = it.answer
                binding.tvRetrieved.text = it.retrievedChunks.joinToString("\n\n---\n\n")
                binding.tvError.visibility = View.GONE
            }?.onFailure {
                binding.tvError.text = "Error: ${it.message}"
                binding.tvError.visibility = View.VISIBLE
            }
        }
    }

    private fun uploadPdf(uri: Uri) {
        val contentResolver = contentResolver
        val fileName = getFileName(uri) ?: "document.pdf"
        val tempFile = File(cacheDir, fileName)
        
        try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(tempFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            val requestFile = tempFile.asRequestBody("application/pdf".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", tempFile.name, requestFile)
            
            viewModel.uploadPdf(body)
        } catch (e: Exception) {
            Toast.makeText(this, "Error preparing file: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (index != -1) result = it.getString(index)
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != null && cut != -1) {
                result = result?.substring(cut + 1)
            }
        }
        return result
    }
}
