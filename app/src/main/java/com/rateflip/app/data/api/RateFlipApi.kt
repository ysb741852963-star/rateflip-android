package com.rateflip.app.data.api

import com.rateflip.app.data.model.ExchangeRateResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * RateFlip API 接口
 */
interface RateFlipApi {
    
    /**
     * 获取汇率
     * @param base 基准货币代码，默认 USD
     */
    @GET("api/v1/rates")
    suspend fun getRates(
        @Query("base") base: String = "USD"
    ): ExchangeRateResponse
    
    /**
     * 强制刷新汇率
     * @param base 基准货币代码，默认 USD
     */
    @GET("api/v1/rates/refresh")
    suspend fun refreshRates(
        @Query("base") base: String = "USD"
    ): ExchangeRateResponse
    
    /**
     * 检查缓存状态
     * @param base 基准货币代码
     */
    @GET("api/v1/rates/status")
    suspend fun getCacheStatus(
        @Query("base") base: String = "USD"
    ): ExchangeRateResponse
}
