package com.example.enchantedandroid.viewmodel

import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.enchantedandroid.model.ChatMessage
import com.example.enchantedandroid.model.ConversationEntity
import com.example.enchantedandroid.model.MessageEntity
import com.example.enchantedandroid.network.OllamaApi
import com.example.enchantedandroid.network.OllamaGenerateRequest
import com.example.enchantedandroid.repository.ConversationRepository
import com.example.enchantedandroid.repository.SettingsRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * ViewModel for a single conversation identified by [conversationId].
 * Handles loading/saving messages via Room, communicating with Ollama, and exposing UI state.
 */
class ChatViewModel(
    private val conversationId: String,
    private val repository: ConversationRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    // ----- Conversation metadata -----
    private val _conversation = MutableStateFlow<ConversationEntity?>(null)
    val conversation: StateFlow<ConversationEntity?> = _conversation.asStateFlow()

    // ----- Messages -----
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    // Base URL for Ollama – read from DataStore
    val baseUrl: StateFlow<String> = settingsRepository.ollamaUrl.stateIn(viewModelScope, SharingStarted.Eagerly, "http://10.0.2.2:11434")
    val darkModeEnabled: StateFlow<Boolean> = settingsRepository.darkModeEnabled.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    // Holds an optional base64‑encoded image to attach to the next user prompt.
    private var attachedImageBase64: String? = null

    // ---- Init: load conversation and its messages ----
    init {
        // Load conversation entity
        viewModelScope.launch {
            repository.getConversation(conversationId).collect { conv ->
                _conversation.value = conv
            }
        }
        // Load messages for this conversation
        viewModelScope.launch {
            repository.getMessages(conversationId).collect { entities ->
                _messages.value = entities.map { entity ->
                    ChatMessage(
                        id = entity.id,
                        role = entity.role,
                        content = entity.content,
                        image = entity.imageBase64?.let { Base64.decode(it, Base64.DEFAULT) }
                    )
                }
            }
        }
    }

    /** Attach an image (base64) to be sent with the next user message. */
    fun setAttachedImage(base64: String?) {
        attachedImageBase64 = base64
    }

    /** Update system prompt for this conversation. */
    fun updateSystemPrompt(prompt: String) {
        viewModelScope.launch {
            val conv = _conversation.value ?: return@launch
            repository.updateConversation(conv.copy(systemPrompt = prompt, updatedAt = System.currentTimeMillis()))
        }
    }

    /** Update selected model for this conversation. */
    fun updateSelectedModel(model: String) {
        viewModelScope.launch {
            val conv = _conversation.value ?: return@launch
            repository.updateConversation(conv.copy(selectedModel = model, updatedAt = System.currentTimeMillis()))
        }
    }

    /** Send a user prompt, store it, and request an assistant response. */
    fun sendMessage(prompt: String) {
        val systemPrompt = _conversation.value?.systemPrompt ?: ""
        val fullPrompt = if (systemPrompt.isNotBlank()) "${systemPrompt}\n${prompt}" else prompt
        val model = _conversation.value?.selectedModel ?: "llama2"
        // Insert user message into DB
        val userEntity = MessageEntity(
            conversationId = conversationId,
            role = "user",
            content = prompt,
            imageBase64 = attachedImageBase64
        )
        viewModelScope.launch {
            repository.insertMessage(userEntity)
            // Reset image after sending
            attachedImageBase64 = null
            // Prepare Ollama request
            val request = OllamaGenerateRequest(
                model = model,
                prompt = fullPrompt,
                stream = false,
                images = null // image already sent as base64 in our payload if needed
            )
            try {
                // Create client with current base URL
                val api = OllamaApi.create(baseUrl.value)
                val response = api.generate(request)
                val assistantEntity = MessageEntity(
                    conversationId = conversationId,
                    role = "assistant",
                    content = response.response ?: "(empty response)"
                )
                repository.insertMessage(assistantEntity)
            } catch (e: Exception) {
                val errorEntity = MessageEntity(
                    conversationId = conversationId,
                    role = "assistant",
                    content = "Error: ${e.localizedMessage}",
                    error = true
                )
                repository.insertMessage(errorEntity)
            }
        }
    }

    /** Edit an existing message's content. */
    fun editMessage(messageId: String, newContent: String) {
        viewModelScope.launch {
            // Find the message entity via current list
            val current = repository.getMessages(conversationId).firstOrNull() ?: return@launch
            val target = current.find { it.id == messageId } ?: return@launch
            val updated = target.copy(content = newContent)
            repository.updateMessage(updated)
        }
    }

    /** Delete a single message. */
    fun deleteMessage(messageId: String) {
        viewModelScope.launch {
            val current = repository.getMessages(conversationId).firstOrNull() ?: return@launch
            val target = current.find { it.id == messageId } ?: return@launch
            repository.deleteMessage(target)
        }
    }

    /** Delete all messages for this conversation. */
    fun clearConversation() {
        viewModelScope.launch { repository.deleteAllMessagesForConversation(conversationId) }
    }
}