package com.example.fefumarket

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2

class AdDetailActivity : AppCompatActivity() {

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
        val title = intent.getStringExtra("title")
        val dorm = intent.getStringExtra("dorm")
        val price = intent.getStringExtra("price")
        val description = intent.getStringExtra("description")

        // Пример списка изображений (пока статичный)
        val photos = listOf(
            R.drawable.laptop_ic_test,
            R.drawable.iphone_ic_test,
            R.drawable.sony_ic_test
        )

        // Настройка ViewPager2
        photoPager.adapter = PhotoPagerAdapter(photos)

        // Заполняем данные
        adTitle.text = title
        adPrice.text = price
        adDorm.text = getString(R.string.dorm_format, dorm)
        adDescription.text = description

        // ====== ОБРАБОТКА КНОПОК ======

        // Кнопка назад
        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Верхнее "Избранное"
        var isFavorite = false // состояние

        btnFavoriteTop.setOnClickListener {
            isFavorite = !isFavorite
            if (isFavorite) {
                btnFavoriteTop.setImageResource(R.drawable.ic_heart_red)
                Toast.makeText(this, "Добавлено в избранное", Toast.LENGTH_SHORT).show()
            } else {
                btnFavoriteTop.setImageResource(R.drawable.ic_heart_top_bar)
                Toast.makeText(this, "Удалено из избранного", Toast.LENGTH_SHORT).show()
            }
        }

        // Верхний "Reply"
        btnChatTop.setOnClickListener {
            Toast.makeText(this, "Поделиться", Toast.LENGTH_SHORT).show()
        }

        // Нижняя кнопка "Написать"
        btnChat.setOnClickListener {
            Toast.makeText(this, "Написать продавцу", Toast.LENGTH_SHORT).show()
        }

        // Нижняя кнопка "Избранное"
        btnFavorite.setOnClickListener {
            Toast.makeText(this, "Добавлено в избранное", Toast.LENGTH_SHORT).show()
        }
    }
}