package com.example.fefumarket

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2

class AdDetailActivity : AppCompatActivity() {

    private lateinit var ad: Ad

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ad_detail)

        intent?.data?.let { dataUri ->
            val adName = dataUri.lastPathSegment?.replace("_", " ")
            val foundAd = adName?.let { AdRepository.findByTitle(it) }
            if (foundAd != null) {
                ad = foundAd
            } else {
                Toast.makeText(this, "Объявление не найдено", Toast.LENGTH_SHORT).show()
                finish()
                return
            }
        } ?: run {
            val title = intent.getStringExtra("title") ?: ""
            val seller = intent.getStringExtra("seller") ?: ""
            val price = intent.getStringExtra("price") ?: ""
            val dorm = intent.getStringExtra("dorm") ?: ""
            val description = intent.getStringExtra("description") ?: ""
            val imageResId = intent.getIntExtra("imageResId", 0)
            val category = intent.getStringExtra("category") ?: "Разное"
            val condition = intent.getStringExtra("condition") ?: "Любое"
            ad = Ad(
                title = title,
                price = price,
                dorm = dorm,
                seller = seller,
                description = description,
                category = category,
                condition = condition,
                imageResId = imageResId
            )
        }

        val photoPager: ViewPager2 = findViewById(R.id.photoPager)
        val adTitle: TextView = findViewById(R.id.titleText)
        val adPrice: TextView = findViewById(R.id.priceText)
        val adDorm: TextView = findViewById(R.id.dormText)
        val adDescription: TextView = findViewById(R.id.descriptionText)
        val btnChat: Button = findViewById(R.id.contactButton)
        val btnFavorite: Button = findViewById(R.id.favoriteButton)
        val btnBack: ImageButton = findViewById(R.id.btnBack)
        val btnFavoriteTop: ImageButton = findViewById(R.id.btnFavoriteTop)
        val btnShareTop: ImageButton = findViewById(R.id.btnChatTop)

        val photos = listOf(R.drawable.laptop_ic_test, R.drawable.iphone_ic_test, R.drawable.sony_ic_test)
        photoPager.adapter = PhotoPagerAdapter(photos)

        adTitle.text = ad.title
        adPrice.text = ad.price
        adDorm.text = ad.dorm
        adDescription.text = ad.description

        btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        var isFavorite = FavoritesManager.isFavorite(ad)
        updateFavoriteIcon(btnFavoriteTop, isFavorite)

        btnFavoriteTop.setOnClickListener {
            isFavorite = !isFavorite
            if (isFavorite) FavoritesManager.add(ad) else FavoritesManager.remove(ad)
            updateFavoriteIcon(btnFavoriteTop, isFavorite)
        }

        btnFavorite.setOnClickListener {
            isFavorite = !isFavorite
            if (isFavorite) FavoritesManager.add(ad) else FavoritesManager.remove(ad)
            updateFavoriteIcon(btnFavoriteTop, isFavorite)
        }

        btnChat.setOnClickListener { openChat() }
        btnShareTop.setOnClickListener { shareAd() }
    }

    private fun updateFavoriteIcon(button: ImageButton, isFavorite: Boolean) {
        button.setImageResource(if (isFavorite) R.drawable.ic_heart_red else R.drawable.ic_heart_top_bar)
    }

    private fun openChat() {
        val chatId = "${ad.seller}_${ad.title}"
        MessagesManager.getOrCreateChat(chatId, ad.seller, ad.title, ad.imageResId)
        val intent = Intent(this, MessageActivity::class.java)
        intent.putExtra("CHAT_ID", chatId)
        intent.putExtra("SELLER_NAME", ad.seller)
        intent.putExtra("PRODUCT_NAME", ad.title)
        intent.putExtra("AVATAR_ID", ad.imageResId)
        startActivity(intent)
    }

    private fun shareAd() {
        val adLink = "fefumarket://ad/${ad.title.replace(" ", "_")}"
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "Смотри объявление на FEFU Market: $adLink")
        }
        startActivity(Intent.createChooser(sendIntent, "Поделиться объявлением"))
    }
}