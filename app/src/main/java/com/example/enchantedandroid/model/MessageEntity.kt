package com.example.enchantedandroid.model

import androidx.room.*
import java.util.UUID

@Entity(
    tableName = "message",
    foreignKeys = [ForeignKey(
        entity = ConversationEntity::class,
        parentColumns = ["id"],
        childColumns = ["conversationId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["conversationId"])]
)
data class MessageEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val conversationId: String,
    var role: String,
    var content: String,
    var imageBase64: String? = null,
    var done: Boolean = false,
    var error: Boolean = false,
    var createdAt: Long = System.currentTimeMillis()
)