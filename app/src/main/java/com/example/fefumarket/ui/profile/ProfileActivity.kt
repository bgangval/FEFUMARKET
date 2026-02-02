package com.example.fefumarket.ui.profile

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.fefumarket.R
import com.example.fefumarket.base.BaseActivity
import com.example.fefumarket.data.repository.SessionManager
import com.example.fefumarket.ui.auth.LoginActivity

class ProfileActivity : BaseActivity() {

    private lateinit var session: SessionManager

    private lateinit var profileImage: ImageView
    private lateinit var nameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var repeatPasswordInput: EditText
    private lateinit var btnSave: TextView
    private lateinit var btnLogout: TextView
    private lateinit var btnDelete: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        session = SessionManager(this)

        // 🔹 Инициализация полей
        profileImage = findViewById(R.id.profileImage)
        nameInput = findViewById(R.id.nameInput)
        passwordInput = findViewById(R.id.passwordInput)
        repeatPasswordInput = findViewById(R.id.repeatPasswordInput)
        btnSave = findViewById(R.id.saveButton)
        btnLogout = findViewById(R.id.logoutButton)
        btnDelete = findViewById(R.id.deleteButton)

        // 🔹 Загрузка сохранённых данных пользователя
        nameInput.setText(session.getUserName())
        passwordInput.setText(session.getPassword())
        session.getImagePath()?.let {
            val bmp = BitmapFactory.decodeFile(it)
            if (bmp != null) profileImage.setImageBitmap(bmp)
        }

        // 🔹 Выбор нового изображения профиля
        val pickImageLauncher = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->
            uri?.let {
                session.saveImagePath(uri.toString())
                Glide.with(this)
                    .load(uri)
                    .circleCrop()
                    .into(profileImage)
            }
        }

        profileImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        // 🔹 Сохранение имени и пароля пользователя
        btnSave.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val password = passwordInput.text.toString()
            val repeatPassword = repeatPasswordInput.text.toString()

            if (name.isEmpty()) {
                showToast("Введите имя")
                return@setOnClickListener
            }

            if (password.isNotEmpty()) {
                if (password.length < 6) {
                    showToast("Пароль должен быть не менее 6 символов")
                    return@setOnClickListener
                }
                if (password != repeatPassword) {
                    showToast("Пароли не совпадают")
                    return@setOnClickListener
                }
                session.savePassword(password) // 🔹 Сохранение нового пароля
            }

            session.saveUserName(name) // 🔹 Сохранение имени пользователя
            showToast("Данные сохранены")
        }

        // 🔹 Смена аккаунта
        btnLogout.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Сменить аккаунт")
            builder.setMessage("Вы точно хотите сменить аккаунт?")
            builder.setPositiveButton("Да") { dialog, _ ->
                session.clear() // 🔹 Очистка данных сессии
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent) // 🔹 Переход на экран логина
                showToast("Вы вышли из аккаунта")
                dialog.dismiss()
            }
            builder.setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()
        }

        // 🔹 Удаление аккаунта
        btnDelete.setOnClickListener { deleteAccount() }
    }

    // 🔹 Метод удаления аккаунта с подтверждением
    private fun deleteAccount() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Удалить аккаунт")
        builder.setMessage("Вы точно хотите удалить аккаунт?")
        builder.setPositiveButton("Да") { dialog, _ ->
            session.clearFull() // 🔹 Полная очистка сессии и данных пользователя
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent) // 🔹 Переход на экран логина
            showToast("Аккаунт удалён")
            dialog.dismiss()
        }
        builder.setNegativeButton("Отмена") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        setActiveNavItem(R.id.nav_profile) // 🔹 Подсветка активного элемента нижней навигации
    }
}