package com.example.fefumarket.ui.chat

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fefumarket.R
import com.example.fefumarket.data.models.ChatItem
import com.example.fefumarket.data.models.MessageItem
import com.example.fefumarket.data.models.api.toMessageItem
import com.example.fefumarket.data.repository.ChatRepository
import com.example.fefumarket.data.repository.MessagesManager
import com.example.fefumarket.data.repository.SessionManager
import com.example.fefumarket.network.RetrofitClient
import kotlinx.coroutines.launch

class MessageActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var inputMessage: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var adapter: MessageAdapter

    private lateinit var chatId: String
    private var apiChatId: Int? = null
    private lateinit var chat: ChatItem
    private lateinit var chatRepository: ChatRepository
    private lateinit var sessionManager: SessionManager
    private val messages = mutableListOf<MessageItem>()

    private fun hideKeyboard(editText: EditText) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
        imm.hideSoftInputFromWindow(editText.windowToken, 0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        // 🔹 Получаем данные из Intent, переданные из ChatAdapter
        val sellerName = intent.getStringExtra("SELLER_NAME") ?: "Продавец"
        val productName = intent.getStringExtra("PRODUCT_NAME") ?: "Товар"
        val avatarUri = intent.getStringExtra("AVATAR_URI") ?: ""
        chatId = intent.getStringExtra("CHAT_ID") ?: "${sellerName}_$productName"
        apiChatId = intent.getIntExtra("API_CHAT_ID", -1).takeIf { it != -1 }

        // 🔹 Инициализация репозитория
        sessionManager = SessionManager(this)
        val api = RetrofitClient.create(this)
        chatRepository = ChatRepository(api, sessionManager)

        // 🔹 Получаем или создаём чат через MessagesManager (локальный кэш)
        chat = MessagesManager.getOrCreateChat(chatId, sellerName, productName, avatarUri)

        // 🔹 Загружаем сообщения с сервера, если есть API chat ID
        if (apiChatId != null) {
            loadMessages(apiChatId!!)
        }

        // Заголовок чата
        findViewById<TextView>(R.id.chatTitle).text = sellerName
        findViewById<TextView>(R.id.chatSubtitle).text = productName

        // Кнопка "Назад"
        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        // Настройка RecyclerView для сообщений
        recyclerView = findViewById(R.id.recyclerViewMessages)
        inputMessage = findViewById(R.id.inputMessage)
        sendButton = findViewById(R.id.sendButton)
        adapter = MessageAdapter(messages)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // 🔹 Отправка нового сообщения через API
        sendButton.setOnClickListener {
            val text = inputMessage.text.toString().trim()
            if (text.isNotEmpty()) {
                if (apiChatId != null) {
                    // Отправляем через API
                    lifecycleScope.launch {
                        try {
                            val messageOut = chatRepository.sendMessage(apiChatId!!, text)
                            val currentUserId = sessionManager.getLogin()?.let { 
                                // Здесь нужно получить ID текущего пользователя
                                // Пока используем простую проверку
                                0 // Заглушка
                            } ?: 0
                            val messageItem = messageOut.toMessageItem(currentUserId)
                            messages.add(messageItem)
                            adapter.notifyItemInserted(messages.size - 1)
                            recyclerView.scrollToPosition(messages.size - 1)
                            inputMessage.text.clear()
                            MessagesManager.addMessage(chatId, text)
                        } catch (e: Exception) {
                            Toast.makeText(
                                this@MessageActivity,
                                "Ошибка отправки: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    // Fallback на локальное сохранение
                    messages.add(MessageItem(text, isUser = true))
                    adapter.notifyItemInserted(messages.size - 1)
                    recyclerView.scrollToPosition(messages.size - 1)
                    inputMessage.text.clear()
                    MessagesManager.addMessage(chatId, text)
                }
            }
        }

        val stickerButton: ImageButton = findViewById(R.id.stickerButton)
        val stickerRecycler: RecyclerView = findViewById(R.id.recyclerViewStickers)

        val stickers = listOf(
            R.drawable.sticker1,
            R.drawable.sticker2,
            R.drawable.sticker3,
            R.drawable.sticker4,
            R.drawable.sticker5,
            R.drawable.sticker6
        )

        // 🔹 Настройка RecyclerView со стикерами и логика отправки выбранного стикера
        val stickerAdapter = StickerAdapter(stickers) { stickerRes ->
            messages.add(MessageItem(text = null, isUser = true, isSticker = true, stickerRes = stickerRes))
            adapter.notifyItemInserted(messages.size - 1)
            recyclerView.scrollToPosition(messages.size - 1)
            stickerRecycler.visibility = View.GONE
        }
        stickerRecycler.adapter = stickerAdapter
        stickerRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        stickerButton.setOnClickListener {
            if (stickerRecycler.visibility == View.GONE) {
                hideKeyboard(inputMessage)
                stickerRecycler.visibility = View.VISIBLE
            } else {
                stickerRecycler.visibility = View.GONE
            }
        }
    }

    // 🔹 Загрузка сообщений с сервера
    private fun loadMessages(chatId: Int) {
        lifecycleScope.launch {
            try {
                val chatOut = chatRepository.getChat(chatId)
                val currentUserId = sessionManager.getLogin()?.let { 
                    // Здесь нужно получить ID текущего пользователя
                    // Пока используем простую проверку
                    0 // Заглушка
                } ?: 0
                
                messages.clear()
                chatOut.messages.forEach { messageOut ->
                    messages.add(messageOut.toMessageItem(currentUserId))
                }
                adapter.notifyDataSetChanged()
                
                if (messages.isNotEmpty()) {
                    recyclerView.scrollToPosition(messages.size - 1)
                }
            } catch (e: Exception) {
                // Если ошибка, используем локальные данные
                chat.lastMessage.takeIf { it.isNotEmpty() }?.let {
                    messages.add(MessageItem(it, isUser = false))
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }
}