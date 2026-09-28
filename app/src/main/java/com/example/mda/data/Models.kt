package com.example.mda.data

import com.google.gson.annotations.SerializedName

data class HealthResponse(
    val status: String
)

data class UploadResponse(
    val filename: String,
    val message: String
)

data class AskRequest(
    val question: String
)

data class AskResponse(
    val answer: String,
    @SerializedName("retrieved_chunks")
    val retrievedChunks: List<String>
)

data class ErrorResponse(
    val detail: String
)
