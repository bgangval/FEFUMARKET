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
        adapter = FavoriteAdapter(mutableListOf()) { ad ->
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    ad.id.toIntOrNull()?.let { productId ->
                        adRepository.removeFavorite(productId)
                    }
                } catch (_: Exception) {
                    // Если сервер недоступен, все равно удаляем локально как fallback
                }

                withContext(Dispatchers.Main) {
                    FavoritesManager.remove(ad)
                    adapter.removeItemById(ad.id)
                }
            }
        }
        recyclerView.adapter = adapter
    }

    // 🔹 Обновление списка избранного с сервера
    private fun updateFavoritesList() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val favoriteAds = adRepository.getFavorites() // получаем избранное с сервера

                // 🔹 Обновляем UI в главном потоке
                withContext(Dispatchers.Main) {
                    adapter.items.clear()
                    adapter.items.addAll(favoriteAds)
                    adapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    // Если ошибка, показываем пустой список или используем локальный кэш как fallback
                    val localFavorites = FavoritesManager.getAll()
                    adapter.items.clear()
                    adapter.items.addAll(localFavorites)
                    adapter.notifyDataSetChanged()
                }
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
