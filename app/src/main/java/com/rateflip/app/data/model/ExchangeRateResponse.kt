package com.rateflip.app.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 汇率 API 响应模型
 */
@Serializable
data class ExchangeRateResponse(
    @SerialName("success")
    val success: Boolean,
    
    @SerialName("data")
    val data: ExchangeRateData? = null,
    
    @SerialName("error")
    val error: String? = null
)

/**
 * 汇率数据
 */
@Serializable
data class ExchangeRateData(
    @SerialName("base")
    val base: String,
    
    @SerialName("timestamp")
    val timestamp: Long,
    
    @SerialName("rates")
    val rates: Map<String, Double>,
    
    @SerialName("cached")
    val cached: Boolean = false,
    
    @SerialName("cacheAgeSeconds")
    val cacheAgeSeconds: Int? = null,
    
    @SerialName("stale")
    val stale: Boolean = false
)
