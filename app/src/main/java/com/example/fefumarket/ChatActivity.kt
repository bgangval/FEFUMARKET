package com.example.fefumarket

import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class ChatActivity : AppCompatActivity() {

    private lateinit var chats: MutableList<ChatItem>
    private lateinit var adapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val recyclerView: RecyclerView = findViewById(R.id.chatRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Загружаем все чаты из менеджера
        chats = MessagesManager.getAllChats().toMutableList()
        adapter = ChatAdapter(chats)
        recyclerView.adapter = adapter

        // Проверяем, пришли ли из объявления
        val chatId = intent.getStringExtra("CHAT_ID")
        chatId?.let {
            val chat = MessagesManager.getAllChats().find { "${it.sellerName}_${it.productName}" == chatId }
            chat?.let { c ->
                if (!chats.contains(c)) {
                    chats.add(c)
                    adapter.notifyItemInserted(chats.size - 1)
                }
                // Прокручиваем к нужному чату
                val position = chats.indexOf(c)
                recyclerView.scrollToPosition(position)
            }
        }

        setupSwipe(recyclerView)
        setupBottomNavigation()
    }

    private fun setupSwipe(recyclerView: RecyclerView) {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            val deleteIcon = ContextCompat.getDrawable(this@ChatActivity, R.drawable.ic_delete)
            val muteIcon = ContextCompat.getDrawable(this@ChatActivity, R.drawable.ic_mute)
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
                val icon: android.graphics.drawable.Drawable?

                if (dX > 0) { // свайп вправо → mute/unmute
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

    private fun setupBottomNavigation() {
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> startActivity(Intent(this, HomeActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                })
                R.id.nav_favorites -> startActivity(Intent(this, FavoritesActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                })
                R.id.nav_chat -> true
                R.id.nav_profile -> true
                else -> false
            }
            true
        }
        bottomNavigation.post { bottomNavigation.selectedItemId = R.id.nav_chat }
    }
}