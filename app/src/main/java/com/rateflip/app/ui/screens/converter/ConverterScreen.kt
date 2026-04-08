package com.rateflip.app.ui.screens.converter

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rateflip.app.R
import com.rateflip.app.data.model.Currency
import com.rateflip.app.data.model.CurrencyList
import com.rateflip.app.ui.theme.NumberTypography

/**
 * 计算颜色的亮度（0.0 - 1.0）
 * 根据 WCAG 标准：L = 0.299*R + 0.587*G + 0.114*B
 */
private fun Color.luminance(): Float {
    val red = this.red
    val green = this.green
    val blue = this.blue
    return 0.299f * red + 0.587f * green + 0.114f * blue
}

/**
 * 换算器主页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConverterScreen(
    onNavigateToSettings: () -> Unit,
    viewModel: ConverterViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showFromCurrencyPicker by remember { mutableStateOf(false) }
    var showToCurrencyPicker by remember { mutableStateOf(false) }

    // Toast 显示
    LaunchedEffect(state.toastMessage, state.isAlreadyLatest) {
        if (state.isAlreadyLatest) {
            kotlinx.coroutines.delay(2000)
            viewModel.onEvent(ConverterEvent.DismissToast)
        } else if (state.toastMessage != null) {
            kotlinx.coroutines.delay(2000)
            viewModel.onEvent(ConverterEvent.DismissToast)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
                .imePadding()
                .statusBarsPadding()
        ) {
            // 顶部栏
            TopBar(
                onRefresh = { viewModel.onEvent(ConverterEvent.RefreshRates) },
                onSettings = onNavigateToSettings,
                isRefreshing = state.isRefreshing
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 更新时间
            Text(
                text = stringResource(R.string.last_updated_format, state.lastUpdated),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 源货币卡片
            CurrencyInputCard(
                currency = state.fromCurrency,
                amount = state.fromAmount,
                onAmountChange = { viewModel.onEvent(ConverterEvent.UpdateAmount(it)) },
                onCurrencyClick = { showFromCurrencyPicker = true },
                isEditable = true
            )

            // 交换按钮
            SwapButton(
                onClick = { viewModel.onEvent(ConverterEvent.SwapCurrencies) }
            )

            // 目标货币卡片
            CurrencyInputCard(
                currency = state.toCurrency,
                amount = state.toAmount,
                onAmountChange = {},
                onCurrencyClick = { showToCurrencyPicker = true },
                isEditable = false,
                exchangeRate = if (state.exchangeRate > 0)
                    stringResource(R.string.exchange_rate_format, state.fromCurrency.code, state.exchangeRate, state.toCurrency.code)
                    else "",
                lastUpdated = if (state.lastUpdated.isNotEmpty()) stringResource(R.string.updated_format, state.lastUpdated) else ""
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 快捷换算
            Text(
                text = stringResource(R.string.quick_convert),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            QuickConvertChips(
                onChipClick = { from, to ->
                    viewModel.onEvent(ConverterEvent.SelectFromCurrency(from))
                    viewModel.onEvent(ConverterEvent.SelectToCurrency(to))
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 更多货币
            Text(
                text = stringResource(R.string.more_currencies),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            MoreCurrenciesGrid(
                onCurrencyClick = { currency ->
                    viewModel.onEvent(ConverterEvent.SelectToCurrency(currency))
                }
            )

            // 底部广告位占位
            Spacer(modifier = Modifier.weight(1f))

            BannerAdPlaceholder()
        }

        // Toast
        AnimatedVisibility(
            visible = state.toastMessage != null || state.isAlreadyLatest,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 80.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = if (state.isAlreadyLatest) MaterialTheme.colorScheme.primary
                    else Color(0xFF00C853)
            ) {
                Text(
                    text = if (state.isAlreadyLatest) stringResource(R.string.toast_already_latest)
                        else stringResource(R.string.toast_rates_updated, state.toastMessage ?: ""),
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }

    // 源货币选择器
    if (showFromCurrencyPicker) {
        CurrencyPickerSheet(
            currencies = CurrencyList.常用货币,
            selectedCurrency = state.fromCurrency,
            onCurrencySelected = {
                viewModel.onEvent(ConverterEvent.SelectFromCurrency(it))
                showFromCurrencyPicker = false
            },
            onDismiss = { showFromCurrencyPicker = false }
        )
    }

    // 目标货币选择器
    if (showToCurrencyPicker) {
        CurrencyPickerSheet(
            currencies = CurrencyList.常用货币,
            selectedCurrency = state.toCurrency,
            onCurrencySelected = {
                viewModel.onEvent(ConverterEvent.SelectToCurrency(it))
                showToCurrencyPicker = false
            },
            onDismiss = { showToCurrencyPicker = false }
        )
    }
}

/**
 * 顶部栏
 */
