package ua.pp.soulrise.panditai.presentation.ui.chat

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import ua.pp.soulrise.panditai.R
import ua.pp.soulrise.panditai.data.model.ChatMessage

class ChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val messages = mutableListOf<ChatMessage>()

    fun getMessages(): List<ChatMessage> {
        return messages.toList()
    }

    companion object {
        const val USER_MESSAGE = 0
        const val AI_MESSAGE = 1
        const val ERROR_MESSAGE = 2
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            messages[position].isError -> ERROR_MESSAGE
            messages[position].isUser -> USER_MESSAGE
            else -> AI_MESSAGE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutRes = when (viewType) {
            USER_MESSAGE -> R.layout.item_user_message
            ERROR_MESSAGE -> R.layout.item_error_message
            else -> R.layout.item_ai_message
        }
        val view = LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MessageViewHolder).bind(messages[position])
    }

    override fun getItemCount(): Int = messages.size

    fun updateMessages(newMessages: List<ChatMessage>) {
        messages.clear()
        messages.addAll(newMessages)
        notifyDataSetChanged()
    }

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.messageText)

        fun bind(message: ChatMessage) {
            messageText.text = HtmlCompat.fromHtml(message.message, HtmlCompat.FROM_HTML_MODE_COMPACT)
            
            // Добавляем обработчик длительного нажатия
            itemView.setOnLongClickListener { view ->
                val clipboard = ContextCompat.getSystemService(view.context, ClipboardManager::class.java)
                val clip = ClipData.newPlainText("Message", message.message)
                clipboard?.setPrimaryClip(clip)
                
                // Показываем уведомление о копировании
                Toast.makeText(view.context, view.context.getString(R.string.message_copied), Toast.LENGTH_SHORT).show()
                true
            }
        }
    }
}