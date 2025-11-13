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
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.fefumarket.R
import com.example.fefumarket.data.repository.SessionManager
import com.example.fefumarket.ui.ad.MyPostsActivity
import com.example.fefumarket.ui.auth.LoginActivity
import com.example.fefumarket.ui.chat.ChatActivity
import com.example.fefumarket.ui.favorites.FavoritesActivity
import com.example.fefumarket.ui.home.HomeActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class ProfileActivity : AppCompatActivity() {

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

        // Инициализация полей
        profileImage = findViewById(R.id.profileImage)
        nameInput = findViewById(R.id.nameInput)
        passwordInput = findViewById(R.id.passwordInput)
        repeatPasswordInput = findViewById(R.id.repeatPasswordInput)
        btnSave = findViewById(R.id.saveButton)
        btnLogout = findViewById(R.id.logoutButton)
        btnDelete = findViewById(R.id.deleteButton)

        setupBottomNavigation()

        // Загружаем сохранённые данные
        nameInput.setText(session.getUserName())
        passwordInput.setText(session.getPassword())

        session.getImagePath()?.let {
            val bmp = BitmapFactory.decodeFile(it)
            if (bmp != null) profileImage.setImageBitmap(bmp)
        }

        // Выбор изображения с использованием ActivityResultContracts
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

        // Сохранение данных
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
                session.savePassword(password) // сохраняем новый пароль
            }

            // Сохраняем имя независимо от пароля
            session.saveUserName(name)
            showToast("Данные сохранены")
        }

        // Сменить аккаунт
        btnLogout.setOnClickListener {
            // Создаём диалог подтверждения
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Сменить аккаунт")
            builder.setMessage("Вы точно хотите сменить аккаунт?")

            // Кнопка Да
            builder.setPositiveButton("Да") { dialog, _ ->
                session.clear()  // Сбрасываем текущий вход
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                showToast("Вы вышли из аккаунта")
                dialog.dismiss()
            }

            // Кнопка Отмена
            builder.setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
        }

        // Удалить аккаунт с подтверждением
        btnDelete.setOnClickListener { deleteAccount() }
    }

    private fun logout() {
        session.clear()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        showToast("Вы вышли из аккаунта")
    }

    private fun deleteAccount() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Удалить аккаунт")
        builder.setMessage("Вы точно хотите удалить аккаунт?")
        builder.setPositiveButton("Да") { dialog, _ ->
            session.clearFull()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
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

    private fun setupBottomNavigation() {
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, HomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    true
                }
                R.id.nav_favorites -> {
                    val intent = Intent(this, FavoritesActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    true
                }
                R.id.nav_add -> {
                    val intent = Intent(this, MyPostsActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    true
                }
                R.id.nav_chat -> {
                    val intent = Intent(this, ChatActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    true
                }
                R.id.nav_profile -> true
                else -> false
            }
        }
        bottomNavigation.post { bottomNavigation.selectedItemId = R.id.nav_profile }
    }
    override fun onResume() {
        super.onResume()
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.selectedItemId = R.id.nav_profile
    }
}