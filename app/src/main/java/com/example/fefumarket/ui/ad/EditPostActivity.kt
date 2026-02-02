package com.example.fefumarket.ui.ad

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.fefumarket.R
import com.example.fefumarket.data.models.Ad
import com.example.fefumarket.data.repository.AdRepository
import com.example.fefumarket.data.repository.FavoritesManager
import com.example.fefumarket.data.repository.SessionManager
import com.example.fefumarket.network.RetrofitClient
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.core.net.toUri

// Экран редактирования объявления
// Позволяет изменять данные объявления, добавлять фотографии,
// отмечать как проданное, управлять сохранением через AdRepository
class EditPostActivity : AppCompatActivity() {

    private lateinit var ad: Ad
    private lateinit var adRepository: AdRepository
    private lateinit var sessionManager: SessionManager

    private lateinit var photoPager: ViewPager2
    private lateinit var btnAddPhoto: ImageButton
    private lateinit var etTitle: EditText
    private lateinit var etPrice: EditText
    private lateinit var spinnerDorm: Spinner
    private lateinit var etDescription: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var spinnerCondition: Spinner
    private lateinit var btnSave: MaterialButton
    private lateinit var btnSold: MaterialButton

    private val photoList = mutableListOf<Uri>()

    // 🔹 Логика выбора нескольких фотографий
    private val pickImagesLauncher = registerForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            photoList.clear()
            photoList.addAll(uris)
            photoPager.adapter?.notifyDataSetChanged()
        }
    }

    private val dorms = listOf(
        "Город", "РГИСИ", "Корпус 1.8", "Корпус 1.9", "Корпус 1.10", "Корпус 1.11",
        "Корпус 1.12", "Корпус 1.13", "Корпус 2.1", "Корпус 2.2", "Корпус 2.3",
        "Корпус 2.4", "Корпус 2.5", "Корпус 2.6", "Корпус 2.7", "Корпус 4", "Корпус 5",
        "Корпус 6.1", "Корпус 6.2", "Корпус 7.1", "Корпус 7.2", "Корпус 8.1", "Корпус 8.2",
        "Корпус 9", "Корпус 10", "Корпус 11"
    )
    private val categories = listOf(
        "Одежда", "Обувь", "Техника", "Бьюти", "Еда", "Для учебы", "Мебель", "Барахло", "Другое"
    )
    private val conditions = listOf("Новое", "Б/у")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_post)

        // 🔹 Инициализация SessionManager и репозитория
        sessionManager = SessionManager(this)
        val api = RetrofitClient.create(this)
        adRepository = AdRepository(api, sessionManager)

        // ---------- Инициализация UI ----------
        photoPager = findViewById(R.id.photoPager)
        btnAddPhoto = findViewById(R.id.btnAddPhoto)
        etTitle = findViewById(R.id.etTitle)
        etPrice = findViewById(R.id.etPrice)
        spinnerDorm = findViewById(R.id.spinnerDorm)
        etDescription = findViewById(R.id.etDescription)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        spinnerCondition = findViewById(R.id.spinnerCondition)
        btnSave = findViewById(R.id.btnSave)
        btnSold = findViewById(R.id.btnSold)
        findViewById<ImageButton>(R.id.btnBack)?.setOnClickListener { finish() }

        btnAddPhoto.setOnClickListener { pickImagesLauncher.launch("image/*") }
        btnSave.setOnClickListener { saveChanges() }
        btnSold.setOnClickListener { markAsSold() }

        // 🔹 Загружаем объявление по title через AdRepository
        val adTitle = intent.getStringExtra("AD_TITLE") ?: ""
        CoroutineScope(Dispatchers.IO).launch {
            val foundAd = adRepository.findByTitle(adTitle)
            if (foundAd == null) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EditPostActivity, "Объявление не найдено", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } else {
                ad = foundAd
                withContext(Dispatchers.Main) { initFields() }
            }
        }
    }

    // Инициализация полей с данными объявления
    private fun initFields() {
        // Фото
        if (ad.imageUris.isNotEmpty()) {
            photoList.clear()
            photoList.addAll(ad.imageUris.map { it.toUri() })
        }
        photoPager.adapter = PhotoPagerAdapter(photoList)

        // Текстовые поля
        etTitle.setText(ad.title)
        etPrice.setText(ad.price.filter { it.isDigit() })
        etDescription.setText(ad.description)

        // Spinner
        spinnerDorm.adapter = whiteTextAdapter(dorms)
        spinnerDorm.setSelection(dorms.indexOf(ad.dorm).coerceAtLeast(0))
        spinnerCategory.adapter = whiteTextAdapter(categories)
        spinnerCategory.setSelection(categories.indexOf(ad.category).coerceAtLeast(0))
        spinnerCondition.adapter = whiteTextAdapter(conditions)
        spinnerCondition.setSelection(conditions.indexOf(ad.condition).coerceAtLeast(0))
    }

    // 🔹 Сохранение изменений через AdRepository
    private fun saveChanges() {
        val newTitle = etTitle.text.toString().trim()
        val newPriceText = etPrice.text.toString().trim()
        val newPrice = "₽$newPriceText"
        val newDorm = spinnerDorm.selectedItem?.toString()?.trim() ?: ""
        val newDescription = etDescription.text.toString().trim()
        val newCategory = spinnerCategory.selectedItem?.toString()?.trim() ?: ""
        val newCondition = spinnerCondition.selectedItem?.toString()?.trim() ?: ""
        val newImageUris = photoList.map { it.toString() }

        if (newTitle.isEmpty() || newPriceText.isEmpty()) {
            Toast.makeText(this, "Заполните название и цену", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedAd = ad.copy(
            title = newTitle,
            price = newPrice,
            dorm = newDorm,
            description = newDescription,
            category = newCategory,
            condition = newCondition,
            imageUris = newImageUris
        )

        CoroutineScope(Dispatchers.IO).launch {
            adRepository.updateAd(updatedAd) // 🔹 обновление объявления через репозиторий
            withContext(Dispatchers.Main) {
                Toast.makeText(this@EditPostActivity, "Объявление обновлено", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    // 🔹 Отметка объявления как проданное
    private fun markAsSold() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Отметить как проданное")
        builder.setMessage("Вы точно хотите отметить это объявление как проданное?")
        builder.setPositiveButton("Да") { dialog, _ ->
            CoroutineScope(Dispatchers.IO).launch {
                adRepository.removeAd(ad.id) // 🔹 удаление через репозиторий
                FavoritesManager.remove(ad)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EditPostActivity, "Объявление отмечено как проданное", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                    finish()
                }
            }
        }
        builder.setNegativeButton("Отмена") { dialog, _ -> dialog.dismiss() }
        builder.create().show()
    }

    // ---------- Адаптер для ViewPager ----------
    inner class PhotoPagerAdapter(private val photos: List<Uri>) : RecyclerView.Adapter<PhotoPagerAdapter.PhotoViewHolder>() {
        inner class PhotoViewHolder(itemView: ImageView) : RecyclerView.ViewHolder(itemView) {
            val imageView: ImageView = itemView
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
            val imageView = ImageView(parent.context).apply {
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
            return PhotoViewHolder(imageView)
        }

        override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
            Glide.with(holder.imageView).load(photos[position]).into(holder.imageView)
        }

        override fun getItemCount(): Int = photos.size
    }

    // ---------- Адаптер Spinner с белым текстом ----------
    private fun whiteTextAdapter(items: List<String>): ArrayAdapter<String> {
        return object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                (view as TextView).setTextColor(resources.getColor(R.color.white, null))
                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                (view as TextView).setTextColor(resources.getColor(R.color.white, null))
                view.setBackgroundColor(resources.getColor(R.color.bg, null))
                return view
            }
        }.apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
    }
}