package com.example.fefumarket.ui.ad

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager2.widget.ViewPager2
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.fefumarket.R
import com.example.fefumarket.data.models.Ad
import com.example.fefumarket.data.repository.AdRepository
import com.example.fefumarket.data.repository.FavoritesManager
import com.example.fefumarket.data.repository.MessagesManager
import com.example.fefumarket.network.RetrofitClient
import com.example.fefumarket.ui.chat.MessageActivity
import kotlinx.coroutines.launch

class AdDetailActivity : AppCompatActivity() {

    private lateinit var ad: Ad
    private lateinit var photoPager: ViewPager2
    private lateinit var adTitle: TextView
    private lateinit var adPrice: TextView
    private lateinit var adDorm: TextView
    private lateinit var adDescription: TextView
    private lateinit var adCategory: TextView
    private lateinit var adCondition: TextView
    private lateinit var btnFavoriteTop: ImageButton

    private lateinit var adRepository: AdRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ad_detail)

        // ---------- UI ----------
        photoPager = findViewById(R.id.photoPager)
        adTitle = findViewById(R.id.titleText)
        adPrice = findViewById(R.id.priceText)
        adDorm = findViewById(R.id.dormText)
        adDescription = findViewById(R.id.descriptionText)
        btnFavoriteTop = findViewById(R.id.btnFavoriteTop)
        adCategory = findViewById(R.id.categoryText)
        adCondition = findViewById(R.id.conditionText)

        val btnBack: ImageButton = findViewById(R.id.btnBack)
        val btnFavorite: Button = findViewById(R.id.favoriteButton)
        val btnChat: Button = findViewById(R.id.contactButton)
        val btnShareTop: ImageButton = findViewById(R.id.btnChatTop)

        // ---------- Создаём репозиторий ----------
        adRepository = AdRepository(RetrofitClient.create(this))

        // ---------- Получаем ID объявления ----------
        val adIdFromIntent = intent.getStringExtra("AD_ID")
        val adIdFromDeepLink = intent?.data?.lastPathSegment
        val resolvedId = adIdFromIntent ?: adIdFromDeepLink

        if (resolvedId == null) {
            Toast.makeText(this, "Ошибка загрузки объявления", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // ---------- Загружаем объявление асинхронно ----------
        lifecycleScope.launch {
            val foundAd = adRepository.getAdById(resolvedId)
            if (foundAd == null) {
                Toast.makeText(this@AdDetailActivity, "Объявление не найдено", Toast.LENGTH_SHORT).show()
                finish()
                return@launch
            }
            ad = foundAd
            updateUI()
        }

        // ---------- Кнопка "Назад" ----------
        btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        // ---------- Избранное ----------
        var isFavorite = false
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

        // ---------- Чат ----------
        btnChat.setOnClickListener { openChat() }

        // ---------- Поделиться ----------
        btnShareTop.setOnClickListener { shareAd() }
    }

    // ---------- Обновление объявления при возврате ----------
    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            val updatedAd = adRepository.getAdById(ad.id)
            if (updatedAd != null) {
                ad = updatedAd
                updateUI()
            }
        }
    }

    private fun updateUI() {
        adTitle.text = ad.title
        adPrice.text = ad.price
        adDorm.text = ad.dorm
        adDescription.text = ad.description
        adCategory.text = ad.category
        adCondition.text = ad.condition

        val photos: List<Uri> =
            if (ad.imageUris.isNotEmpty())
                ad.imageUris.map { it.toUri() }
            else
                listOf(Uri.parse("android.resource://${packageName}/${R.drawable.ic_camera}"))

        photoPager.adapter = PhotoPagerAdapter(photos)
    }

    private fun updateFavoriteIcon(button: ImageButton, isFavorite: Boolean) {
        button.setImageResource(
            if (isFavorite) R.drawable.ic_heart_red
            else R.drawable.ic_heart_top_bar
        )
    }

    private fun openChat() {
        val chatId = "${ad.seller}_${ad.id}"
        val avatar = ad.imageUris.firstOrNull() ?: ""
        MessagesManager.getOrCreateChat(chatId, ad.seller, ad.title, avatar)

        val intent = Intent(this, MessageActivity::class.java).apply {
            putExtra("CHAT_ID", chatId)
            putExtra("SELLER_NAME", ad.seller)
            putExtra("PRODUCT_NAME", ad.title)
            putExtra("AVATAR_URI", avatar)
        }
        startActivity(intent)
    }

    private fun shareAd() {
        val deepLink = Uri.parse("fefumarket://ad/${ad.id}")
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "Смотри объявление: $deepLink")
        }
        startActivity(Intent.createChooser(sendIntent, "Поделиться объявлением"))
    }

    // ---------- Адаптер для ViewPager ----------
    inner class PhotoPagerAdapter(private val photos: List<Uri>) :
        RecyclerView.Adapter<PhotoPagerAdapter.PhotoViewHolder>() {

        inner class PhotoViewHolder(itemView: ImageView) : RecyclerView.ViewHolder(itemView) {
            val imageView: ImageView = itemView
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
            val imageView = ImageView(parent.context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
            return PhotoViewHolder(imageView)
        }

        override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
            Glide.with(holder.imageView).load(photos[position]).into(holder.imageView)
        }

        override fun getItemCount(): Int = photos.size
    }
}