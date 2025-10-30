package com.example.fefumarket

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
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
    private val searchHandler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { performSearch("") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
        }

        initRecyclerView()
        populateAds()
        setupBottomNavigation()
        setupSearchView()
    }

    private fun initRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.setHasFixedSize(true)
        recyclerView.setItemViewCacheSize(20)
    }

    private fun populateAds() {
        originalAdList.addAll(
            listOf(
                Ad("Ноутбук Lenovo", "₽45,000", "Иван", "Новый, i7, 16GB RAM", R.drawable.laptop_ic_test),
                Ad("Айфон 12", "₽70,000", "Мария", "В отличном состоянии, 128GB", R.drawable.iphone_ic_test),
                Ad("Кроссовки Nike", "₽9,000", "Олег", "Размер 42, оригинал", R.drawable.nike_ic_test),
                Ad("Монитор Samsung", "₽12,500", "Анна", "27 дюймов, 4K", R.drawable.monitor_ic_test),
                Ad("Наушники Sony", "₽5,000", "Виктор", "Беспроводные, шумоподавление", R.drawable.sony_ic_test),
                Ad("Велосипед Giant", "₽25,000", "Петр", "Горный, 21 скорость", R.drawable.bike_ic_test),
                Ad("Книга по Android", "₽1,200", "Елена", "Kotlin для начинающих", R.drawable.book_ic_test),
                Ad("Часы Casio", "₽3,500", "Дмитрий", "Кварцевые, водонепроницаемые", R.drawable.watch_ic_test)
            )
        )

        adAdapter = AdAdapter(originalAdList)
        recyclerView.adapter = adAdapter

        // Padding под BottomNavigationView
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.viewTreeObserver.addOnGlobalLayoutListener {
            recyclerView.setPadding(0, 0, 0, bottomNavigation.height)
        }
    }

    private fun setupBottomNavigation() {
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_favorites -> {
                    val intent = Intent(this, FavoritesActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    true
                }
                R.id.nav_add -> { showToast("Публикация"); true }
                R.id.nav_chat -> {
                    val intent = Intent(this, ChatActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    true
                }
                R.id.nav_profile -> { showToast("Профиль"); true }
                else -> false
            }
        }
        bottomNavigation.post { bottomNavigation.selectedItemId = R.id.nav_home }
    }

    private fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    private fun setupSearchView() {
        val searchView = findViewById<SearchView>(R.id.searchView)
        searchView.isIconified = false
        searchView.requestFocusFromTouch()
        searchView.clearFocus()

        val color = ContextCompat.getColor(this, R.color.search_icon_color)

        searchView.post {
            val searchIcon = searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon)
            searchIcon?.setColorFilter(color, PorterDuff.Mode.SRC_IN)

            val searchEditText = searchView.findViewById<TextView>(androidx.appcompat.R.id.search_src_text)
            searchEditText?.apply {
                setTextColor(color)
                setHintTextColor(color)
                textSize = 16f
                isFocusable = true
                isFocusableInTouchMode = true
                isCursorVisible = true
            }

            val closeButton = searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)
            closeButton?.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                performSearch(query.orEmpty())
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchHandler.removeCallbacks(searchRunnable)
                searchHandler.postDelayed({ performSearch(newText.orEmpty()) }, 300)
                return true
            }
        })
    }

    private fun performSearch(query: String) {
        val filteredList = if (query.isEmpty()) {
            originalAdList.toList()
        } else {
            originalAdList.filter { ad ->
                ad.title.contains(query, ignoreCase = true) ||
                        ad.seller.contains(query, ignoreCase = true) ||
                        ad.price.contains(query, ignoreCase = true) ||
                        ad.description.contains(query, ignoreCase = true)
            }
        }
        adAdapter.updateAds(filteredList)
        if (filteredList.isEmpty()) {
            Toast.makeText(this, "Нет результатов: '$query'", Toast.LENGTH_SHORT).show()
        }
        Log.d("HomeActivity", "Filtered: ${filteredList.size} items")
    }

    override fun onDestroy() {
        searchHandler.removeCallbacks(searchRunnable)
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.selectedItemId = R.id.nav_home
    }
}

data class Ad(
    val title: String,
    val price: String,
    val seller: String,
    val description: String = "",
    val imageResId: Int
)