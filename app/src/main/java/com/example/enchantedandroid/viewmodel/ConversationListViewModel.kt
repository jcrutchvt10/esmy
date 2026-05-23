package com.example.enchantedandroid.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.enchantedandroid.model.ConversationEntity
import com.example.enchantedandroid.repository.ConversationRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ConversationListViewModel(private val repository: ConversationRepository) : ViewModel() {
    val conversations: StateFlow<List<ConversationEntity>> = repository.conversations
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun createConversation(name: String = "New Chat") {
        viewModelScope.launch {
            repository.insertConversation(ConversationEntity(name = name))
        }
    }

    fun deleteConversation(conversation: ConversationEntity) {
        viewModelScope.launch { repository.deleteConversation(conversation) }
    }

    fun deleteAllConversations() {
        viewModelScope.launch { repository.deleteAllConversations() }
    }
}