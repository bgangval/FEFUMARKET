package com.example.fefumarket.ui.favorites

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fefumarket.R
import com.example.fefumarket.base.BaseActivity
import com.example.fefumarket.data.repository.FavoritesManager
import com.example.fefumarket.data.models.Ad
import com.example.fefumarket.data.repository.AdRepository
import com.example.fefumarket.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoritesActivity : BaseActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FavoriteAdapter
    private lateinit var adRepository: AdRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        recyclerView = findViewById(R.id.favoritesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 🔹 Инициализация репозитория
        val api = RetrofitClient.create(this)
        adRepository = AdRepository(api)

        adapter = FavoriteAdapter(mutableListOf())
        recyclerView.adapter = adapter
    }

    private fun updateFavoritesList() {
        CoroutineScope(Dispatchers.IO).launch {
            val allAds = adRepository.getAds() // все объявления с репозитория
            val favoriteAds = FavoritesManager.getAll()

            val updatedList = favoriteAds.mapNotNull { favAd ->
                allAds.find { it.id == favAd.id } // берем только реальные объявления
            }.toMutableList()

            // Если какие-то избранные больше не существуют, удаляем их из FavoritesManager
            favoriteAds.forEach { favAd ->
                if (updatedList.none { it.id == favAd.id }) {
                    FavoritesManager.remove(favAd)
                }
            }

            withContext(Dispatchers.Main) {
                adapter.items.clear()
                adapter.items.addAll(updatedList)
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setActiveNavItem(R.id.nav_favorites)
        updateFavoritesList()
    }
}