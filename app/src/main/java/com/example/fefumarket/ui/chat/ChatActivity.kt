package com.example.fefumarket.ui.chat

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fefumarket.R
import com.example.fefumarket.base.BaseActivity
import com.example.fefumarket.data.models.ChatItem
import com.example.fefumarket.data.repository.MessagesManager

class ChatActivity : BaseActivity() {

    private lateinit var chats: MutableList<ChatItem>
    private lateinit var adapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // 🔹 Указываем активный пункт нижней навигации
        setActiveNavItem(R.id.nav_chat)

        val recyclerView: RecyclerView = findViewById(R.id.chatRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 🔹 Загружаем все чаты через MessagesManager
        chats = MessagesManager.getAllChats().toMutableList()
        adapter = ChatAdapter(chats)
        recyclerView.adapter = adapter

        // 🔹 Если пришли из объявления, прокручиваем к нужному чату
        val chatId = intent.getStringExtra("CHAT_ID")
        chatId?.let { id ->
            val chat = MessagesManager.getAllChats().find { "${it.sellerName}_${it.productName}" == id }
            chat?.let { c ->
                if (!chats.contains(c)) {
                    chats.add(c)
                    adapter.notifyItemInserted(chats.size - 1)
                }
                recyclerView.scrollToPosition(chats.indexOf(c))
            }
        }

        setupSwipe(recyclerView)
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
    }
}