package com.example.enchantedandroid.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.enchantedandroid.repository.ConversationRepository
import com.example.enchantedandroid.repository.SettingsRepository
import com.example.enchantedandroid.data.AppDatabase

class ChatViewModelFactory(
    private val conversationId: String,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            val db = AppDatabase.getInstance(context)
            val repo = ConversationRepository(db)
            val settingsRepo = SettingsRepository(context)
            @Suppress("UNCHECKED_CAST")
            return ChatViewModel(conversationId, repo, settingsRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}