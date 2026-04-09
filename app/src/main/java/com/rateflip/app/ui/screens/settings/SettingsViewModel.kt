package com.rateflip.app.ui.screens.settings

import androidx.lifecycle.ViewModel
import com.rateflip.app.PreferencesManager
import com.rateflip.app.data.repository.ExchangeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * 设置页面 ViewModel
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val repository: ExchangeRepository
) : ViewModel() {

    private val _theme = MutableStateFlow(preferencesManager.theme)
    val theme: StateFlow<String> = _theme.asStateFlow()

    private val _language = MutableStateFlow(preferencesManager.language)
    val language: StateFlow<String> = _language.asStateFlow()

    fun setTheme(theme: String) {
        preferencesManager.theme = theme
        _theme.value = theme
    }

    fun setLanguage(language: String) {
        preferencesManager.language = language
        _language.value = language
    }

    fun resetToDefaults() {
        preferencesManager.resetAll()
        _theme.value = "system"
        _language.value = "en"
    }
}
