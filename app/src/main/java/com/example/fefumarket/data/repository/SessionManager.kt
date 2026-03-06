package com.example.fefumarket.data.repository

import android.content.Context

// Менеджер сессии пользователя: хранение логина, пароля, имени, пути к изображению и токена API.
// Обеспечивает сохранение, получение и очистку данных сессии в SharedPreferences
class SessionManager(context: Context) {

    private val prefs = context.getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE)

    // Сохраняем email пользователя при входе
    fun saveLogin(email: String) {
        prefs.edit().putString("USER_EMAIL", email).apply()
    }

    // Получаем сохранённый email
    fun getLogin(): String? {
        return prefs.getString("USER_EMAIL", null)
    }

    // Сохраняем имя пользователя
    fun saveUserName(name: String) {
        prefs.edit().putString("USER_NAME", name).apply()
    }

    // Получаем имя пользователя
    fun getUserName(): String? {
        return prefs.getString("USER_NAME", "")
    }

    // Сохраняем пароль пользователя
    fun savePassword(pass: String) {
        prefs.edit().putString("USER_PASSWORD", pass).apply()
    }

    // Получаем пароль
    fun getPassword(): String? {
        return prefs.getString("USER_PASSWORD", "")
    }

    // Сохраняем путь к изображению профиля
    fun saveImagePath(path: String) {
        prefs.edit().putString("USER_IMAGE_PATH", path).apply()
    }

    // Получаем путь к изображению профиля
    fun getImagePath(): String? {
        return prefs.getString("USER_IMAGE_PATH", null)
    }

    // Выход: сохраняем данные, но сбрасываем email
    fun clear() {
        prefs.edit()
            .remove("USER_EMAIL")
            .remove("AUTH_TOKEN")
            .apply()
    }

    // Полное удаление всех данных профиля
    fun clearFull() {
        prefs.edit().clear().apply()
    }

    /** ===== TOKEN (для API) ===== */

    // Сохраняем токен для авторизации API-запросов
    fun saveToken(token: String) {
        prefs.edit().putString("AUTH_TOKEN", token).apply()
    }

    // Получаем токен для авторизации API-запросов
    fun getToken(): String? {
        return prefs.getString("AUTH_TOKEN", null)
    }

    // Удаляем токен (например, при выходе)
    fun clearToken() {
        prefs.edit().remove("AUTH_TOKEN").apply()
    }

}
