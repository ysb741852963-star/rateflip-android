package com.rateflip.app

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * SharedPreferences 管理器
 */
@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME, Context.MODE_PRIVATE
    )

    private val _themeFlow = MutableStateFlow(prefs.getString(KEY_THEME, "system") ?: "system")
    val themeFlow: StateFlow<String> = _themeFlow.asStateFlow()

    private val _languageFlow = MutableStateFlow(prefs.getString(KEY_LANGUAGE, "en") ?: "en")
    val languageFlow: StateFlow<String> = _languageFlow.asStateFlow()

    /**
     * 主题设置
     * "system" | "light" | "dark"
     */
    var theme: String
        get() = prefs.getString(KEY_THEME, "system") ?: "system"
        set(value) {
            prefs.edit().putString(KEY_THEME, value).apply()
            _themeFlow.value = value
        }

    /**
     * 语言设置
     */
    var language: String
        get() = prefs.getString(KEY_LANGUAGE, "en") ?: "en"
        set(value) {
            prefs.edit().putString(KEY_LANGUAGE, value).apply()
            _languageFlow.value = value
        }

    /**
     * 重置所有设置为默认值
     */
    fun resetAll() {
        prefs.edit().clear().apply()
        _themeFlow.value = "system"
        _languageFlow.value = "en"
    }

    companion object {
        private const val PREFS_NAME = "rateflip_prefs"
        private const val KEY_THEME = "theme"
        private const val KEY_LANGUAGE = "language"
    }
}
