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
        Currency("CNY", "Chinese Yuan", "¥", "🇨🇳"),
        Currency("EUR", "Euro", "€", "🇪🇺"),
        Currency("GBP", "British Pound", "£", "🇬🇧"),
        Currency("JPY", "Japanese Yen", "¥", "🇯🇵"),
        Currency("CAD", "Canadian Dollar", "$", "🇨🇦"),
        Currency("AUD", "Australian Dollar", "$", "🇦🇺"),
        Currency("CHF", "Swiss Franc", "Fr", "🇨🇭"),
        Currency("HKD", "Hong Kong Dollar", "$", "🇭🇰"),
        Currency("KRW", "South Korean Won", "₩", "🇰🇷"),
        Currency("MXN", "Mexican Peso", "$", "🇲🇽"),
        Currency("NZD", "New Zealand Dollar", "$", "🇳🇿"),
        Currency("SGD", "Singapore Dollar", "$", "🇸🇬"),
        Currency("INR", "Indian Rupee", "₹", "🇮🇳")
    )
    
    /**
     * 根据代码获取货币
     */
    fun getByCode(code: String): Currency {
        return 常用货币.find { it.code == code } ?: 常用货币.first()
    }
}
