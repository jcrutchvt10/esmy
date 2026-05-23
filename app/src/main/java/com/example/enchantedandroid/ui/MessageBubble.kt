package com.example.enchantedandroid.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material.icons.filled.PlayArrow
import android.speech.tts.TextToSpeech
import androidx.compose.material3.IconButton
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.enchantedandroid.model.ChatMessage
import com.halilibo.compose.richtext.RichText
import com.halilibo.compose.richtext.markdown.Markdown

@Composable
fun MessageBubble(message: ChatMessage) {
    val isUser = message.role == "user"
    val background = if (isUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
    val alignment = if (isUser) Arrangement.End else Arrangement.Start

    Row(
        horizontalArrangement = alignment,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .background(background, shape = MaterialTheme.shapes.medium)
                .padding(8.dp)
                .widthIn(max = 250.dp)
        ) {
            Column {
                RichText {
                    Markdown(content = message.content)
                }
                if (!isUser) {
                    IconButton(onClick = {
                        // Initialize TTS and speak the message content.
                        val tts = TextToSpeech(LocalContext.current) { }
                        tts.speak(message.content, TextToSpeech.QUEUE_FLUSH, null, null)
                    }) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Read aloud")
                    }
                }
            }
        }
    }
}