@Composable
private fun TopBar(
    onRefresh: () -> Unit,
    onSettings: () -> Unit,
    isRefreshing: Boolean
) {
    val rotation by animateFloatAsState(
        targetValue = if (isRefreshing) 360f else 0f,
        label = "refresh_rotation"
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.app_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Refresh button - 无背景，图标颜色与Settings一致，融入Header
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = stringResource(R.string.cd_refresh),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier
                    .size(36.dp)
                    .rotate(rotation)
                    .clickable(enabled = !isRefreshing, onClick = onRefresh)
                    .padding(6.dp)
            )

            // Settings icon - 无背景，低调融入
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = stringResource(R.string.cd_settings),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier
                    .size(36.dp)
                    .clickable(onClick = onSettings)
                    .padding(6.dp)
            )
        }
    }
}

/**
 * 货币输入卡片
 */
@Composable
private fun CurrencyInputCard(
    currency: Currency,
    amount: String,
    onAmountChange: (String) -> Unit,
    onCurrencyClick: () -> Unit,
    isEditable: Boolean,
    exchangeRate: String = "",
    lastUpdated: String = ""
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isEditable) MaterialTheme.colorScheme.surface
                else MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 货币选择器
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onCurrencyClick)
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = currency.flag, style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = currency.code,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "▼",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 金额输入/显示
            if (isEditable) {
                OutlinedTextField(
                    value = amount,
                    onValueChange = onAmountChange,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.headlineLarge.copy(
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.Transparent
                    )
                )
            } else {
                Text(
                    text = "${currency.symbol} $amount",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 货币名称
            Text(
                text = currency.name,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            // 汇率信息
            if (exchangeRate.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = exchangeRate,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            // 更新时间（非编辑模式卡片）
            if (lastUpdated.isNotEmpty()) {
                Text(
                    text = lastUpdated,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}

/**
 * 交换按钮
 */
@Composable
private fun SwapButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        FilledIconButton(
            onClick = onClick,
            modifier = Modifier.size(56.dp),
            shape = CircleShape,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                imageVector = Icons.Default.SwapVert,
                contentDescription = stringResource(R.string.cd_swap),
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

/**
 * 快捷换算芯片
 */
@Composable
private fun QuickConvertChips(
    onChipClick: (Currency, Currency) -> Unit
) {
    val quickPairs = listOf(
        Pair("USD", "CAD"),
        Pair("EUR", "USD"),
        Pair("GBP", "USD"),
        Pair("JPY", "USD"),
        Pair("CAD", "USD")
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(quickPairs) { (fromCode, toCode) ->
            val from = CurrencyList.getByCode(fromCode)
            val to = CurrencyList.getByCode(toCode)

            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onChipClick(from, to) },
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = fromCode,
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(
                        text = "→",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = toCode,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

/**
 * 更多货币网格
 */
@Composable
private fun MoreCurrenciesGrid(
    onCurrencyClick: (Currency) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(CurrencyList.常用货币.filter { it.code != "USD" && it.code != "CNY" }) { currency ->
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { onCurrencyClick(currency) },
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = currency.code,
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = currency.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

/**
 * 底部广告位占位
 */
@Composable
private fun BannerAdPlaceholder() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.ad_placeholder),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}

/**
 * 货币选择底部弹窗
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CurrencyPickerSheet(
    currencies: List<Currency>,
    @Suppress("UNUSED_PARAMETER") selectedCurrency: Currency,
    onCurrencySelected: (Currency) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.select_currency),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            currencies.forEach { currency ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCurrencySelected(currency) }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = currency.flag, style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = currency.code,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = currency.name,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                if (currency != currencies.last()) {
                    Divider()
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
