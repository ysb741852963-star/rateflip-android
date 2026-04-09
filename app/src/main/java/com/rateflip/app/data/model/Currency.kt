package com.rateflip.app.data.model

/**
 * 货币数据模型
 */
data class Currency(
    val code: String,       // 货币代码，如 "USD"
    val name: String,       // 货币名称，如 "US Dollar"
    val symbol: String,     // 货币符号，如 "$"
    val flag: String         // 国旗 emoji
)

/**
 * 常用货币列表
 */
object CurrencyList {
    val 常用货币 = listOf(
        Currency("USD", "US Dollar", "$", "🇺🇸"),
        Currency("CAD", "Canadian Dollar", "$", "🇨🇦"),
        Currency("MXN", "Mexican Peso", "$", "🇲🇽"),
        Currency("EUR", "Euro", "€", "🇪🇺"),
        Currency("GBP", "British Pound", "£", "🇬🇧"),
        Currency("CNY", "Chinese Yuan", "¥", "🇨🇳"),
        Currency("JPY", "Japanese Yen", "¥", "🇯🇵"),
        Currency("KRW", "South Korean Won", "₩", "🇰🇷"),
        Currency("HKD", "Hong Kong Dollar", "$", "🇭🇰"),
        Currency("IDR", "Indonesian Rupiah", "Rp", "🇮🇩"),
        Currency("AUD", "Australian Dollar", "$", "🇦🇺"),
        Currency("NZD", "New Zealand Dollar", "$", "🇳🇿"),
        Currency("CHF", "Swiss Franc", "Fr", "🇨🇭"),
        Currency("SGD", "Singapore Dollar", "$", "🇸🇬"),
        Currency("THB", "Thai Baht", "฿", "🇹🇭"),
        Currency("MYR", "Malaysian Ringgit", "RM", "🇲🇾"),
        Currency("PHP", "Philippine Peso", "₱", "🇵🇭"),
        Currency("VND", "Vietnamese Dong", "₫", "🇻🇳"),
        Currency("INR", "Indian Rupee", "₹", "🇮🇳"),
        Currency("BRL", "Brazilian Real", "R$", "🇧🇷"),
        Currency("RUB", "Russian Ruble", "₽", "🇷🇺"),
        Currency("ZAR", "South African Rand", "R", "🇿🇦"),
        Currency("SEK", "Swedish Krona", "kr", "🇸🇪"),
        Currency("NOK", "Norwegian Krone", "kr", "🇳🇴"),
        Currency("DKK", "Danish Krone", "kr", "🇩🇰"),
        Currency("PLN", "Polish Zloty", "zł", "🇵🇱"),
        Currency("TRY", "Turkish Lira", "₺", "🇹🇷"),
        Currency("AED", "UAE Dirham", "د.إ", "🇦🇪"),
        Currency("SAR", "Saudi Riyal", "﷼", "🇸🇦"),
        Currency("TWD", "Taiwan Dollar", "NT$", "🇹🇼")
    )
    
    /**
     * 根据代码获取货币
     */
    fun getByCode(code: String): Currency {
        return 常用货币.find { it.code == code } ?: 常用货币.first()
    }
}
