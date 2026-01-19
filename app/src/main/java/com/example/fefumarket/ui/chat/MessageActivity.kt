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
import android.view.View

class MessageActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var inputMessage: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var adapter: MessageAdapter

    private lateinit var chatId: String
    private lateinit var chat: ChatItem
    private val messages = mutableListOf<MessageItem>()

    private fun hideKeyboard(editText: EditText) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
        imm.hideSoftInputFromWindow(editText.windowToken, 0)
    }

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

        val stickerButton: ImageButton = findViewById(R.id.stickerButton)
        val stickerRecycler: RecyclerView = findViewById(R.id.recyclerViewStickers)

        // Список стикеров (локальные PNG или URL)
        val stickers = listOf(
            R.drawable.sticker1,
            R.drawable.sticker2,
            R.drawable.sticker3,
            R.drawable.sticker4,
            R.drawable.sticker5,
            R.drawable.sticker6
        )

        // Настройка RecyclerView стикеров
        val stickerAdapter = StickerAdapter(stickers) { stickerRes ->
            // Стикер выбран → добавляем как сообщение
            messages.add(MessageItem(text = null, isUser = true, isSticker = true, stickerRes = stickerRes))
            adapter.notifyItemInserted(messages.size - 1)
            recyclerView.scrollToPosition(messages.size - 1)
            stickerRecycler.visibility = View.GONE
        }
        stickerRecycler.adapter = stickerAdapter
        stickerRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // Кнопка стикеров
        stickerButton.setOnClickListener {
            if (stickerRecycler.visibility == View.GONE) {
                hideKeyboard(inputMessage)
                stickerRecycler.visibility = View.VISIBLE
            } else {
                stickerRecycler.visibility = View.GONE
            }
        }
    }
}