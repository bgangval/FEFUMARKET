package com.example.fefumarket.ad

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
import com.google.android.material.button.MaterialButton
import androidx.core.net.toUri
import com.example.fefumarket.R
import com.example.fefumarket.data.models.Ad
import com.example.fefumarket.data.repository.AdRepository

class EditPostActivity : AppCompatActivity() {

    private lateinit var ad: Ad

    private lateinit var photoPager: ViewPager2
    private lateinit var btnAddPhoto: ImageButton
    private lateinit var etTitle: EditText
    private lateinit var etPrice: EditText
    private lateinit var spinnerDorm: Spinner
    private lateinit var etDescription: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var spinnerCondition: Spinner
    private lateinit var btnSave: MaterialButton

    private val photoList = mutableListOf<Uri>() // список выбранных фото

    private val pickImagesLauncher = registerForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            photoList.clear()
            photoList.addAll(uris)
            photoPager.adapter?.notifyDataSetChanged()
        }
    }

    // Списки для Spinner
    private val dorms = listOf(
        "Город", "РГИСИ",
        "Корпус 1.8", "Корпус 1.9", "Корпус 1.10", "Корпус 1.11",
        "Корпус 1.12", "Корпус 1.13", "Корпус 2.1", "Корпус 2.2",
        "Корпус 2.3", "Корпус 2.4", "Корпус 2.5", "Корпус 2.6",
        "Корпус 2.7", "Корпус 4", "Корпус 5", "Корпус 6.1",
        "Корпус 6.2", "Корпус 7.1", "Корпус 7.2", "Корпус 8.1",
        "Корпус 8.2", "Корпус 9", "Корпус 10", "Корпус 11"
    )

    private val categories = listOf(
        "Одежда", "Обувь", "Техника", "Бьюти",
        "Еда", "Для учебы", "Мебель", "Барахло", "Другое"
    )

    private val conditions = listOf("Новое", "Б/у")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_post)

        photoPager = findViewById(R.id.photoPager)
        btnAddPhoto = findViewById(R.id.btnAddPhoto)
        etTitle = findViewById(R.id.etTitle)
        etPrice = findViewById(R.id.etPrice)
        spinnerDorm = findViewById(R.id.spinnerDorm)
        etDescription = findViewById(R.id.etDescription)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        spinnerCondition = findViewById(R.id.spinnerCondition)
        btnSave = findViewById(R.id.btnSave)

        val adTitle = intent.getStringExtra("AD_TITLE") ?: ""
        ad = AdRepository.findByTitle(adTitle) ?: run {
            Toast.makeText(this, "Объявление не найдено", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        initFields()

        btnAddPhoto.setOnClickListener { pickImagesLauncher.launch("image/*") }
        btnSave.setOnClickListener { saveChanges() }
        findViewById<ImageButton>(R.id.btnBack)?.setOnClickListener { finish() }
    }

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

        // Spinner'ы
        spinnerDorm.adapter = whiteTextAdapter(dorms)
        spinnerDorm.setSelection(dorms.indexOf(ad.dorm).coerceAtLeast(0))

        spinnerCategory.adapter = whiteTextAdapter(categories)
        spinnerCategory.setSelection(categories.indexOf(ad.category).coerceAtLeast(0))

        spinnerCondition.adapter = whiteTextAdapter(conditions)
        spinnerCondition.setSelection(conditions.indexOf(ad.condition).coerceAtLeast(0))
    }

    private fun saveChanges() {
        val newTitle = etTitle.text.toString().trim()
        val newPriceText = etPrice.text.toString().trim()
        val newPrice = "₽$newPriceText"
        val newDorm = spinnerDorm.selectedItem?.toString()?.trim() ?: ""
        val newDescription = etDescription.text.toString().trim()
        val newCategory = spinnerCategory.selectedItem?.toString()?.trim() ?: ""
        val newCondition = spinnerCondition.selectedItem?.toString()?.trim() ?: ""

        if (newTitle.isEmpty() || newPriceText.isEmpty()) {
            Toast.makeText(this, "Заполните название и цену", Toast.LENGTH_SHORT).show()
            return
        }

        val newImageUris = photoList.map { it.toString() }

        val updatedAd = ad.copy(
            title = newTitle,
            price = newPrice,
            dorm = newDorm,
            description = newDescription,
            category = newCategory,
            condition = newCondition,
            imageUris = newImageUris
        )

        // Обновляем репозиторий
        AdRepository.updateAd(updatedAd)

        Toast.makeText(this, "Объявление обновлено", Toast.LENGTH_SHORT).show()
        finish()
    }

    // Адаптер для ViewPager
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
        }.apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
    }
}