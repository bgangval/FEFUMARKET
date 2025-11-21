package com.example.fefumarket.ui.favorites

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fefumarket.R
import com.example.fefumarket.base.BaseActivity
import com.example.fefumarket.data.repository.FavoritesManager
import com.example.fefumarket.data.models.Ad
import com.example.fefumarket.data.repository.AdRepository

class FavoritesActivity : BaseActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FavoriteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        recyclerView = findViewById(R.id.favoritesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val favoriteList = FavoritesManager.getAll().toMutableList()
        adapter = FavoriteAdapter(favoriteList)
        recyclerView.adapter = adapter
    }

    private fun updateFavoritesList() {
        val favoriteAds = FavoritesManager.getAll().toMutableList()
        val updatedList = mutableListOf<Ad>()

        for (ad in favoriteAds) {
            val realAd = AdRepository.ads.find { it.id == ad.id }

            if (realAd == null || realAd.isSold) {
                FavoritesManager.remove(ad)
            } else {
                updatedList.add(realAd)
            }
        }

        adapter.items.clear()
        adapter.items.addAll(updatedList)
        adapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        setActiveNavItem(R.id.nav_favorites)
        updateFavoritesList()
    }
}