package com.example.fefumarket.ui.chat

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fefumarket.R
import com.example.fefumarket.base.BaseActivity
import com.example.fefumarket.data.models.ChatItem
import com.example.fefumarket.data.models.api.toChatItem
import com.example.fefumarket.data.repository.ChatRepository
import com.example.fefumarket.data.repository.SessionManager
import com.example.fefumarket.network.RetrofitClient
import kotlinx.coroutines.launch

class ChatActivity : BaseActivity() {

    private lateinit var chats: MutableList<ChatItem>
    private lateinit var adapter: ChatAdapter
    private lateinit var chatRepository: ChatRepository
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // 🔹 Указываем активный пункт нижней навигации
        setActiveNavItem(R.id.nav_chat)

        sessionManager = SessionManager(this)
        val api = RetrofitClient.create(this)
        chatRepository = ChatRepository(api, sessionManager)

        val recyclerView: RecyclerView = findViewById(R.id.chatRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        chats = mutableListOf()
        adapter = ChatAdapter(chats)
        recyclerView.adapter = adapter

        // 🔹 Загружаем чаты с сервера
        loadChats()

        setupSwipe(recyclerView)
    }

    // 🔹 Загрузка чатов с сервера
    private fun loadChats() {
        lifecycleScope.launch {
            try {
                val chatOuts = chatRepository.getMyChats()
                chats.clear()
                // Преобразуем ChatOut в ChatItem
                chatOuts.forEach { chatOut ->
                    val chatItem = chatOut.toChatItem()
                    chats.add(chatItem)
                }
                adapter.notifyDataSetChanged()
            } catch (e: Exception) {
                Toast.makeText(
                    this@ChatActivity,
                    "Ошибка загрузки чатов: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
                // Fallback на локальный кэш
                chats.clear()
                chats.addAll(com.example.fefumarket.data.repository.MessagesManager.getAllChats())
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun setupSwipe(recyclerView: RecyclerView) {
        // Свайпы влево/вправо — только визуальные действия, логика между страницами здесь не требуется
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            val deleteIcon: Drawable? = ContextCompat.getDrawable(this@ChatActivity, R.drawable.ic_delete)
            val muteIcon: Drawable? = ContextCompat.getDrawable(this@ChatActivity, R.drawable.ic_mute)
            val deleteColor = Color.parseColor("#f44336")
            val muteColor = Color.parseColor("#2196F3")
            val background = ColorDrawable()

            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) = false

            override fun onSwiped(vh: RecyclerView.ViewHolder, dir: Int) {
                val pos = vh.adapterPosition
                if (dir == ItemTouchHelper.LEFT) {
                    adapter.removeAt(pos)
                } else if (dir == ItemTouchHelper.RIGHT) {
                    adapter.toggleMute(pos)
                }
            }

            override fun onChildDraw(
                c: Canvas, rv: RecyclerView, vh: RecyclerView.ViewHolder,
                dX: Float, dY: Float, actionState: Int, isActive: Boolean
            ) {
                val itemView = vh.itemView
                val icon: Drawable?

                if (dX > 0) { // свайп вправо → mute
                    background.color = muteColor
                    background.setBounds(itemView.left, itemView.top, itemView.left + dX.toInt(), itemView.bottom)
                    background.draw(c)
                    icon = muteIcon
                    icon?.let {
                        val iconTop = itemView.top + (itemView.height - it.intrinsicHeight) / 2
                        val iconLeft = itemView.left + 16
                        it.setBounds(iconLeft, iconTop, iconLeft + it.intrinsicWidth, iconTop + it.intrinsicHeight)
                        it.draw(c)
                    }
                } else if (dX < 0) { // свайп влево → удалить
                    background.color = deleteColor
                    background.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                    background.draw(c)
                    icon = deleteIcon
                    icon?.let {
                        val iconTop = itemView.top + (itemView.height - it.intrinsicHeight) / 2
                        val iconRight = itemView.right - 16
                        it.setBounds(iconRight - it.intrinsicWidth, iconTop, iconRight, iconTop + it.intrinsicHeight)
                        it.draw(c)
                    }
                }

                super.onChildDraw(c, rv, vh, dX, dY, actionState, isActive)
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView)
    }

    override fun onResume() {
        super.onResume()
        // 🔹 Подсветка активного пункта навигации при возврате на экран
        setActiveNavItem(R.id.nav_chat)
        // 🔹 Обновляем список чатов при возврате на экран
        loadChats()
    }
}
