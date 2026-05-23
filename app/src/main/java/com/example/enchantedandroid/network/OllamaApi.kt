package com.example.enchantedandroid.network

import com.squareup.moshi.Moshi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

// Request payload for Ollama's generate endpoint.
data class OllamaGenerateRequest(
    val model: String,
    val prompt: String,
    val stream: Boolean = false,
    val images: List<String>? = null
)

// Simplified response model – only the fields we need.
data class OllamaGenerateResponse(
    val model: String?,
    val created_at: String?,
    val response: String?,
    val done: Boolean?,
    val done_reason: String?,
    val context: List<Int>?
)

interface OllamaApi {
    @POST("/api/generate")
    suspend fun generate(@Body request: OllamaGenerateRequest): OllamaGenerateResponse

    companion object {
        fun create(baseUrl: String): OllamaApi {
            // Ensure the baseUrl ends with a trailing slash as required by Retrofit.
            val normalized = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
            val moshi = Moshi.Builder().build()
            return Retrofit.Builder()
                .baseUrl(normalized)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(OllamaApi::class.java)
        }
    }
}
