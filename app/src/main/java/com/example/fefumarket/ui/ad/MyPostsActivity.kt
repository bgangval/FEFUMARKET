package com.example.fefumarket.ui.ad

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fefumarket.R
import com.example.fefumarket.base.BaseActivity
import com.example.fefumarket.data.repository.AdRepository
import com.example.fefumarket.data.repository.SessionManager
import com.example.fefumarket.data.utils.GridSpacingItemDecoration
import com.example.fefumarket.network.RetrofitClient
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyPostsActivity : BaseActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MyPostsAdapter
    private lateinit var btnAddPost: MaterialButton

    private lateinit var currentUser: String
    private lateinit var adRepository: AdRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_posts)

        recyclerView = findViewById(R.id.myPostsRecyclerView)
        btnAddPost = findViewById(R.id.btnAddPost)

        currentUser = SessionManager(this).getUserName() ?: ""

        // 🔹 Инициализация репозитория
        val api = RetrofitClient.create(this)
        adRepository = AdRepository(api)

        btnAddPost.setOnClickListener {
            startActivity(Intent(this, AddPostActivity::class.java))
        }

        // Сетка 2 в ряд
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        val spacingPx = (16 * resources.displayMetrics.density).toInt()
        recyclerView.addItemDecoration(GridSpacingItemDecoration(2, spacingPx, true))

        // Загружаем объявления текущего пользователя
        loadMyAds()
    }

    override fun onResume() {
        super.onResume()
        setActiveNavItem(R.id.nav_add)
        loadMyAds() // обновляем список после возвращения
    }

    private fun loadMyAds() {
        CoroutineScope(Dispatchers.IO).launch {
            val allAds = adRepository.getAds()
            val myAds = allAds.filter { it.seller == currentUser }.toMutableList()

            withContext(Dispatchers.Main) {
                if (::adapter.isInitialized) {
                    adapter.updateList(myAds)
                } else {
                    adapter = MyPostsAdapter(myAds) { ad ->
                        val intent = Intent(this@MyPostsActivity, EditPostActivity::class.java)
                        intent.putExtra("AD_TITLE", ad.title)
                        startActivity(intent)
                    }
                    recyclerView.adapter = adapter
                }
            }
        }
    }
}