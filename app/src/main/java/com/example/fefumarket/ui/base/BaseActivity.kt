package com.example.fefumarket.base

import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.fefumarket.R
import com.example.fefumarket.ui.ad.MyPostsActivity
import com.example.fefumarket.ui.chat.ChatActivity
import com.example.fefumarket.ui.favorites.FavoritesActivity
import com.example.fefumarket.ui.home.HomeActivity
import com.example.fefumarket.ui.profile.ProfileActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

open class BaseActivity : AppCompatActivity() {

    // Текущий пункт навигации
    protected var currentNavId: Int = R.id.nav_home

    // Порядок пунктов для определения направления анимации
    private val navOrder = listOf(
        R.id.nav_home,
        R.id.nav_favorites,
        R.id.nav_add,
        R.id.nav_chat,
        R.id.nav_profile
    )

    override fun setContentView(layoutResID: Int) {
        super.setContentView(R.layout.activity_base)
        val container = findViewById<FrameLayout>(R.id.container)
        layoutInflater.inflate(layoutResID, container, true)
        setupBottomNavigation()
    }

    protected fun setupBottomNavigation() {
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.selectedItemId = currentNavId

        bottomNavigation.setOnItemSelectedListener { item ->
            if (item.itemId == currentNavId) return@setOnItemSelectedListener true

            val forward = navOrder.indexOf(item.itemId) > navOrder.indexOf(currentNavId)

            val targetActivity = when (item.itemId) {
                R.id.nav_home -> HomeActivity::class.java
                R.id.nav_favorites -> FavoritesActivity::class.java
                R.id.nav_add -> MyPostsActivity::class.java
                R.id.nav_chat -> ChatActivity::class.java
                R.id.nav_profile -> ProfileActivity::class.java
                else -> null
            }

            targetActivity?.let { cls ->
                val intent = Intent(this, cls)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)

                // Анимация перехода
                if (forward) {
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                } else {
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                }
            }
            true
        }
    }

    /** Дочерние Activity вызывают в onResume() для подсветки */
    protected fun setActiveNavItem(activeId: Int) {
        currentNavId = activeId
        findViewById<BottomNavigationView>(R.id.bottomNavigation)?.selectedItemId = activeId
    }
}