package com.example.fefumarket

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import android.widget.ArrayAdapter

class FiltersActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filters)

        val btnBack: ImageView = findViewById(R.id.btnBack)
        val dormSpinner: Spinner = findViewById(R.id.dormSpinner)
        val categorySpinner: Spinner = findViewById(R.id.categorySpinner)
        val conditionSpinner: Spinner = findViewById(R.id.conditionSpinner)
        val minPriceInput: EditText = findViewById(R.id.minPriceInput)
        val maxPriceInput: EditText = findViewById(R.id.maxPriceInput)
        val applyButton: MaterialButton = findViewById(R.id.applyFiltersButton)

        // Список корпусов
        val dorms = listOf(
            "Любой", "Город", "РГИСИ", "Корпус 1.8", "Корпус 1.9",
            "Корпус 1.10", "Корпус 1.11", "Корпус 1.12", "Корпус 1.13",
            "Корпус 2.1", "Корпус 2.2", "Корпус 2.3", "Корпус 2.4",
            "Корпус 2.5", "Корпус 2.6", "Корпус 2.7", "Корпус 4",
            "Корпус 5", "Корпус 6.1", "Корпус 6.2", "Корпус 7.1",
            "Корпус 7.2", "Корпус 8.1", "Корпус 8.2", "Корпус 9", "Корпус 10", "Корпус 11"
        )

        // Категории
        val categories = listOf(
            "Любая", "Одежда", "Обувь", "Техника", "Бьюти",
            "Еда", "Для учебы", "Мебель", "Барахло"
        )

        // Состояние
        val conditions = listOf(
            "Любое", "Б/у", "Новое"
        )

        val dormAdapter = ArrayAdapter(this, R.layout.spinner_item, dorms)
        dormAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        dormSpinner.adapter = dormAdapter

        val categoryAdapter = ArrayAdapter(this, R.layout.spinner_item, categories)
        categoryAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        categorySpinner.adapter = categoryAdapter

        val conditionAdapter = ArrayAdapter(this, R.layout.spinner_item, conditions)
        conditionAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        conditionSpinner.adapter = conditionAdapter

        btnBack.setOnClickListener { finish() }

        applyButton.setOnClickListener {
            val selectedDorm = dormSpinner.selectedItem.toString()
            val selectedCategory = categorySpinner.selectedItem.toString()
            val selectedCondition = conditionSpinner.selectedItem.toString()
            val minPrice = minPriceInput.text.toString()
            val maxPrice = maxPriceInput.text.toString()

            val resultIntent = Intent().apply {
                putExtra("DORM", selectedDorm)
                putExtra("CATEGORY", selectedCategory)
                putExtra("CONDITION", selectedCondition)
                putExtra("MIN_PRICE", minPrice)
                putExtra("MAX_PRICE", maxPrice)
            }

            setResult(Activity.RESULT_OK, resultIntent)
            Toast.makeText(this, "Фильтры применены", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}