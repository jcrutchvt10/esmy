package com.example.enchantedandroid.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.enchantedandroid.repository.ConversationRepository
import com.example.enchantedandroid.data.AppDatabase

class ConversationListViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ConversationListViewModel::class.java)) {
            val db = AppDatabase.getInstance(context)
            val repo = ConversationRepository(db)
            @Suppress("UNCHECKED_CAST")
            return ConversationListViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}