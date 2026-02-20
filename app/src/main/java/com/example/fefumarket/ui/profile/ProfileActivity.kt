package com.example.fefumarket.ui.profile

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.fefumarket.R
import com.example.fefumarket.base.BaseActivity
import com.example.fefumarket.data.models.api.UserUpdate
import com.example.fefumarket.data.repository.SessionManager
import com.example.fefumarket.data.repository.UserRepository
import com.example.fefumarket.network.RetrofitClient
import com.example.fefumarket.ui.auth.LoginActivity
import kotlinx.coroutines.launch

class ProfileActivity : BaseActivity() {

    private lateinit var session: SessionManager
    private lateinit var userRepository: UserRepository

    private lateinit var profileImage: ImageView
    private lateinit var nameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var repeatPasswordInput: EditText
    private lateinit var btnSave: TextView
    private lateinit var btnLogout: TextView
    private lateinit var btnDelete: TextView

    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        session = SessionManager(this)
        val api = RetrofitClient.create(this)
        userRepository = UserRepository(api, session)

        // 🔹 Инициализация полей
        profileImage = findViewById(R.id.profileImage)
        nameInput = findViewById(R.id.nameInput)
        passwordInput = findViewById(R.id.passwordInput)
        repeatPasswordInput = findViewById(R.id.repeatPasswordInput)
        btnSave = findViewById(R.id.saveButton)
        btnLogout = findViewById(R.id.logoutButton)
        btnDelete = findViewById(R.id.deleteButton)

        // 🔹 Загрузка данных пользователя с сервера
        loadUserProfile()

        // 🔹 Выбор нового изображения профиля
        val pickImageLauncher = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->
            uri?.let {
                selectedImageUri = it
                Glide.with(this)
                    .load(it)
                    .circleCrop()
                    .into(profileImage)
            }
        }

        profileImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        // 🔹 Сохранение имени и пароля пользователя через API
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
                // Пароль обновляется через отдельный эндпоинт (если есть) или остается локально
                session.savePassword(password)
            }

            lifecycleScope.launch {
                try {
                    // Обновляем имя через API
                    val userUpdate = UserUpdate(name = name)
                    val updatedUser = userRepository.updateMe(userUpdate)
                    
                    // Сохраняем локально для совместимости
                    session.saveUserName(updatedUser.name)
                    updatedUser.avatar_url?.let { session.saveImagePath(it) }
                    
                    showToast("Данные сохранены")
                } catch (e: Exception) {
                    showToast("Ошибка при сохранении: ${e.message}")
                }
            }
        }

        // 🔹 Смена аккаунта через API
        btnLogout.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Сменить аккаунт")
            builder.setMessage("Вы точно хотите сменить аккаунт?")
            builder.setPositiveButton("Да") { dialog, _ ->
                lifecycleScope.launch {
                    try {
                        userRepository.logout()
                        val intent = Intent(this@ProfileActivity, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        showToast("Вы вышли из аккаунта")
                    } catch (e: Exception) {
                        // Даже если API вызов не удался, очищаем локально
                        session.clear()
                        session.clearToken()
                        val intent = Intent(this@ProfileActivity, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        showToast("Вы вышли из аккаунта")
                    }
                    dialog.dismiss()
                }
            }
            builder.setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()
        }

        // 🔹 Удаление аккаунта через API
        btnDelete.setOnClickListener { deleteAccount() }
    }

    // 🔹 Загрузка профиля пользователя с сервера
    private fun loadUserProfile() {
        lifecycleScope.launch {
            try {
                val user = userRepository.getMe()
                nameInput.setText(user.name)
                
                // Загружаем аватар с сервера, если есть
                user.avatar_url?.let { avatarUrl ->
                    Glide.with(this@ProfileActivity)
                        .load(avatarUrl)
                        .circleCrop()
                        .placeholder(R.drawable.ic_user)
                        .into(profileImage)
                    session.saveImagePath(avatarUrl)
                } ?: run {
                    // Если аватара нет на сервере, пробуем загрузить локальный
                    session.getImagePath()?.let {
                        val bmp = BitmapFactory.decodeFile(it)
                        if (bmp != null) profileImage.setImageBitmap(bmp)
                    }
                }
                
                // Сохраняем имя локально для совместимости
                session.saveUserName(user.name)
            } catch (e: Exception) {
                // Если ошибка, используем локальные данные
                nameInput.setText(session.getUserName())
                session.getImagePath()?.let {
                    val bmp = BitmapFactory.decodeFile(it)
                    if (bmp != null) profileImage.setImageBitmap(bmp)
                }
            }
        }
    }

    // 🔹 Метод удаления аккаунта с подтверждением через API
    private fun deleteAccount() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Удалить аккаунт")
        builder.setMessage("Вы точно хотите удалить аккаунт? Это действие нельзя отменить.")
        builder.setPositiveButton("Да") { dialog, _ ->
            lifecycleScope.launch {
                try {
                    userRepository.deleteMe()
                    session.clearFull()
                    val intent = Intent(this@ProfileActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    showToast("Аккаунт удалён")
                } catch (e: Exception) {
                    showToast("Ошибка при удалении: ${e.message}")
                }
                dialog.dismiss()
            }
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