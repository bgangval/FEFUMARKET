package com.example.fefumarket

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.bottomnavigation.BottomNavigationView

class MyPostsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MyPostsAdapter
    private lateinit var btnAddPost: MaterialButton
    private lateinit var bottomNavigation: BottomNavigationView

    private lateinit var currentUser: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_posts)

        recyclerView = findViewById(R.id.myPostsRecyclerView)
        btnAddPost = findViewById(R.id.btnAddPost)
        bottomNavigation = findViewById(R.id.bottomNavigation)

        currentUser = SessionManager(this).getUserName() ?: ""

        adapter = MyPostsAdapter(
            AdRepository.getMyAds(currentUser).toMutableList()
        ) { ad ->
            val intent = Intent(this, EditPostActivity::class.java)
            intent.putExtra("AD_TITLE", ad.title)
            startActivity(intent)
        }

        // Сетка 2 в ряд
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        // ОТСТУПЫ БЕЗ DIMEN — 16dp
        val spacingPx = (16 * resources.displayMetrics.density).toInt()
        recyclerView.addItemDecoration(
            GridSpacingItemDecoration(2, spacingPx, true)
        )

        recyclerView.adapter = adapter

        btnAddPost.setOnClickListener {
            startActivity(Intent(this, AddPostActivity::class.java))
        }

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, HomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    true
                }
                R.id.nav_favorites -> {
                    val intent = Intent(this, FavoritesActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    true
                }
                R.id.nav_add -> true
                R.id.nav_chat -> {
                    val intent = Intent(this, ChatActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    true
                }
                R.id.nav_profile -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
        bottomNavigation.post { bottomNavigation.selectedItemId = R.id.nav_add }
    }

    override fun onResume() {
        super.onResume()
        adapter.updateList(
            AdRepository.getMyAds(currentUser).toMutableList()
        )
    }
}