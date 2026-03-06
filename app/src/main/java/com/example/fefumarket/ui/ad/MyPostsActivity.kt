package com.example.fefumarket.ui.ad

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fefumarket.R
import com.example.fefumarket.base.BaseActivity
import com.example.fefumarket.ui.ad.AddPostActivity
import com.example.fefumarket.data.repository.AdRepository
import com.example.fefumarket.data.repository.SessionManager
import com.example.fefumarket.data.utils.GridSpacingItemDecoration
import com.example.fefumarket.network.RetrofitClient
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.widget.Toast

// Экран "Мои объявления"
// Отображает все объявления текущего пользователя в виде сетки
// Позволяет перейти к добавлению нового объявления или редактированию существующего
class MyPostsActivity : BaseActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MyPostsAdapter
    private lateinit var btnAddPost: MaterialButton

    private lateinit var adRepository: AdRepository
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_posts)

        recyclerView = findViewById(R.id.myPostsRecyclerView)
        btnAddPost = findViewById(R.id.btnAddPost)

        // 🔹 Получаем данные о текущем пользователе
        sessionManager = SessionManager(this)

        // 🔹 Инициализация репозитория с session
        val api = RetrofitClient.create(this)
        adRepository = AdRepository(api, sessionManager)

        // Кнопка добавления нового объявления
        btnAddPost.setOnClickListener {
            startActivity(Intent(this, AddPostActivity::class.java))
        }

        // Настройка RecyclerView как сетки с 2 колонками
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        val spacingPx = (16 * resources.displayMetrics.density).toInt()
        recyclerView.addItemDecoration(GridSpacingItemDecoration(2, spacingPx, true))

        // Загружаем объявления текущего пользователя
        loadMyAds()
    }

    override fun onResume() {
        super.onResume()
        setActiveNavItem(R.id.nav_add) // 🔹 Активный элемент навигации
        loadMyAds() // обновляем список после возвращения
    }

    // 🔹 Загрузка объявлений текущего пользователя через AdRepository
    private fun loadMyAds() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val myAds = adRepository.getMyAds().toMutableList()

                withContext(Dispatchers.Main) {
                    if (::adapter.isInitialized) {
                        adapter.updateList(myAds)
                    } else {
                        // Инициализация адаптера с обработчиком клика для редактирования
                        adapter = MyPostsAdapter(myAds) { ad ->
                            val intent = Intent(this@MyPostsActivity, EditPostActivity::class.java)
                            intent.putExtra("AD_ID", ad.id)
                            intent.putExtra("AD_TITLE", ad.title)
                            startActivity(intent)
                        }
                        recyclerView.adapter = adapter
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@MyPostsActivity,
                        "Ошибка загрузки моих объявлений: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}
