package com.example.enchantedandroid.model

import java.util.UUID

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val role: String,
    val content: String,
    val image: ByteArray? = null
)
