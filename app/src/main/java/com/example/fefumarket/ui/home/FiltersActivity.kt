package com.example.fefumarket.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
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
        "Барахло",
        "Другое"
    )

    private val conditions = listOf("Новое", "Б/у", "Любое")

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

        // Инициализация списков выбранных фильтров
        dorms.forEach { selectedDorms.add(false) }
        categories.forEach { selectedCategories.add(false) }
        conditions.forEach { selectedConditions.add(false) }

        // 🔹 Получение текущих фильтров из Intent, если активити открыта для редактирования фильтров
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

        // Диалоги выбора фильтров
        dormSelectText.setOnClickListener {
            showMultiChoiceDialog("Выберите корпуса", dorms, selectedDorms) { selected ->
                dormSelectText.text = if (selected.isEmpty()) "Выберите корпуса" else selected.joinToString(", ")
            }
        }

        categorySelectText.setOnClickListener {
            showMultiChoiceDialog("Выберите категории", categories, selectedCategories) { selected ->
                categorySelectText.text = if (selected.isEmpty()) "Выберите категории" else selected.joinToString(", ")
            }
        }

        conditionSelectText.setOnClickListener {
            showMultiChoiceDialog("Выберите состояние", conditions, selectedConditions) { selected ->
                conditionSelectText.text = if (selected.isEmpty()) "Выберите состояние" else selected.joinToString(", ")
            }
        }

        // Кнопка "Сброс" фильтров
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

        // 🔹 Кнопка "Применить" фильтры
        applyButton.setOnClickListener {
            val selectedDormNames = dorms.filterIndexed { index, dorm -> selectedDorms[index] && dorm != "Любой" }
            val selectedCategoryNames = categories.filterIndexed { index, cat -> selectedCategories[index] && cat != "Любая" }
            val selectedConditionNames = conditions.filterIndexed { index, cond -> selectedConditions[index] && cond != "Любое" }

            val minPrice = minPriceInput.text.toString()
            val maxPrice = maxPriceInput.text.toString()

            // 🔹 Отправка выбранных фильтров обратно в HomeActivity через Intent
            val resultIntent = Intent().apply {
                putExtra("DORMS", selectedDormNames.toTypedArray())
                putExtra("CATEGORIES", selectedCategoryNames.toTypedArray())
                putExtra("CONDITIONS", selectedConditionNames.toTypedArray())
                putExtra("MIN_PRICE", minPrice)
                putExtra("MAX_PRICE", maxPrice)
            }

            setResult(RESULT_OK, resultIntent)  // 🔹 установка результата для HomeActivity
            Toast.makeText(this, "Фильтры применены", Toast.LENGTH_SHORT).show()
            finish() // закрываем FiltersActivity
        }
    }

    // Вспомогательный метод для отображения диалога с множественным выбором
    private fun showMultiChoiceDialog(
        title: String,
        items: List<String>,
        selectedItems: MutableList<Boolean>,
        onSelectionDone: (List<String>) -> Unit
    ) {
        val builder = AlertDialog.Builder(this, R.style.Theme_FEFUMARKET_Dialog)
        builder.setTitle(title)

        val adapter = object : ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_multiple_choice,
            items
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                (view.findViewById<TextView>(android.R.id.text1)).apply {
                    setTextColor(resources.getColor(android.R.color.white, null))
                    textSize = 16f
                }
                view.setBackgroundColor(resources.getColor(R.color.bg, null))
                return view
            }
        }
        builder.setAdapter(adapter, null)

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

        // Настройка цветов кнопок и текста
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(resources.getColor(android.R.color.white, null))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(resources.getColor(android.R.color.white, null))

        val listView = dialog.listView
        listView.choiceMode = ListView.CHOICE_MODE_MULTIPLE
        listView.setBackgroundColor(resources.getColor(R.color.bg, null))
        selectedItems.forEachIndexed { index, isChecked ->
            listView.setItemChecked(index, isChecked)
        }
        listView.setOnItemClickListener { _, _, which, _ ->
            selectedItems[which] = listView.isItemChecked(which)
        }
    }
}
