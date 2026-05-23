package com.example.enchantedandroid.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.compose.ui.platform.LocalContext
import android.util.Base64
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.enchantedandroid.model.ChatMessage
import androidx.navigation.NavHostController
import com.example.enchantedandroid.viewmodel.ChatViewModel

@Composable
fun ChatScreen(
    viewModel: ChatViewModel = viewModel(),
    navController: NavHostController
) {
    var showSettings by remember { mutableStateOf(false) }
    val messages by viewModel.messages.collectAsState()
    var prompt by remember { mutableStateOf("") }
    var editMessage by remember { mutableStateOf<ChatMessage?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val voiceRecognizer = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val matches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!matches.isNullOrEmpty()) {
                prompt = matches[0]
            }
        }
    }
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val stream = context.contentResolver.openInputStream(uri)
            val bytes = stream?.readBytes()
            stream?.close()
            val base64 = bytes?.let { Base64.encodeToString(it, Base64.DEFAULT) }
            viewModel.setAttachedImage(base64)
        }
    }

    if (showSettings) {
SettingsScreen(
            currentUrl = viewModel.baseUrl.collectAsState().value,
            onSave = { newUrl ->
                viewModel.updateBaseUrl(newUrl)
                showSettings = false
            },
            onBack = { showSettings = false },
            darkModeEnabled = viewModel.darkModeEnabled.collectAsState().value,
            onDarkModeChange = { viewModel.setDarkMode(it) },
            systemPrompt = viewModel.conversation.collectAsState().value?.systemPrompt ?: "",
            onSystemPromptChange = { viewModel.updateSystemPrompt(it) },
            selectedModel = viewModel.conversation.collectAsState().value?.selectedModel ?: "",
            availableModels = listOf("llama2", "mistral", "phi"),
            onModelSelect = { viewModel.updateSelectedModel(it) }
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Enchanted Android") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showSettings = true }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(messages) { message ->
                    MessageBubble(message, onLongPress = {
                        editMessage = message
                        showEditDialog = true
                    })
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                TextField(
                    value = prompt,
                    onValueChange = { prompt = it },
                    placeholder = { Text("Enter prompt...") },
                    modifier = Modifier.weight(1f)
                )
                // Voice input button
                IconButton(
                    onClick = {
                        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                            putExtra(
                                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                            )
                        }
                        voiceRecognizer.launch(intent)
                    }
                ) {
                    Icon(Icons.Default.Mic, contentDescription = "Voice input")
                }
                // Image attachment button
                IconButton(
                    onClick = { imagePicker.launch("image/*") }
                ) {
                    Icon(Icons.Default.Photo, contentDescription = "Attach image")
                }
                // Send button
                IconButton(
                    onClick = {
                        viewModel.sendMessage(prompt)
                        prompt = ""
                    },
                    enabled = prompt.isNotBlank()
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send")
                }
            }
        }
    }

    // Edit message dialog
    if (showEditDialog && editMessage != null) {
        var newContent by remember { mutableStateOf(editMessage!!.content) }
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Message") },
            text = {
                TextField(
                    value = newContent,
                    onValueChange = { newContent = it },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.editMessage(editMessage!!.id, newContent)
                    showEditDialog = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                Button(onClick = { showEditDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
