package com.example.fefumarket

import android.graphics.PorterDuff
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adAdapter: AdAdapter
    private val originalAdList = mutableListOf<Ad>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        originalAdList.addAll(
            listOf(
                Ad("Ноутбук Lenovo", "₽45,000", "Иван"),
                Ad("Айфон 12", "₽70,000", "Мария"),
                Ad("Кроссовки Nike", "₽9,000", "Олег"),
                Ad("Монитор Samsung", "₽12,500", "Анна"),
                Ad("Наушники Sony", "₽5,000", "Виктор")
            )
        )
        adAdapter = AdAdapter(originalAdList.toList())
        recyclerView.adapter = adAdapter

        // BottomNavigation
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_cart -> true
                R.id.nav_add -> true
                R.id.nav_chat -> true
                R.id.nav_profile -> true
                else -> false
            }
        }

        // Настройка SearchView
        val searchView = findViewById<SearchView>(R.id.searchView)
        val color = ContextCompat.getColor(this, R.color.search_icon_color)

        // Сразу раскрытый и готовый к вводу
        searchView.isIconified = false
        searchView.isFocusable = true
        searchView.isFocusableInTouchMode = true
        searchView.requestFocus()

        searchView.post {
            // Лупа
            val searchIcon = searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon)
            searchIcon.setColorFilter(color, PorterDuff.Mode.SRC_IN)

            // Текст подсказки и вводимый текст
            val hintText = searchView.findViewById<TextView>(androidx.appcompat.R.id.search_src_text)
            hintText.setTextColor(color)
            hintText.setHintTextColor(color)
            hintText.textSize = 16f

            // Крестик очистки текста
            val closeButton = searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)
            closeButton.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        }

        // Обработка фильтра
        val btnFilter: ImageButton = findViewById(R.id.btnFilter)
        btnFilter.setOnClickListener {
            // TODO: показать диалог фильтрации
        }
    }
}