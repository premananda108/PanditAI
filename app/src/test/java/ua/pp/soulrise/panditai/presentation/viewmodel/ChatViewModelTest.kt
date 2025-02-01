package ua.pp.soulrise.panditai.presentation.viewmodel

import android.app.Application
import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import ua.pp.soulrise.panditai.R
import ua.pp.soulrise.panditai.data.remote.ApiResponse
import ua.pp.soulrise.panditai.data.remote.ApiService
import ua.pp.soulrise.panditai.data.remote.RetrofitInstance
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModelTest {
    private lateinit var viewModel: ChatViewModel
    private lateinit var application: Application
    private lateinit var apiService: ApiService
    
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        // Мокаем Application для получения строковых ресурсов
        application = mockk {
            every { getString(R.string.error_no_response) } returns "Server is not responding"
            every { getString(R.string.error_unknown) } returns "Unknown error"
            every { getString(R.string.error_server) } returns "Server error"
        }
        
        // Мокаем ApiService
        apiService = mockk()
        
        // Создаем ViewModel с мокнутыми зависимостями
        viewModel = ChatViewModel(application)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when sending message successfully, should add user message and AI response`() = runTest {
        // Arrange
        val userMessage = "Hello"
        val aiResponse = "Hi there!"
        
        // Мокаем успешный ответ от сервера
        coEvery { 
            RetrofitInstance.api.sendMessage(any())
        } returns Response.success(ApiResponse("ok", aiResponse))

        // Act & Assert
        viewModel.messages.test {
            // Начальное состояние - пустой список
            assertEquals(emptyList(), awaitItem())
            
            // Отправляем сообщение
            viewModel.sendMessage(userMessage)
            
            // Проверяем, что добавилось сообщение пользователя
            val messagesWithUserMessage = awaitItem()
            assertEquals(1, messagesWithUserMessage.size)
            assertTrue(messagesWithUserMessage[0].isUser)
            assertEquals(userMessage, messagesWithUserMessage[0].message)
            
            // Проверяем, что добавился ответ AI
            val messagesWithAiResponse = awaitItem()
            assertEquals(2, messagesWithAiResponse.size)
            assertFalse(messagesWithAiResponse[1].isUser)
            assertEquals(aiResponse, messagesWithAiResponse[1].message)
        }
    }

    @Test
    fun `when server returns error, should show error message`() = runTest {
        // Arrange
        val userMessage = "Hello"
        val errorMessage = "Error occurred"
        
        // Мокаем ответ с ошибкой
        coEvery { 
            RetrofitInstance.api.sendMessage(any())
        } returns Response.success(ApiResponse("error", errorMessage))

        // Act & Assert 
        viewModel.messages.test {
            // Начальное состояние
            assertEquals(emptyList(), awaitItem())
            
            // Отправляем сообщение
            viewModel.sendMessage(userMessage)
            
            // Проверяем сообщение пользователя
            val messagesWithUserMessage = awaitItem()
            assertEquals(1, messagesWithUserMessage.size)
            
            // Проверяем сообщение об ошибке
            val messagesWithError = awaitItem()
            assertEquals(2, messagesWithError.size)
            assertTrue(messagesWithError[1].isError)
            assertEquals(errorMessage, messagesWithError[1].message)
        }
    }

    @Test
    fun `when network error occurs, should show network error message`() = runTest {
        // Arrange
        val userMessage = "Hello"
        
        // Мокаем сетевую ошибку
        coEvery { 
            RetrofitInstance.api.sendMessage(any())
        } throws java.net.SocketTimeoutException()

        // Act & Assert
        viewModel.messages.test {
            // Начальное состояние
            assertEquals(emptyList(), awaitItem())
            
            // Отправляем сообщение
            viewModel.sendMessage(userMessage)
            
            // Проверяем сообщение пользователя
            val messagesWithUserMessage = awaitItem()
            assertEquals(1, messagesWithUserMessage.size)
            
            // Проверяем сообщение об ошибке
            val messagesWithError = awaitItem()
            assertEquals(2, messagesWithError.size)
            assertTrue(messagesWithError[1].isError)
            assertEquals(application.getString(R.string.error_no_response), messagesWithError[1].message)
        }
    }
}
