package com.example.fefumarket.ui.favorites

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fefumarket.R
import com.example.fefumarket.base.BaseActivity
import com.example.fefumarket.data.repository.FavoritesManager
import com.example.fefumarket.ui.chat.ChatActivity
import com.example.fefumarket.ui.profile.ProfileActivity
import com.example.fefumarket.ui.ad.MyPostsActivity
import com.example.fefumarket.ui.home.HomeActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class FavoritesActivity : BaseActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FavoriteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        recyclerView = findViewById(R.id.favoritesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Получаем реальные избранные из FavoritesManager
        val favoriteList = FavoritesManager.getAll().toMutableList()
        adapter = FavoriteAdapter(favoriteList)
        recyclerView.adapter = adapter
    }

    private fun updateFavoritesList() {
        val favoriteList = FavoritesManager.getAll().toMutableList()
        adapter.items.clear()
        adapter.items.addAll(favoriteList)
        adapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        setActiveNavItem(R.id.nav_favorites)
        updateFavoritesList()
    }
}