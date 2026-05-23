package com.example.enchantedandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.enchantedandroid.ui.theme.EnchantedTheme
import com.example.enchantedandroid.ui.ChatScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EnchantedTheme {
                ChatScreen()
            }
        }
    }
}
