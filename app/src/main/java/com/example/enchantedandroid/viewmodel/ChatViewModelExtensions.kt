package com.example.enchantedandroid.viewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** Extension to set dark mode preference */
fun ChatViewModel.setDarkMode(enabled: Boolean) {
    // Use a new coroutine scope to avoid accessing viewModelScope directly from extension
    CoroutineScope(Dispatchers.Main).launch {
        settingsRepository.setDarkMode(enabled)
    }
}