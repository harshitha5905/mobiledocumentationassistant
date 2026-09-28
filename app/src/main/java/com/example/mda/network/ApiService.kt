package com.example.mda.network

import com.example.mda.data.AskRequest
import com.example.mda.data.AskResponse
import com.example.mda.data.HealthResponse
import com.example.mda.data.UploadResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("health")
    suspend fun checkHealth(): Response<HealthResponse>

    @Multipart
    @POST("upload")
    suspend fun uploadPdf(
        @Part file: MultipartBody.Part
    ): Response<UploadResponse>

    @POST("ask")
    suspend fun askQuestion(
        @Body request: AskRequest
    ): Response<AskResponse>
}
