package com.example.fefumarket.ui.ad

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.fefumarket.R
import com.example.fefumarket.data.models.Ad
import com.example.fefumarket.data.repository.AdRepository
import com.example.fefumarket.data.repository.CategoryRepository
import com.example.fefumarket.data.repository.SessionManager
import com.example.fefumarket.network.RetrofitClient
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.util.UUID

// Экран добавления нового объявления.
// Отвечает за сбор данных от пользователя и передачу их в слой репозитория.
class AddPostActivity : AppCompatActivity() {

    private lateinit var photoPager: ViewPager2
    private lateinit var btnAddPhoto: ImageButton
    private lateinit var etTitle: EditText
    private lateinit var etPrice: EditText
    private lateinit var etDescription: EditText
    private lateinit var spinnerDorm: Spinner
    private lateinit var spinnerCategory: Spinner
    private lateinit var spinnerCondition: Spinner
    private lateinit var btnSave: MaterialButton

    private val dorms = listOf(
        "Город", "РГИСИ",
        "Корпус 1.8", "Корпус 1.9", "Корпус 1.10", "Корпус 1.11",
        "Корпус 1.12", "Корпус 1.13", "Корпус 2.1", "Корпус 2.2",
        "Корпус 2.3", "Корпус 2.4", "Корпус 2.5", "Корпус 2.6",
        "Корпус 2.7", "Корпус 4", "Корпус 5", "Корпус 6.1",
        "Корпус 6.2", "Корпус 7.1", "Корпус 7.2", "Корпус 8.1",
        "Корпус 8.2", "Корпус 9", "Корпус 10", "Корпус 11"
    )

    private val categories = mutableListOf<String>()
    private val conditions = listOf("Новое", "Б/у")

    private val photoList = mutableListOf<Uri>()

    private lateinit var adRepository: AdRepository
    private lateinit var categoryRepository: CategoryRepository
    private lateinit var sessionManager: SessionManager

