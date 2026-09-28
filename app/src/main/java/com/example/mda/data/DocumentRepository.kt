package com.example.mda.data

import com.example.mda.network.ApiClient
import okhttp3.MultipartBody
import retrofit2.Response

class DocumentRepository {
    private val api = ApiClient.instance

    suspend fun checkHealth() = api.checkHealth()

    suspend fun uploadPdf(file: MultipartBody.Part) = api.uploadPdf(file)

    suspend fun askQuestion(question: String) = api.askQuestion(AskRequest(question))
}
