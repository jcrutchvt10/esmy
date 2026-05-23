package com.example.enchantedandroid.repository

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first
import androidx.datastore.core.DataStore

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {
    // Ollama endpoint URL
    val ollamaUrl: Flow<String> = context.settingsDataStore.data.map { prefs ->
        prefs[PreferencesKeys.OLLAMA_URL] ?: "http://10.0.2.2:11434"
    }

    suspend fun setOllamaUrl(url: String) {
        context.settingsDataStore.edit { settings ->
            settings[PreferencesKeys.OLLAMA_URL] = url
        }
    }

    // Dark mode flag
    val darkModeEnabled: Flow<Boolean> = context.settingsDataStore.data.map { prefs ->
        prefs[PreferencesKeys.DARK_MODE] ?: false
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.settingsDataStore.edit { settings ->
            settings[PreferencesKeys.DARK_MODE] = enabled
        }
    }
}

private object PreferencesKeys {
    val OLLAMA_URL = stringPreferencesKey("ollama_url")
    val DARK_MODE = booleanPreferencesKey("dark_mode")
}