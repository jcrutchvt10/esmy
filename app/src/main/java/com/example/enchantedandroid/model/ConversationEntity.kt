package com.example.enchantedandroid.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "conversation")
data class ConversationEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    var name: String = "New Chat",
    var systemPrompt: String = "",
    var selectedModel: String = "llama2",
    var createdAt: Long = System.currentTimeMillis(),
    var updatedAt: Long = System.currentTimeMillis()
)