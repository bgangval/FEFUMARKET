package com.example.fefumarket

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2

class AdDetailActivity : AppCompatActivity() {

    private lateinit var ad: Ad

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ad_detail)

        // Основные элементы
        val photoPager: ViewPager2 = findViewById(R.id.photoPager)
        val adTitle: TextView = findViewById(R.id.titleText)
        val adPrice: TextView = findViewById(R.id.priceText)
        val adDorm: TextView = findViewById(R.id.dormText)
        val adDescription: TextView = findViewById(R.id.descriptionText)

        // Нижние кнопки
        val btnChat: Button = findViewById(R.id.contactButton)
        val btnFavorite: Button = findViewById(R.id.favoriteButton)

        // Верхние кнопки
        val btnBack: ImageButton = findViewById(R.id.btnBack)
        val btnFavoriteTop: ImageButton = findViewById(R.id.btnFavoriteTop)
        val btnChatTop: ImageButton = findViewById(R.id.btnChatTop)

        // Получаем данные из intent
        val title = intent.getStringExtra("title") ?: ""
        val seller = intent.getStringExtra("seller") ?: ""
        val price = intent.getStringExtra("price") ?: ""
        val dorm = intent.getStringExtra("dorm") ?: ""
        val description = intent.getStringExtra("description") ?: ""
        val imageResId = intent.getIntExtra("imageResId", 0)

        // Создаём объект Ad с полем dorm
        ad = Ad(
            title = title,
            price = price,
            seller = seller,
            description = description,
            imageResId = imageResId,
            dorm = dorm
        )

        // Пример списка изображений (пока статичный)
        val photos = listOf(
            R.drawable.laptop_ic_test,
            R.drawable.iphone_ic_test,
            R.drawable.sony_ic_test
        )

        // Настройка ViewPager2
        photoPager.adapter = PhotoPagerAdapter(photos)

        // Заполняем данные
        adTitle.text = ad.title
        adPrice.text = ad.price
        adDorm.text = ad.dorm
        adDescription.text = ad.description

        // ====== ОБРАБОТКА КНОПОК ======

        btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        // Верхнее "Избранное"
        var isFavorite = FavoritesManager.isFavorite(ad)
        updateFavoriteIcon(btnFavoriteTop, isFavorite)

        btnFavoriteTop.setOnClickListener {
            isFavorite = !isFavorite
            if (isFavorite) {
                FavoritesManager.add(ad)
                Toast.makeText(this, "Добавлено в избранное", Toast.LENGTH_SHORT).show()
            } else {
                FavoritesManager.remove(ad)
                Toast.makeText(this, "Удалено из избранного", Toast.LENGTH_SHORT).show()
            }
            updateFavoriteIcon(btnFavoriteTop, isFavorite)
        }

        // Нижняя кнопка "Избранное"
        btnFavorite.setOnClickListener {
            isFavorite = !isFavorite
            if (isFavorite) {
                FavoritesManager.add(ad)
                Toast.makeText(this, "Добавлено в избранное", Toast.LENGTH_SHORT).show()
            } else {
                FavoritesManager.remove(ad)
                Toast.makeText(this, "Удалено из избранного", Toast.LENGTH_SHORT).show()
            }
            updateFavoriteIcon(btnFavoriteTop, isFavorite)
        }

        // Кнопки "Написать сообщение"
        btnChat.setOnClickListener { openChat() }
        btnChatTop.setOnClickListener { openChat() }
    }

    private fun updateFavoriteIcon(button: ImageButton, isFavorite: Boolean) {
        if (isFavorite) {
            button.setImageResource(R.drawable.ic_heart_red)
        } else {
            button.setImageResource(R.drawable.ic_heart_top_bar)
        }
    }

    private fun openChat() {
        val chatId = "${ad.seller}_${ad.title}"
        MessagesManager.getOrCreateChat(chatId, ad.seller, ad.title, ad.imageResId)

        val intent = Intent(this, MessageActivity::class.java)
        intent.putExtra("CHAT_ID", chatId)
        intent.putExtra("SELLER_NAME", ad.seller)
        intent.putExtra("PRODUCT_NAME", ad.title)
        intent.putExtra("AVATAR_ID", ad.imageResId)
        startActivity(intent)
    }
}