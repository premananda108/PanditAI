package ua.pp.soulrise.panditai.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ua.pp.soulrise.panditai.R
import ua.pp.soulrise.panditai.data.model.ChatMessage
import ua.pp.soulrise.panditai.data.remote.ApiRequest
import ua.pp.soulrise.panditai.data.remote.ApiData
import ua.pp.soulrise.panditai.data.remote.RetrofitInstance
import ua.pp.soulrise.panditai.data.config.ApiConfig

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun sendMessage(message: String) {
        // Сразу добавляем сообщение пользователя в список
        _messages.value = _messages.value + listOf(ChatMessage(message, true))

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val request = ApiRequest(
                    data = ApiData(
                        message = message,
                        image = "",
                        idb = ApiConfig.IDB
                    )
                )
                val response = RetrofitInstance.api.sendMessage(request)
                if (response.isSuccessful && response.body()?.reaction == "ok") {
                    val aiResponse = response.body()?.response ?: getApplication<Application>().getString(R.string.error_no_response)
                    // Добавляем ответ ИИ в список
                    _messages.value = _messages.value + listOf(ChatMessage(aiResponse, false))
                } else {
                    // Если сервер вернул ошибку, добавляем её в чат
                    val errorMessage = response.body()?.response ?: getApplication<Application>().getString(R.string.error_server)
                    _messages.value = _messages.value + listOf(
                        ChatMessage(
                            message = errorMessage,
                            isUser = false,
                            isError = true
                        )
                    )
                    _error.value = errorMessage
                }
            } catch (e: java.net.SocketTimeoutException) {
                val errorMessage = getApplication<Application>().getString(R.string.error_no_response)
                _messages.value = _messages.value + listOf(
                    ChatMessage(
                        message = errorMessage,
                        isUser = false,
                        isError = true
                    )
                )
                _error.value = errorMessage
            } catch (e: Exception) {
                val errorMessage = e.message ?: getApplication<Application>().getString(R.string.error_unknown)
                _messages.value = _messages.value + listOf(
                    ChatMessage(
                        message = errorMessage,
                        isUser = false,
                        isError = true
                    )
                )
                _error.value = errorMessage
            } finally {
                _isLoading.value = false
            }
        }
    }
}