package com.rateflip.app.data.repository

import com.rateflip.app.data.api.RateFlipApi
import com.rateflip.app.data.model.ExchangeRateData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 汇率数据仓库（带内存缓存）
 */
@Singleton
class ExchangeRepository @Inject constructor(
    private val api: RateFlipApi
) {
    // 内存缓存：key = base currency code
    private data class CacheEntry(
        val data: ExchangeRateData,
        val timestamp: Long = System.currentTimeMillis()
    )
    
    private var cachedRates: CacheEntry? = null
    
    // 缓存有效期：10分钟
    private val cacheValidityMs = 10 * 60 * 1000L
    
    /**
     * 检查缓存是否有效
     */
    private fun isCacheValid(baseCurrency: String): Boolean {
        val cached = cachedRates ?: return false
        val age = System.currentTimeMillis() - cached.timestamp
        return cached.data.base == baseCurrency && age < cacheValidityMs
    }
    
    /**
     * 获取汇率（优先使用缓存，缓存有效则不发请求）
     */
    suspend fun getRates(baseCurrency: String = "USD"): Result<ExchangeRateData> {
        return withContext(Dispatchers.IO) {
            // 缓存有效则直接返回
            if (isCacheValid(baseCurrency)) {
                return@withContext Result.success(cachedRates!!.data)
            }
            
            try {
                val response = api.getRates(baseCurrency)
                if (response.success && response.data != null) {
                    cachedRates = CacheEntry(response.data)
                    Result.success(response.data)
                } else {
                    // 接口失败但有缓存，尝试用缓存
                    if (cachedRates != null && cachedRates!!.data.base == baseCurrency) {
                        Result.success(cachedRates!!.data)
                    } else {
                        Result.failure(Exception(response.error ?: "获取汇率失败"))
                    }
                }
            } catch (e: Exception) {
                // 网络错误但有缓存，尝试用缓存
                if (cachedRates != null && cachedRates!!.data.base == baseCurrency) {
                    Result.success(cachedRates!!.data)
                } else {
                    Result.failure(e)
                }
            }
        }
    }
    
    /**
     * 强制刷新汇率（忽略缓存）
     */
    suspend fun refreshRates(baseCurrency: String = "USD"): Result<ExchangeRateData> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.refreshRates(baseCurrency)
                if (response.success && response.data != null) {
                    cachedRates = CacheEntry(response.data)
                    Result.success(response.data)
                } else {
                    Result.failure(Exception(response.error ?: "刷新汇率失败"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * 检查缓存是否新鲜（用于判断是否需要刷新）
     */
    suspend fun isCacheFresh(baseCurrency: String): Boolean {
        return isCacheValid(baseCurrency)
    }

    /**
     * 清空缓存
     */
    fun clearCache() {
        cachedRates = null
    }
}
