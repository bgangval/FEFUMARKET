package com.example.fefumarket.ui.chat

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fefumarket.R
import com.example.fefumarket.data.models.ChatItem
import com.example.fefumarket.data.models.MessageItem
import com.example.fefumarket.data.repository.MessagesManager

class MessageActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var inputMessage: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var adapter: MessageAdapter

    private lateinit var chatId: String
    private lateinit var chat: ChatItem
    private val messages = mutableListOf<MessageItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        // ==== Получаем данные из Intent ====
        val sellerName = intent.getStringExtra("SELLER_NAME") ?: "Продавец"
        val productName = intent.getStringExtra("PRODUCT_NAME") ?: "Товар"
        val avatarUri = intent.getStringExtra("AVATAR_URI") ?: ""  // теперь строка
        chatId = intent.getStringExtra("CHAT_ID") ?: "${sellerName}_$productName"

        // ==== Получаем или создаём чат ====
        chat = MessagesManager.getOrCreateChat(chatId, sellerName, productName, avatarUri)

        // ==== Устанавливаем имя и товар в шапку ====
        findViewById<TextView>(R.id.chatTitle).text = sellerName
        findViewById<TextView>(R.id.chatSubtitle).text = productName

        // ==== Кнопка "Назад" ====
        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        // ==== Настройка RecyclerView ====
        recyclerView = findViewById(R.id.recyclerViewMessages)
        inputMessage = findViewById(R.id.inputMessage)
        sendButton = findViewById(R.id.sendButton)

        adapter = MessageAdapter(messages)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // ==== Загрузка последнего сообщения (если есть) ====
        chat.lastMessage.takeIf { it.isNotEmpty() }?.let {
            messages.add(MessageItem(it, isUser = false))
        }

        // ==== Отправка нового сообщения ====
        sendButton.setOnClickListener {
            val text = inputMessage.text.toString().trim()
            if (text.isNotEmpty()) {
                // Добавляем сообщение в список
                messages.add(MessageItem(text, isUser = true))
                adapter.notifyItemInserted(messages.size - 1)
                recyclerView.scrollToPosition(messages.size - 1)
                inputMessage.text.clear()

                // Обновляем данные в менеджере чатов
                MessagesManager.addMessage(chatId, text)
            }
        }
    }
}