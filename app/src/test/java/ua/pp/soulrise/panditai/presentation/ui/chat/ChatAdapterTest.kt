package ua.pp.soulrise.panditai.presentation.ui.chat

import android.content.ClipboardManager
import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import io.mockk.*
import org.junit.Before
import org.junit.Test
import ua.pp.soulrise.panditai.R
import ua.pp.soulrise.panditai.data.model.ChatMessage
import kotlin.test.assertEquals

class ChatAdapterTest {
    private lateinit var adapter: ChatAdapter
    
    @Before
    fun setup() {
        adapter = ChatAdapter()
    }
    
    @Test
    fun `getItemViewType should return correct type for different messages`() {
        // Arrange
        val userMessage = ChatMessage("Hello", isUser = true)
        val aiMessage = ChatMessage("Hi", isUser = false)
        val errorMessage = ChatMessage("Error", isUser = false, isError = true)
        
        adapter.updateMessages(listOf(userMessage, aiMessage, errorMessage))
        
        // Act & Assert
        assertEquals(ChatAdapter.USER_MESSAGE, adapter.getItemViewType(0))
        assertEquals(ChatAdapter.AI_MESSAGE, adapter.getItemViewType(1))
        assertEquals(ChatAdapter.ERROR_MESSAGE, adapter.getItemViewType(2))
    }
    
    @Test
    fun `getItemCount should return correct size`() {
        // Arrange
        val messages = listOf(
            ChatMessage("1", true),
            ChatMessage("2", false),
            ChatMessage("3", false, true)
        )
        
        // Act
        adapter.updateMessages(messages)
        
        // Assert
        assertEquals(3, adapter.getItemCount())
    }
    
    @Test
    fun `updateMessages should update the list`() {
        // Arrange
        val initialMessages = listOf(ChatMessage("1", true))
        val newMessages = listOf(
            ChatMessage("2", false),
            ChatMessage("3", true)
        )
        
        // Act
        adapter.updateMessages(initialMessages)
        assertEquals(1, adapter.getItemCount())
        
        adapter.updateMessages(newMessages)
        
        // Assert
        assertEquals(2, adapter.getItemCount())
        assertEquals(newMessages, adapter.getMessages())
    }
    
    @Test
    fun `onLongClick should copy message to clipboard`() {
        // Arrange
        val message = "Test message"
        val chatMessage = ChatMessage(message, true)
        
        val view = mockk<View>(relaxed = true)
        val context = mockk<Context>(relaxed = true)
        val clipboard = mockk<ClipboardManager>(relaxed = true)
        val textView = mockk<TextView>(relaxed = true)
        
        every { view.context } returns context
        every { view.findViewById<TextView>(R.id.messageText) } returns textView
        every { ContextCompat.getSystemService(context, ClipboardManager::class.java) } returns clipboard
        
        val viewHolder = ChatAdapter.MessageViewHolder(view)
        
        // Act
        viewHolder.bind(chatMessage)
        
        // Verify
        verify { 
            textView.text = any()
            view.setOnLongClickListener(any())
        }
    }
}
