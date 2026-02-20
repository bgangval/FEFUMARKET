package com.example.fefumarket.ui.ad

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.fefumarket.R
import com.example.fefumarket.data.models.Ad
import com.example.fefumarket.data.repository.AdRepository
import com.example.fefumarket.data.repository.ChatRepository
import com.example.fefumarket.data.repository.FavoritesManager
import com.example.fefumarket.data.repository.MessagesManager
import com.example.fefumarket.data.repository.SessionManager
import com.example.fefumarket.network.RetrofitClient
import com.example.fefumarket.ui.chat.MessageActivity
import kotlinx.coroutines.launch

// Экран детального просмотра объявления
// Отображает информацию о товаре, фотографии, управление избранным,
// возможность открыть чат с продавцом и поделиться объявлением через deep link
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
    private lateinit var chatRepository: ChatRepository
    private lateinit var sessionManager: SessionManager // 🔹 SessionManager для AdRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ad_detail)

        // ---------- Инициализация UI ----------
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

        // ---------- Создание SessionManager и репозиториев ----------
        sessionManager = SessionManager(this)
        val api = RetrofitClient.create(this)
        adRepository = AdRepository(api, sessionManager)
        chatRepository = ChatRepository(api, sessionManager)

        // ---------- Получение ID объявления ----------
        val adIdFromIntent = intent.getStringExtra("AD_ID")
        val adIdFromDeepLink = intent?.data?.lastPathSegment
        val resolvedId = adIdFromIntent ?: adIdFromDeepLink

        if (resolvedId == null) {
            Toast.makeText(this, "Ошибка загрузки объявления", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // ---------- Загрузка объявления с использованием репозитория ----------
        var isFavorite = false
        lifecycleScope.launch {
            val foundAd = adRepository.getAdById(resolvedId)
            if (foundAd == null) {
                Toast.makeText(this@AdDetailActivity, "Объявление не найдено", Toast.LENGTH_SHORT).show()
                finish()
                return@launch
            }
            ad = foundAd
            
            // Проверяем, является ли объявление избранным
            try {
                val favorites = adRepository.getFavorites()
                isFavorite = favorites.any { it.id == ad.id }
            } catch (e: Exception) {
                // Если ошибка, используем локальный кэш как fallback
                isFavorite = FavoritesManager.isFavorite(ad)
            }
            
            updateUI()
            updateFavoriteIcon(btnFavoriteTop, isFavorite)
        }

        // ---------- Настройка кнопок ----------
        // btnBack — возвращение на предыдущий экран
        btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        // btnFavoriteTop / btnFavorite — добавление/удаление объявления из избранного через API
        btnFavoriteTop.setOnClickListener {
            lifecycleScope.launch {
                try {
                    val productId = ad.id.toIntOrNull() 
                        ?: throw Exception("Invalid product ID")
                    
                    isFavorite = !isFavorite
                    if (isFavorite) {
                        adRepository.addFavorite(productId)
                        FavoritesManager.add(ad) // Также обновляем локальный кэш
                    } else {
                        adRepository.removeFavorite(productId)
                        FavoritesManager.remove(ad) // Также обновляем локальный кэш
                    }
                    updateFavoriteIcon(btnFavoriteTop, isFavorite)
                } catch (e: Exception) {
                    Toast.makeText(
                        this@AdDetailActivity,
                        "Ошибка: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        btnFavorite.setOnClickListener {
            lifecycleScope.launch {
                try {
                    val productId = ad.id.toIntOrNull() 
                        ?: throw Exception("Invalid product ID")
                    
                    isFavorite = !isFavorite
                    if (isFavorite) {
                        adRepository.addFavorite(productId)
                        FavoritesManager.add(ad) // Также обновляем локальный кэш
                    } else {
                        adRepository.removeFavorite(productId)
                        FavoritesManager.remove(ad) // Также обновляем локальный кэш
                    }
                    updateFavoriteIcon(btnFavoriteTop, isFavorite)
                } catch (e: Exception) {
                    Toast.makeText(
                        this@AdDetailActivity,
                        "Ошибка: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        // btnChat — открытие чата через MessagesManager
        btnChat.setOnClickListener { openChat() }

        // btnShareTop — создание deep link и вызов стандартного Share Intent
        btnShareTop.setOnClickListener { shareAd() }
    }

    // ---------- Обновление объявления при возврате на экран ----------
    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            val updatedAd = adRepository.getAdById(ad.id) // проверяем актуальность объявления
            if (updatedAd != null) {
                ad = updatedAd
                updateUI()
            }
        }
    }

    // ---------- Вспомогательные методы ----------

    // Обновление UI элементов с данными объявления
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

    // Обновление иконки "избранного"
    private fun updateFavoriteIcon(button: ImageButton, isFavorite: Boolean) {
        button.setImageResource(
            if (isFavorite) R.drawable.ic_heart_red
            else R.drawable.ic_heart_top_bar
        )
    }

    // 🔹 Логика чата между слоями: создание через API и переход в MessageActivity
    private fun openChat() {
        lifecycleScope.launch {
            try {
                val productId = ad.id.toIntOrNull() 
                    ?: throw Exception("Invalid product ID")
                
                // Создаем или получаем чат через API
                val chatOut = chatRepository.getOrCreateChat(productId)
                
                // Сохраняем локально для совместимости
                val chatId = chatOut.id.toString()
                val avatar = ad.imageUris.firstOrNull() ?: ""
                MessagesManager.getOrCreateChat(chatId, ad.seller, ad.title, avatar)

                val intent = Intent(this@AdDetailActivity, MessageActivity::class.java).apply {
                    putExtra("CHAT_ID", chatId)
                    putExtra("API_CHAT_ID", chatOut.id)
                    putExtra("SELLER_NAME", ad.seller)
                    putExtra("PRODUCT_NAME", ad.title)
                    putExtra("AVATAR_URI", avatar)
                }
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(
                    this@AdDetailActivity,
                    "Ошибка создания чата: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
                // Fallback на локальный чат
                val chatId = "${ad.seller}_${ad.id}"
                val avatar = ad.imageUris.firstOrNull() ?: ""
                MessagesManager.getOrCreateChat(chatId, ad.seller, ad.title, avatar)
                
                val intent = Intent(this@AdDetailActivity, MessageActivity::class.java).apply {
                    putExtra("CHAT_ID", chatId)
                    putExtra("SELLER_NAME", ad.seller)
                    putExtra("PRODUCT_NAME", ad.title)
                    putExtra("AVATAR_URI", avatar)
                }
                startActivity(intent)
            }
        }
    }

    // 🔹 Поделиться объявлением через deep link
    private fun shareAd() {
        val deepLink = Uri.parse("fefumarket://ad/${ad.id}")
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "Смотри объявление: $deepLink")
        }
        startActivity(Intent.createChooser(sendIntent, "Поделиться объявлением"))
    }

    // ---------- Адаптер для ViewPager ----------
    // Отображает фотографии объявления
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