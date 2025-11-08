package com.example.fefumarket

import android.app.Activity
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adAdapter: AdAdapter

    private lateinit var filtersRecyclerView: RecyclerView
    private lateinit var filterChipAdapter: FilterChipAdapter

    private var activeDorms = emptyList<String>()
    private var activeCategories = emptyList<String>()
    private var activeConditions = emptyList<String>()
    private var minPriceFilter: Int? = null
    private var maxPriceFilter: Int? = null

    private val searchHandler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { performSearch("") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
        }

        initFilterChips()
        initRecyclerView()
        setupBottomNavigation()
        setupSearchView()
        performSearch("")

        val btnFilter: ImageButton = findViewById(R.id.btnFilter)
        btnFilter.setOnClickListener {
            val intent = Intent(this, FiltersActivity::class.java).apply {
                putExtra("DORMS", activeDorms.toTypedArray())
                putExtra("CATEGORIES", activeCategories.toTypedArray())
                putExtra("CONDITIONS", activeConditions.toTypedArray())
                putExtra("MIN_PRICE", minPriceFilter?.toString())
                putExtra("MAX_PRICE", maxPriceFilter?.toString())
            }
            startActivityForResult(intent, 1001)
        }
    }

    // ---------- управление видимостью чипсов ----------
    private fun updateFilterChipsVisibility() {
        filtersRecyclerView.post {
            filtersRecyclerView.visibility =
                if (filterChipAdapter.itemCount > 0) View.VISIBLE else View.GONE
        }
    }

    private fun initFilterChips() {
        filtersRecyclerView = findViewById(R.id.filtersRecyclerView)

        filterChipAdapter = FilterChipAdapter(
            mutableListOf(),
            onRemove = { removed ->
                removeFilterValue(removed)
                performSearch("")
            },
            onListChanged = { updateFilterChipsVisibility() }
        )

        filtersRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        filtersRecyclerView.adapter = filterChipAdapter

        updateFilterChipsVisibility()
    }

    private fun initRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.setHasFixedSize(true)
        recyclerView.setItemViewCacheSize(20)
        adAdapter = AdAdapter(emptyList())
        recyclerView.adapter = adAdapter
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

    // ---------- поиск ----------
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

    // ---------- фильтрация ----------
    private fun performSearch(query: String) {
        var result = AdRepository.ads

        if (query.isNotEmpty()) {
            result = result.filter { ad ->
                ad.title.contains(query, ignoreCase = true) ||
                        ad.seller.contains(query, ignoreCase = true) ||
                        ad.description.contains(query, ignoreCase = true) ||
                        ad.price.contains(query, ignoreCase = true)
            }
        }

        if (activeDorms.isNotEmpty()) result = result.filter { ad -> activeDorms.contains(ad.dorm) }
        if (activeCategories.isNotEmpty()) result = result.filter { ad -> activeCategories.contains(ad.category) }
        if (activeConditions.isNotEmpty()) result = result.filter { ad -> activeConditions.contains(ad.condition) }

        // фильтрация по цене (убираем все нецифровые символы)
        result = result.filter { ad ->
            val priceValue = ad.price.replace(Regex("[^\\d]"), "").toIntOrNull() ?: 0
            val minOk = minPriceFilter?.let { priceValue >= it } ?: true
            val maxOk = maxPriceFilter?.let { priceValue <= it } ?: true
            minOk && maxOk
        }

        adAdapter.updateAds(result)
        updateFilterChipsVisibility() // обновляем видимость блока после фильтрации

        if (result.isEmpty()) {
            Toast.makeText(this, "Нет результатов", Toast.LENGTH_SHORT).show()
        }

        Log.d("HomeActivity", "Filtered: ${result.size} items")
    }

    private fun removeFilterValue(filter: String) {
        activeDorms = activeDorms.filterNot { it == filter }
        activeCategories = activeCategories.filterNot { it == filter }
        activeConditions = activeConditions.filterNot { it == filter }

        if (filter.startsWith("Цена от")) minPriceFilter = null
        if (filter.startsWith("Цена до")) maxPriceFilter = null

        val remaining = mutableListOf<String>()
        remaining.addAll(activeDorms)
        remaining.addAll(activeCategories)
        remaining.addAll(activeConditions)

        if (minPriceFilter != null) remaining.add("Цена от $minPriceFilter")
        if (maxPriceFilter != null) remaining.add("Цена до $maxPriceFilter")

        filterChipAdapter.updateFilters(remaining)
        updateFilterChipsVisibility()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1001 && resultCode == Activity.RESULT_OK && data != null) {

            activeDorms = data.getStringArrayExtra("DORMS")?.toList() ?: emptyList()
            activeCategories = data.getStringArrayExtra("CATEGORIES")?.toList() ?: emptyList()
            activeConditions = data.getStringArrayExtra("CONDITIONS")?.toList() ?: emptyList()

            minPriceFilter = data.getStringExtra("MIN_PRICE")?.toIntOrNull()
            maxPriceFilter = data.getStringExtra("MAX_PRICE")?.toIntOrNull()

            val allFilters = mutableListOf<String>()
            allFilters.addAll(activeDorms)
            allFilters.addAll(activeCategories)
            allFilters.addAll(activeConditions)

            if (minPriceFilter != null) allFilters.add("Цена от $minPriceFilter")
            if (maxPriceFilter != null) allFilters.add("Цена до $maxPriceFilter")

            filterChipAdapter.updateFilters(allFilters)
            updateFilterChipsVisibility()

            performSearch("")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        searchHandler.removeCallbacks(searchRunnable)
    }

    override fun onResume() {
        super.onResume()
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.selectedItemId = R.id.nav_home
    }
}