package ua.pp.soulrise.panditai.data.model

data class ChatMessage(
    val message: String,
    val isUser: Boolean,
    val isError: Boolean = false
)