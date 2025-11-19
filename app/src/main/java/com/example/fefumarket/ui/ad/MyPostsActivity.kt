package com.example.fefumarket.ui.ad

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fefumarket.R
import com.example.fefumarket.base.BaseActivity
import com.example.fefumarket.data.repository.AdRepository
import com.example.fefumarket.data.utils.GridSpacingItemDecoration
import com.example.fefumarket.data.repository.SessionManager
import com.example.fefumarket.ui.chat.ChatActivity
import com.example.fefumarket.ui.favorites.FavoritesActivity
import com.example.fefumarket.ui.profile.ProfileActivity
import com.example.fefumarket.ui.home.HomeActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.bottomnavigation.BottomNavigationView

class MyPostsActivity : BaseActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MyPostsAdapter
    private lateinit var btnAddPost: MaterialButton

    private lateinit var currentUser: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_posts)

        recyclerView = findViewById(R.id.myPostsRecyclerView)
        btnAddPost = findViewById(R.id.btnAddPost)

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
    }

    override fun onResume() {
        super.onResume()
        setActiveNavItem(R.id.nav_add)
        adapter.updateList(
            AdRepository.getMyAds(currentUser).toMutableList()
        )
    }
}