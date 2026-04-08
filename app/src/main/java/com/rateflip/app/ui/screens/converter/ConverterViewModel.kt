package com.rateflip.app.ui.screens.converter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rateflip.app.data.model.Currency
import com.rateflip.app.data.model.CurrencyList
import com.rateflip.app.data.repository.ExchangeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * 换算器 ViewModel
 */
@HiltViewModel
class ConverterViewModel @Inject constructor(
    private val repository: ExchangeRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ConverterState())
    val state: StateFlow<ConverterState> = _state.asStateFlow()

    // 换算次数计数（用于插页广告）
    private var conversionCount = 0

    init {
        loadRates()
    }

    /**
     * 处理事件
     */
    fun onEvent(event: ConverterEvent) {
        when (event) {
            is ConverterEvent.RefreshRates -> refreshRates()
            is ConverterEvent.SwapCurrencies -> swapCurrencies()
            is ConverterEvent.SelectFromCurrency -> selectFromCurrency(event.currency)
            is ConverterEvent.SelectToCurrency -> selectToCurrency(event.currency)
            is ConverterEvent.UpdateAmount -> updateAmount(event.amount)
            is ConverterEvent.DismissToast -> dismissToast()
        }
    }

    /**
     * 加载汇率
     */
    private fun loadRates() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val fromCode = _state.value.fromCurrency.code
            val toCode = _state.value.toCurrency.code

            repository.getRates(fromCode)
                .onSuccess { data ->
                    val rate = data.rates[toCode] ?: getFallbackRate(toCode)
                    val updatedTime = formatTimestamp(data.timestamp)

                    _state.update { state ->
                        state.copy(
                            isLoading = false,
                            exchangeRate = rate,
                            lastUpdated = updatedTime,
                            // 用最新汇率 + 当前输入金额重新计算
                            toAmount = calculateResult(state.fromAmount, rate),
                            isOffline = data.stale,
                            error = null
                        )
                    }
                }
                .onFailure { e ->
                    // 网络错误时使用 fallback 汇率，避免转换结果为 0
                    val fallbackRate = getFallbackRate(toCode)
                    _state.update { state ->
                        state.copy(
                            isLoading = false,
                            exchangeRate = fallbackRate,
                            toAmount = calculateResult(state.fromAmount, fallbackRate),
                            error = "Network error: ${e.message}. Using cached rate."
                        )
                    }
                }
        }
    }

    /**
     * 获取备用汇率（当网络不可用时）
     */
    private fun getFallbackRate(currencyCode: String): Double {
        // 常用货币的近似汇率（基于 USD）
        return fallbackRates[currencyCode] ?: 1.0
    }

    private val fallbackRates = mapOf(
        "CNY" to 7.0,
        "EUR" to 0.92,
        "GBP" to 0.79,
        "JPY" to 150.0,
        "CAD" to 1.36,
        "AUD" to 1.53,
        "CHF" to 0.88,
        "HKD" to 7.82,
        "KRW" to 1330.0,
        "MXN" to 17.0,
        "NZD" to 1.63,
        "SGD" to 1.34,
        "INR" to 83.0,
        "USD" to 1.0
    )

    /**
     * 刷新汇率
     */
    private fun refreshRates() {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true, isAlreadyLatest = false) }

            // 先检查缓存是否新鲜
            val isFresh = repository.isCacheFresh(_state.value.fromCurrency.code)

            if (isFresh) {
                _state.update { it.copy(isRefreshing = false, isAlreadyLatest = true) }
                return@launch
            }

            val fromCode = _state.value.fromCurrency.code
            val toCode = _state.value.toCurrency.code

            repository.refreshRates(fromCode)
                .onSuccess { data ->
                    val rate = data.rates[toCode] ?: getFallbackRate(toCode)
                    val updatedTime = formatTimestamp(data.timestamp)

                    _state.update { state ->
                        state.copy(
                            isRefreshing = false,
                            exchangeRate = rate,
                            lastUpdated = updatedTime,
                            toastMessage = "Rates updated!"
                        )
                    }
                }
                .onFailure { e ->
                    _state.update {
                        it.copy(
                            isRefreshing = false,
                            error = "Failed to refresh: ${e.message}"
                        )
                    }
                }
        }
    }

    /**
     * 交换货币
     */
    private fun swapCurrencies() {
        _state.update { state ->
            // 保存原始输入金额
            val originalFromAmount = state.fromAmount
            // 只交换货币，不交换金额
            state.copy(
                fromCurrency = state.toCurrency,
                toCurrency = state.fromCurrency,
                // 保持 fromAmount 不变，重新计算 toAmount
                fromAmount = originalFromAmount,
                toAmount = ""
            )
        }
        // 重新加载汇率并计算
        loadRates()
    }

    /**
     * 选择源货币
     */
    private fun selectFromCurrency(currency: Currency) {
        _state.update { it.copy(fromCurrency = currency) }
        loadRates()
    }

    /**
     * 选择目标货币
     */
    private fun selectToCurrency(currency: Currency) {
        _state.update { it.copy(toCurrency = currency) }
        loadRates()
        incrementConversionCount()
    }

    /**
     * 更新金额
     */
    private fun updateAmount(amount: String) {
        _state.update { state ->
            state.copy(
                fromAmount = amount,
                toAmount = calculateResult(amount, state.exchangeRate)
            )
        }
        incrementConversionCount()
    }

    /**
     * 计算换算结果
     */
    private fun calculateResult(amount: String, rate: Double): String {
        val value = amount.toDoubleOrNull() ?: return ""
        val result = value * rate
        return String.format(Locale.US, "%.2f", result)
    }

    /**
     * 增加换算次数（用于插页广告）
     */
    private fun incrementConversionCount() {
        conversionCount++
        if (conversionCount >= 3) {
            // TODO: 触发插页广告
            conversionCount = 0
        }
    }

    /**
     * 格式化时间戳
     */
    private fun formatTimestamp(timestamp: Long): String {
        val sdf = SimpleDateFormat("MMM dd, HH:mm", Locale.US)
        return sdf.format(Date(timestamp * 1000))
    }

    /**
     * 关闭 Toast
     */
    private fun dismissToast() {
        _state.update { it.copy(toastMessage = null, isAlreadyLatest = false) }
    }
}
