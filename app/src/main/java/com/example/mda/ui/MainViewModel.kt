package com.example.mda.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mda.data.AskResponse
import com.example.mda.data.DocumentRepository
import com.example.mda.data.UploadResponse
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class MainViewModel : ViewModel() {
    private val repository = DocumentRepository()

    private val _uploadStatus = MutableLiveData<Result<UploadResponse>?>()
    val uploadStatus: LiveData<Result<UploadResponse>?> = _uploadStatus

    private val _askResponse = MutableLiveData<Result<AskResponse>?>()
    val askResponse: LiveData<Result<AskResponse>?> = _askResponse

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _selectedFileName = MutableLiveData<String?>()
    val selectedFileName: LiveData<String?> = _selectedFileName

    fun setSelectedFile(name: String?) {
        _selectedFileName.value = name
    }

    fun uploadPdf(filePart: MultipartBody.Part) {
        viewModelScope.launch {
            _isLoading.value = true
            _uploadStatus.value = null
            try {
                val response = repository.uploadPdf(filePart)
                if (response.isSuccessful && response.body() != null) {
                    _uploadStatus.value = Result.success(response.body()!!)
                } else {
                    _uploadStatus.value = Result.failure(Exception("Upload failed: ${response.code()}"))
                }
            } catch (e: Exception) {
                _uploadStatus.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun askQuestion(question: String) {
        if (question.isBlank()) return
        
        viewModelScope.launch {
            _isLoading.value = true
            _askResponse.value = null
            try {
                val response = repository.askQuestion(question)
                if (response.isSuccessful && response.body() != null) {
                    _askResponse.value = Result.success(response.body()!!)
                } else {
                    _askResponse.value = Result.failure(Exception("Failed to get answer: ${response.code()}"))
                }
            } catch (e: Exception) {
                _askResponse.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
