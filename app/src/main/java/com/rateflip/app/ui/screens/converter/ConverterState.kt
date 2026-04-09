package com.rateflip.app.ui.screens.converter

import com.rateflip.app.data.model.Currency
import com.rateflip.app.data.model.CurrencyList

/**
 * 换算器 UI 状态
 */
data class ConverterState(
    val fromCurrency: Currency = CurrencyList.getByCode("USD")!!,
    val toCurrency: Currency = CurrencyList.getByCode("CAD")!!,
    val fromAmount: String = "",
    val toAmount: String = "",
    val exchangeRate: Double = 0.0,
    val lastUpdated: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isOffline: Boolean = false,
    val isRefreshing: Boolean = false,
    val toastMessage: String? = null,
    val isAlreadyLatest: Boolean = false
)

/**
 * 换算器事件
 */
sealed class ConverterEvent {
    object RefreshRates : ConverterEvent()
    object SwapCurrencies : ConverterEvent()
    data class SelectFromCurrency(val currency: Currency) : ConverterEvent()
    data class SelectToCurrency(val currency: Currency) : ConverterEvent()
    data class UpdateAmount(val amount: String) : ConverterEvent()
    object DismissToast : ConverterEvent()
}