    // Получение изображений из системной галереи (взаимодействие с внешним Activity)
    private val pickImagesLauncher = registerForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            photoList.clear()
            photoList.addAll(uris)
            photoPager.adapter?.notifyDataSetChanged()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)

        // Инициализация SessionManager и репозиториев
        // Связь UI-слоя с сетевым и слоем данных
        sessionManager = SessionManager(this)
        val api = RetrofitClient.create(this)
        adRepository = AdRepository(api, sessionManager)
        categoryRepository = CategoryRepository(api, sessionManager)

        photoPager = findViewById(R.id.photoPager)
        btnAddPhoto = findViewById(R.id.btnAddPhoto)
        etTitle = findViewById(R.id.etTitle)
        etPrice = findViewById(R.id.etPrice)
        etDescription = findViewById(R.id.etDescription)
        spinnerDorm = findViewById(R.id.spinnerDorm)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        spinnerCondition = findViewById(R.id.spinnerCondition)
        btnSave = findViewById(R.id.btnSave)

        // Возврат на предыдущий экран
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        photoPager.adapter = PhotoPagerAdapter(photoList)

        // Запуск системного выбора изображений
        btnAddPhoto.setOnClickListener {
            pickImagesLauncher.launch("image/*")
        }

        spinnerDorm.adapter = whiteTextAdapter(dorms)
        spinnerCondition.adapter = whiteTextAdapter(conditions)

        // Чтобы spinner не был пустым даже при пустых/недоступных категориях на сервере
        categories.clear()
        categories.addAll(defaultCategories())
        spinnerCategory.adapter = whiteTextAdapter(categories)

        // Загружаем категории с сервера
        loadCategories()

        btnSave.setOnClickListener { saveAd() }
    }

    // 🔹 Загрузка категорий с сервера
    private fun loadCategories() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val serverCategories = categoryRepository.getCategories()
                categories.clear()
                if (serverCategories.isEmpty()) {
                    categories.addAll(defaultCategories())
                } else {
                    categories.addAll(serverCategories.map { it.name })
                }
                
                withContext(Dispatchers.Main) {
                    spinnerCategory.adapter = whiteTextAdapter(categories)
                }
            } catch (e: Exception) {
                // Fallback на локальные категории
                withContext(Dispatchers.Main) {
                    categories.clear()
                    categories.addAll(defaultCategories())
                    spinnerCategory.adapter = whiteTextAdapter(categories)
                }
            }
        }
    }

    private fun defaultCategories(): List<String> {
        return listOf(
            "Одежда", "Обувь", "Техника", "Бьюти",
            "Еда", "Для учебы", "Мебель", "Барахло", "Другое"
        )
    }

    // Формирование модели объявления и передача её в репозиторий
    // Логика взаимодействия экрана с бизнес-слоем
    private fun saveAd() {
        val title = etTitle.text.toString().trim()
        val priceText = etPrice.text.toString().trim()
        val dorm = spinnerDorm.selectedItem?.toString()?.trim() ?: ""
        val description = etDescription.text.toString().trim()
        val category = spinnerCategory.selectedItem?.toString()?.trim() ?: ""
        val condition = spinnerCondition.selectedItem?.toString()?.trim() ?: ""

        if (title.isEmpty() || priceText.isEmpty()) {
            Toast.makeText(this, "Введите название и цену", Toast.LENGTH_SHORT).show()
            return
        }

        // Получение данных пользователя из SessionManager
        val seller = sessionManager.getUserName()
            ?.takeIf { it.isNotBlank() }
            ?: sessionManager.getLogin()
            ?: "Без имени"

        val newAd = Ad(
            id = UUID.randomUUID().toString(),
            title = title,
            price = "₽$priceText",
            dorm = dorm,
            seller = seller,
            description = description,
            category = category,
            condition = condition,
            imageUris = photoList.map { it.toString() }
        )

        // Передача данных в репозиторий (слой данных)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val createdAd = adRepository.addAd(newAd)
                
                // Загружаем изображения после создания объявления
                val productId = createdAd.id.toIntOrNull()
                if (productId != null && photoList.isNotEmpty()) {
                    uploadImages(productId)
                }
                
                runOnUiThread {
                    Toast.makeText(
                        this@AddPostActivity,
                        "Объявление опубликовано",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(
                        this@AddPostActivity,
                        "Ошибка при публикации: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    // 🔹 Загрузка изображений на сервер
    private suspend fun uploadImages(productId: Int) {
        try {
            photoList.forEach { uri ->
                val scheme = uri.scheme?.lowercase()
                if (scheme != "content" && scheme != "file") return@forEach

                // Для content:// URI используем InputStream
                val inputStream = contentResolver.openInputStream(uri) ?: return@forEach
                val bytes = inputStream.readBytes()
                inputStream.close()
                
                val requestFile = okhttp3.RequestBody.create("image/*".toMediaTypeOrNull(), bytes)
                val imagePart = MultipartBody.Part.createFormData("image", "image_${System.currentTimeMillis()}.jpg", requestFile)
                adRepository.uploadProductImage(productId, imagePart)
            }
        } catch (e: Exception) {
            // Логируем ошибку, но не прерываем процесс
            android.util.Log.e("AddPostActivity", "Ошибка загрузки изображений: ${e.message}")
        }
    }

    // Адаптер для отображения фотографий объявления
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
                setBackgroundColor(resources.getColor(R.color.light_gray, null))
            }
            return PhotoViewHolder(imageView)
        }

        override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
            Glide.with(holder.imageView)
                .load(photos[position])
                .placeholder(R.drawable.ic_camera)
                .centerCrop()
                .into(holder.imageView)
        }

        override fun getItemCount(): Int = photos.size
    }

    // Адаптер для Spinner с кастомным отображением
    private fun whiteTextAdapter(items: List<String>): ArrayAdapter<String> {
        return object : ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            items
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                (view as TextView).apply {
                    setTextColor(resources.getColor(R.color.white, null))
                    textSize = 16f
                }
                return view
            }

            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val view = super.getDropDownView(position, convertView, parent)
                (view as TextView).apply {
                    setTextColor(resources.getColor(R.color.white, null))
                    setBackgroundColor(resources.getColor(R.color.bg, null))
                }
                return view
            }
        }.apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
    }
}
