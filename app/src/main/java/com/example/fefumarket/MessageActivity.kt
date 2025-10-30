package com.example.fefumarket

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MessageActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var inputMessage: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var adapter: MessageAdapter

    private val messages = mutableListOf<MessageItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        val chatName = intent.getStringExtra("chatName") ?: "Чат"
        findViewById<TextView>(R.id.chatTitle).text = chatName

        recyclerView = findViewById(R.id.recyclerViewMessages)
        inputMessage = findViewById(R.id.inputMessage)
        sendButton = findViewById(R.id.sendButton)

        adapter = MessageAdapter(messages)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        sendButton.setOnClickListener {
            val text = inputMessage.text.toString().trim()
            if (text.isNotEmpty()) {
                messages.add(MessageItem(text, isUser = true))
                adapter.notifyItemInserted(messages.size - 1)
                recyclerView.scrollToPosition(messages.size - 1)
                inputMessage.text.clear()
            }
        }
    }
}