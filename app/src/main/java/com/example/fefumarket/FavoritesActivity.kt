// FavoritesActivity.kt
package com.example.fefumarket

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class FavoritesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FavoriteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        recyclerView = findViewById(R.id.favoritesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Получаем реальные избранные из FavoritesManager
        val favoriteList = FavoritesManager.getAll().toMutableList()

        // Подключаем адаптер
        adapter = FavoriteAdapter(favoriteList)
        recyclerView.adapter = adapter

        setupBottomNavigation()
    }

    override fun onResume() {
        super.onResume()
        updateFavoritesList()
    }

    private fun updateFavoritesList() {
        val favoriteList = FavoritesManager.getAll().toMutableList()
        adapter.items.clear()
        adapter.items.addAll(favoriteList)
        adapter.notifyDataSetChanged()
    }

    private fun setupBottomNavigation() {
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigationFavorites)

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, HomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    true
                }
                R.id.nav_favorites -> true // Уже в Избранном
                R.id.nav_add -> true
                R.id.nav_chat -> true
                R.id.nav_profile -> true
                else -> false
            }
        }

        // Подсветка активного пункта
        bottomNavigation.selectedItemId = R.id.nav_favorites
    }
}