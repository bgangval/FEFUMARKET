package com.example.fefumarket.ui.home

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.fefumarket.R
import com.google.android.material.button.MaterialButton

class FiltersActivity : AppCompatActivity() {

    private val selectedDorms = mutableListOf<Boolean>()
    private val selectedCategories = mutableListOf<Boolean>()
    private val selectedConditions = mutableListOf<Boolean>()

    private val dorms = listOf(
        "Любой", "Город", "РГИСИ",
        "Корпус 1.8", "Корпус 1.9", "Корпус 1.10", "Корпус 1.11", "Корпус 1.12", "Корпус 1.13",
        "Корпус 2.1", "Корпус 2.2", "Корпус 2.3", "Корпус 2.4", "Корпус 2.5", "Корпус 2.6", "Корпус 2.7",
        "Корпус 4", "Корпус 5",
        "Корпус 6.1", "Корпус 6.2", "Корпус 7.1", "Корпус 7.2", "Корпус 8.1", "Корпус 8.2",
        "Корпус 9", "Корпус 10", "Корпус 11"
    )

    private val categories = listOf(
        "Любая",
        "Одежда",
        "Обувь",
        "Техника",
        "Бьюти",
        "Еда",
        "Для учебы",
        "Мебель",
        "Барахло"
    )

    private val conditions = listOf(
        "Новое",
        "Б/у",
        "Любое"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filters)

        val btnBack: ImageView = findViewById(R.id.btnBack)
        val dormSelectText: TextView = findViewById(R.id.dormSelectText)
        val categorySelectText: TextView = findViewById(R.id.categorySelectText)
        val conditionSelectText: TextView = findViewById(R.id.conditionSelectText)
        val minPriceInput: EditText = findViewById(R.id.minPriceInput)
        val maxPriceInput: EditText = findViewById(R.id.maxPriceInput)
        val applyButton: MaterialButton = findViewById(R.id.applyFiltersButton)
        val resetButton: MaterialButton = findViewById(R.id.resetFiltersButton)

        dorms.forEach { selectedDorms.add(false) }
        categories.forEach { selectedCategories.add(false) }
        conditions.forEach { selectedConditions.add(false) }

        // Загружаем переданные фильтры и отмечаем их
        intent?.let {
            val currentDorms = it.getStringArrayExtra("DORMS") ?: emptyArray()
            val currentCategories = it.getStringArrayExtra("CATEGORIES") ?: emptyArray()
            val currentConditions = it.getStringArrayExtra("CONDITIONS") ?: emptyArray()
            val currentMinPrice = it.getStringExtra("MIN_PRICE")
            val currentMaxPrice = it.getStringExtra("MAX_PRICE")

            dorms.forEachIndexed { index, dorm -> selectedDorms[index] = currentDorms.contains(dorm) }
            categories.forEachIndexed { index, cat -> selectedCategories[index] = currentCategories.contains(cat) }
            conditions.forEachIndexed { index, cond -> selectedConditions[index] = currentConditions.contains(cond) }

            dormSelectText.text = if (currentDorms.isEmpty()) "Выберите корпуса" else currentDorms.joinToString(", ")
            categorySelectText.text = if (currentCategories.isEmpty()) "Выберите категории" else currentCategories.joinToString(", ")
            conditionSelectText.text = if (currentConditions.isEmpty()) "Выберите состояние" else currentConditions.joinToString(", ")

            minPriceInput.setText(currentMinPrice)
            maxPriceInput.setText(currentMaxPrice)
        }

        btnBack.setOnClickListener { finish() }

        // Диалог выбора корпусов
        dormSelectText.setOnClickListener {
            showMultiChoiceDialog("Выберите корпуса", dorms, selectedDorms) { selected ->
                dormSelectText.text = if (selected.isEmpty()) "Выберите корпуса" else selected.joinToString(", ")
            }
        }

        // Диалог выбора категорий
        categorySelectText.setOnClickListener {
            showMultiChoiceDialog("Выберите категории", categories, selectedCategories) { selected ->
                categorySelectText.text = if (selected.isEmpty()) "Выберите категории" else selected.joinToString(", ")
            }
        }

        // Диалог выбора состояния
        conditionSelectText.setOnClickListener {
            showMultiChoiceDialog("Выберите состояние", conditions, selectedConditions) { selected ->
                conditionSelectText.text = if (selected.isEmpty()) "Выберите состояние" else selected.joinToString(", ")
            }
        }

        // Кнопка "Сброс"
        resetButton.setOnClickListener {
            for (i in selectedDorms.indices) selectedDorms[i] = false
            for (i in selectedCategories.indices) selectedCategories[i] = false
            for (i in selectedConditions.indices) selectedConditions[i] = false

            dormSelectText.text = "Выберите корпуса"
            categorySelectText.text = "Выберите категории"
            conditionSelectText.text = "Выберите состояние"

            minPriceInput.text.clear()
            maxPriceInput.text.clear()
        }

        // Кнопка "Применить"
        applyButton.setOnClickListener {
            val selectedDormNames = dorms.filterIndexed { index, dorm ->
                selectedDorms[index] && dorm != "Любой"
            }
            val selectedCategoryNames = categories.filterIndexed { index, cat -> selectedCategories[index] && cat != "Любая" }  // Уже ок
            val selectedConditionNames = conditions.filterIndexed { index, cond -> selectedConditions[index] && cond != "Любое" }  // Уже ок

            val minPrice = minPriceInput.text.toString()
            val maxPrice = maxPriceInput.text.toString()

            val resultIntent = Intent().apply {
                putExtra("DORMS", selectedDormNames.toTypedArray())
                putExtra("CATEGORIES", selectedCategoryNames.toTypedArray())
                putExtra("CONDITIONS", selectedConditionNames.toTypedArray())
                putExtra("MIN_PRICE", minPrice)
                putExtra("MAX_PRICE", maxPrice)
            }

            setResult(RESULT_OK, resultIntent)
            Toast.makeText(this, "Фильтры применены", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun showMultiChoiceDialog(
        title: String,
        items: List<String>,
        selectedItems: MutableList<Boolean>,
        onSelectionDone: (List<String>) -> Unit
    ) {
        val builder = AlertDialog.Builder(this, R.style.Theme_FEFUMARKET_Dialog)
        builder.setTitle(title)

        builder.setMultiChoiceItems(items.toTypedArray(), selectedItems.toBooleanArray()) { _, which, isChecked ->
            selectedItems[which] = isChecked
        }

        builder.setPositiveButton("Применить") { dialog, _ ->
            val selected = items.filterIndexed { index, _ -> selectedItems[index] }
            onSelectionDone(selected)
            dialog.dismiss()
        }

        builder.setNegativeButton("Отмена") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(resources.getColor(android.R.color.white, null))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(resources.getColor(android.R.color.white, null))

        val listView = dialog.listView
        for (i in 0 until listView.count) {
            val item = listView.getChildAt(i)
            item?.let {
                val textView = it.findViewById<TextView>(android.R.id.text1)
                textView?.setTextColor(resources.getColor(android.R.color.white, null))
            }
        }
    }
}