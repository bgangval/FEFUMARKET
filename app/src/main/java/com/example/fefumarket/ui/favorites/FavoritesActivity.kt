package com.example.fefumarket.ui.favorites

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fefumarket.R
import com.example.fefumarket.base.BaseActivity
import com.example.fefumarket.data.repository.FavoritesManager
import com.example.fefumarket.data.repository.AdRepository
import com.example.fefumarket.data.repository.SessionManager
import com.example.fefumarket.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoritesActivity : BaseActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FavoriteAdapter
    private lateinit var adRepository: AdRepository
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        recyclerView = findViewById(R.id.favoritesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 🔹 Инициализация session и репозитория для взаимодействия с сервером
        sessionManager = SessionManager(this)
        val api = RetrofitClient.create(this)
        adRepository = AdRepository(api, sessionManager)

        // 🔹 Настраиваем адаптер для RecyclerView с пустым списком, будет обновляться при onResume
        adapter = FavoriteAdapter(mutableListOf())
        recyclerView.adapter = adapter
    }

    // 🔹 Обновление списка избранного с проверкой существования объявлений на сервере
    private fun updateFavoritesList() {
        CoroutineScope(Dispatchers.IO).launch {
            val allAds = adRepository.getAds() // получаем все объявления с репозитория
            val favoriteAds = FavoritesManager.getAll() // получаем список избранного локально

            val updatedList = favoriteAds.mapNotNull { favAd ->
                allAds.find { it.id == favAd.id } // оставляем только реально существующие объявления
            }.toMutableList()

            // 🔹 Удаляем из FavoritesManager те объявления, которых больше нет на сервере
            favoriteAds.forEach { favAd ->
                if (updatedList.none { it.id == favAd.id }) {
                    FavoritesManager.remove(favAd)
                }
            }

            // 🔹 Обновляем UI в главном потоке
            withContext(Dispatchers.Main) {
                adapter.items.clear()
                adapter.items.addAll(updatedList)
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // 🔹 Подсвечиваем пункт нижней навигации
        setActiveNavItem(R.id.nav_favorites)
        // 🔹 Обновляем список избранного каждый раз при возвращении на экран
        updateFavoritesList()
    }
}