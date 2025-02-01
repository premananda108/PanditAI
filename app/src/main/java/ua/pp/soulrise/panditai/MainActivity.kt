package ua.pp.soulrise.panditai

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import ua.pp.soulrise.panditai.data.model.ChatMessage
import ua.pp.soulrise.panditai.presentation.ui.chat.ChatAdapter
import ua.pp.soulrise.panditai.presentation.viewmodel.ChatViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var chatAdapter: ChatAdapter
    private val viewModel: ChatViewModel by viewModels()

    // Ссылки на UI-элементы
    private lateinit var sendButton: android.widget.Button
    private lateinit var loadingIndicator: android.widget.ProgressBar
    private lateinit var messageInput: android.widget.EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Инициализация UI-элементов
        val recyclerView = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.chatRecyclerView)
        sendButton = findViewById(R.id.sendButton)
        loadingIndicator = findViewById(R.id.loadingIndicator)
        messageInput = findViewById(R.id.messageInput)

        // Настройка RecyclerView
        chatAdapter = ChatAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = chatAdapter
        recyclerView.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()

        // Подписка на изменения списка сообщений
        lifecycleScope.launchWhenStarted {
            viewModel.messages.collect { messages ->
                chatAdapter.updateMessages(messages)
                recyclerView.scrollToPosition(messages.size - 1) // Прокручиваем вниз
            }
        }

        // Подписка на состояние загрузки
        lifecycleScope.launchWhenStarted {
            viewModel.isLoading.collect { isLoading ->
                updateLoadingState(isLoading)
            }
        }

        // Подписка на ошибки
        lifecycleScope.launchWhenStarted {
            viewModel.error.collect { error ->
                error?.let { showError(it) }
            }
        }

        // Настройка панели ввода
        sendButton.setOnClickListener {
            val message = messageInput.text.toString().trim()
            if (message.isNotEmpty()) {
                viewModel.sendMessage(message)
                messageInput.text.clear() // Очищаем поле ввода только после отправки
            }
        }
    }

    private fun updateLoadingState(isLoading: Boolean) {
        sendButton.visibility = if (isLoading) View.GONE else View.VISIBLE
        loadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}