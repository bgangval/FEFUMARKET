package com.example.fefumarket

import android.content.Context

class SessionManager(context: Context) {

    private val prefs = context.getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE)

    /** Сохраняем email при входе */
    fun saveLogin(email: String) {
        prefs.edit().putString("USER_EMAIL", email).apply()
    }

    fun getLogin(): String? {
        return prefs.getString("USER_EMAIL", null)
    }

    /** Имя пользователя */
    fun saveUserName(name: String) {
        prefs.edit().putString("USER_NAME", name).apply()
    }

    fun getUserName(): String? {
        return prefs.getString("USER_NAME", "")
    }

    /** Пароль */
    fun savePassword(pass: String) {
        prefs.edit().putString("USER_PASSWORD", pass).apply()
    }

    fun getPassword(): String? {
        return prefs.getString("USER_PASSWORD", "")
    }

    /** Путь к изображению профиля */
    fun saveImagePath(path: String) {
        prefs.edit().putString("USER_IMAGE_PATH", path).apply()
    }

    fun getImagePath(): String? {
        return prefs.getString("USER_IMAGE_PATH", null)
    }

    /** Выход: сохраняем данные, но сбрасываем email */
    fun clear() {
        prefs.edit().remove("USER_EMAIL").apply()
    }

    /** Полное удаление профиля */
    fun clearFull() {
        prefs.edit().clear().apply()
    }
}