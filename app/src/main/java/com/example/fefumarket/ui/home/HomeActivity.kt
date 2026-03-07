package com.example.fefumarket.ui.home

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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fefumarket.R
import com.example.fefumarket.base.BaseActivity
import com.example.fefumarket.data.models.Ad
import com.example.fefumarket.data.repository.AdRepository
import com.example.fefumarket.data.repository.SessionManager
import com.example.fefumarket.network.RetrofitClient
import com.example.fefumarket.ui.auth.LoginActivity
import kotlinx.coroutines.launch

class HomeActivity : BaseActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adAdapter: AdAdapter
    private lateinit var filtersRecyclerView: RecyclerView
    private lateinit var filterChipAdapter: FilterChipAdapter

    private lateinit var session: SessionManager
    private lateinit var adRepository: AdRepository

    private var currentAds = mutableListOf<Ad>()

    private var activeDorms = emptyList<String>()
    private var activeCategories = emptyList<String>()
    private var activeConditions = emptyList<String>()
    private var minPriceFilter: Int? = null
    private var maxPriceFilter: Int? = null

    private val searchHandler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { performSearch("") }

    // 🔹 Лаунчер для FiltersActivity, чтобы получить выбранные фильтры
    private val filtersLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val data = result.data!!

                // 🔹 Обновление активных фильтров после выбора в FiltersActivity
                activeDorms = data.getStringArrayExtra("DORMS")?.toList() ?: emptyList()
                activeCategories = data.getStringArrayExtra("CATEGORIES")?.toList() ?: emptyList()
                activeConditions = data.getStringArrayExtra("CONDITIONS")?.toList() ?: emptyList()
                minPriceFilter = data.getStringExtra("MIN_PRICE")?.toIntOrNull()
                maxPriceFilter = data.getStringExtra("MAX_PRICE")?.toIntOrNull()

                // 🔹 Обновление списка чипсов (RecyclerView с фильтрами)
                val allFilters = mutableListOf<String>()
                allFilters.addAll(activeDorms)
                allFilters.addAll(activeCategories)
                allFilters.addAll(activeConditions)
                minPriceFilter?.let { allFilters.add("Цена от $it") }
                maxPriceFilter?.let { allFilters.add("Цена до $it") }

                filterChipAdapter.updateFilters(allFilters)
                updateFilterChipsVisibility()

                // 🔹 Перефильтровка объявлений после изменения фильтров
                performSearch("")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // 🔹 Проверка авторизации пользователя
        session = SessionManager(this)
        if (session.getLogin() == null || session.getToken().isNullOrBlank()) {
            startActivity(Intent(this, LoginActivity::class.java)) // Переход на экран логина
            finish()
            return
        }

        // 🔹 Инициализация репозитория объявлений с текущей сессией
        val api = RetrofitClient.create(this)
        adRepository = AdRepository(api, session)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        // 🔹 Инициализация RecyclerView с фильтрами (чипсы)
        initFilterChips()

        // 🔹 Инициализация основной сетки объявлений
        initRecyclerView()

        // 🔹 Настройка нижней навигации
        setupBottomNavigation()

        // 🔹 Настройка поиска по объявлениям
        setupSearchView()

        // 🔹 Кнопка открытия FiltersActivity
        val btnFilter: ImageButton = findViewById(R.id.btnFilter)
        btnFilter.setOnClickListener { openFilters() }

        // 🔹 Загрузка объявлений с сервера
        loadAdsFromServer()
    }

    // ===== Инициализация фильтров =====
    private fun initFilterChips() {
        filtersRecyclerView = findViewById(R.id.filtersRecyclerView)
        filterChipAdapter = FilterChipAdapter(
            mutableListOf(),
            onRemove = { removed ->
                removeFilterValue(removed) // удаление фильтра и обновление списка
                performSearch("")
            },
            onListChanged = { updateFilterChipsVisibility() }
        )
        filtersRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        filtersRecyclerView.adapter = filterChipAdapter
        updateFilterChipsVisibility()
    }

    private fun updateFilterChipsVisibility() {
        filtersRecyclerView.post {
            filtersRecyclerView.visibility =
                if (filterChipAdapter.itemCount > 0) View.VISIBLE else View.GONE
        }
    }

    // ===== Инициализация RecyclerView с объявлениями =====
    private fun initRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.setHasFixedSize(true)
        recyclerView.setItemViewCacheSize(20)
        adAdapter = AdAdapter(currentAds)
        recyclerView.adapter = adAdapter
    }

    // ===== Настройка поиска =====
    private fun setupSearchView() {
        val searchView = findViewById<SearchView>(R.id.searchView)
        searchView.isIconified = false
        searchView.requestFocusFromTouch()
        searchView.clearFocus()

        val color = ContextCompat.getColor(this, R.color.search_icon_color)

        searchView.post {
            searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_mag_icon)
                ?.setColorFilter(color, PorterDuff.Mode.SRC_IN)
            searchView.findViewById<TextView>(androidx.appcompat.R.id.search_src_text)?.apply {
                setTextColor(color)
                setHintTextColor(color)
                textSize = 16f
                isFocusable = true
                isFocusableInTouchMode = true
                isCursorVisible = true
            }
            searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)
                ?.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        }

        // 🔹 Обработчики текста поиска
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                performSearch(query.orEmpty()) // поиск при нажатии Enter
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchHandler.removeCallbacks(searchRunnable)
                searchHandler.postDelayed({ performSearch(newText.orEmpty()) }, 300) // поиск с задержкой
                return true
            }
        })
    }

    // ===== Загрузка объявлений с сервера =====
    private fun loadAdsFromServer() {
        lifecycleScope.launch {
            try {
                val ads = adRepository.getAds()
                currentAds.clear()
                currentAds.addAll(ads)
                val searchView = findViewById<SearchView>(R.id.searchView)
                performSearch(searchView.query?.toString().orEmpty())
                Log.d("HOME_ADS", "Ads loaded: ${ads.size}")
            } catch (e: Exception) {
                Log.d("HOME_ADS", "Ошибка: ${e.message}")
                Toast.makeText(this@HomeActivity, "Ошибка загрузки объявлений", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ===== Фильтрация и поиск =====
    private fun performSearch(query: String) {
        var result = currentAds.toMutableList()

        // 🔹 Фильтрация по тексту поиска
        if (query.isNotEmpty()) {
            result = result.filter { ad ->
                ad.title.contains(query, ignoreCase = true) ||
                        ad.seller.contains(query, ignoreCase = true) ||
                        ad.description.contains(query, ignoreCase = true) ||
                        ad.price.contains(query, ignoreCase = true)
            }.toMutableList()
        }

        // 🔹 Фильтрация по активным фильтрам
        if (activeDorms.isNotEmpty())
            result = result.filter { ad -> activeDorms.contains(ad.dorm) }.toMutableList()
        if (activeCategories.isNotEmpty())
            result = result.filter { ad -> activeCategories.contains(ad.category) }.toMutableList()
        if (activeConditions.isNotEmpty())
            result = result.filter { ad -> activeConditions.contains(ad.condition) }.toMutableList()

        // 🔹 Фильтрация по цене
        result = result.filter { ad ->
            val priceValue = ad.price.replace(Regex("[^\\d]"), "").toIntOrNull() ?: 0
            val minOk = minPriceFilter?.let { priceValue >= it } ?: true
            val maxOk = maxPriceFilter?.let { priceValue <= it } ?: true
            minOk && maxOk
        }.toMutableList()

        adAdapter.updateAds(result)
        updateFilterChipsVisibility()
    }

    // ===== Удаление отдельного фильтра =====
    private fun removeFilterValue(filter: String) {
        activeDorms = activeDorms.filterNot { it == filter }
        activeCategories = activeCategories.filterNot { it == filter }
        activeConditions = activeConditions.filterNot { it == filter }

        if (filter.startsWith("Цена от")) minPriceFilter = null
        if (filter.startsWith("Цена до")) maxPriceFilter = null

        // 🔹 Обновление чипсов и повторная фильтрация
        val remaining = mutableListOf<String>()
        remaining.addAll(activeDorms)
        remaining.addAll(activeCategories)
        remaining.addAll(activeConditions)
        minPriceFilter?.let { remaining.add("Цена от $it") }
        maxPriceFilter?.let { remaining.add("Цена до $it") }

        filterChipAdapter.updateFilters(remaining)
        updateFilterChipsVisibility()
    }

    // ===== Открытие FiltersActivity =====
    private fun openFilters() {
        val intent = Intent(this, FiltersActivity::class.java).apply {
            putExtra("DORMS", activeDorms.toTypedArray())
            putExtra("CATEGORIES", activeCategories.toTypedArray())
            putExtra("CONDITIONS", activeConditions.toTypedArray())
            putExtra("MIN_PRICE", minPriceFilter?.toString())
            putExtra("MAX_PRICE", maxPriceFilter?.toString())
        }
        filtersLauncher.launch(intent) // старт FiltersActivity
    }

    override fun onResume() {
        super.onResume()
        setActiveNavItem(R.id.nav_home)
        // После добавления/редактирования объявлений обновляем список с сервера
        loadAdsFromServer()
    }

    override fun onDestroy() {
        super.onDestroy()
        searchHandler.removeCallbacks(searchRunnable)
    }
}
