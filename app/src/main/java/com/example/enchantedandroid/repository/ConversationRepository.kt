package com.example.enchantedandroid.repository

import com.example.enchantedandroid.data.AppDatabase
import com.example.enchantedandroid.model.ConversationEntity
import com.example.enchantedandroid.model.MessageEntity
import kotlinx.coroutines.flow.Flow

class ConversationRepository(private val db: AppDatabase) {
    // Conversations
    val conversations: Flow<List<ConversationEntity>> = db.conversationDao().getAll()

    fun getConversation(id: String): Flow<ConversationEntity?> = db.conversationDao().getById(id)

    suspend fun insertConversation(conversation: ConversationEntity) = db.conversationDao().insert(conversation)

    suspend fun updateConversation(conversation: ConversationEntity) = db.conversationDao().update(conversation)

    suspend fun deleteConversation(conversation: ConversationEntity) = db.conversationDao().delete(conversation)

    suspend fun deleteAllConversations() = db.conversationDao().deleteAll()

    // Messages
    fun getMessages(conversationId: String): Flow<List<MessageEntity>> = db.messageDao().getMessagesForConversation(conversationId)

    suspend fun insertMessage(message: MessageEntity) = db.messageDao().insert(message)

    suspend fun updateMessage(message: MessageEntity) = db.messageDao().update(message)

    suspend fun deleteMessage(message: MessageEntity) = db.messageDao().delete(message)

    suspend fun deleteAllMessagesForConversation(conversationId: String) = db.messageDao().deleteAllForConversation(conversationId)
}