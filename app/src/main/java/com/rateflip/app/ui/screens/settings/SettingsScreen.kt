package com.rateflip.app.ui.screens.settings

import android.app.Activity
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rateflip.app.R
import kotlinx.coroutines.launch

/**
 * 支持的语言列表
 */
data class Language(
    val code: String,
    val name: String,
    val nativeName: String
)

val supportedLanguages = listOf(
    Language("en", "English", "English"),
    Language("zh", "Chinese", "中文"),
    Language("hi", "Hindi", "हिन्दी"),
    Language("es", "Spanish", "Español"),
    Language("ko", "Korean", "한국어"),
    Language("ja", "Japanese", "日本語"),
    Language("ar", "Arabic", "العربية")
)

/**
 * 设置页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val currentTheme by viewModel.theme.collectAsState()
    val currentLanguage by viewModel.language.collectAsState()
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showPrivacyPolicyDialog by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val activity = context as? Activity

    // Pre-compute dynamic subtitles outside LazyColumn lambda
    val themeSubtitleText: String = when (currentTheme) {
        "light" -> stringResource(R.string.theme_light)
        "dark" -> stringResource(R.string.theme_dark)
        else -> stringResource(R.string.theme_system_default)
    }
    val languageSubtitleText: String = supportedLanguages.find { it.code == currentLanguage }?.nativeName ?: "English"

    // 语言选择对话框
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text(stringResource(R.string.select_language)) },
            text = {
                LazyColumn {
                    items(supportedLanguages.size) { index ->
                        val lang = supportedLanguages[index]
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setLanguage(lang.code)
                                    showLanguageDialog = false
                                    activity?.recreate()
                                }
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = currentLanguage == lang.code,
                                onClick = {
                                    viewModel.setLanguage(lang.code)
                                    showLanguageDialog = false
                                    activity?.recreate()
                                }
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = lang.name, style = MaterialTheme.typography.bodyLarge)
                                Text(
                                    text = lang.nativeName,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    // 主题选择对话框
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text(stringResource(R.string.select_theme)) },
            text = {
                Column {
                    ThemeOption(
                        titleRes = R.string.theme_system_default,
                        descRes = R.string.theme_system_desc,
                        isSelected = currentTheme == "system",
                        onSelect = {
                            viewModel.setTheme("system")
                            showThemeDialog = false
                        }
                    )
                    ThemeOption(
                        titleRes = R.string.theme_light,
                        descRes = R.string.theme_light_desc,
                        isSelected = currentTheme == "light",
                        onSelect = {
                            viewModel.setTheme("light")
                            showThemeDialog = false
                        }
                    )
                    ThemeOption(
                        titleRes = R.string.theme_dark,
                        descRes = R.string.theme_dark_desc,
                        isSelected = currentTheme == "dark",
                        onSelect = {
                            viewModel.setTheme("dark")
                            showThemeDialog = false
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }



    // 隐私政策对话框
    if (showPrivacyPolicyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyPolicyDialog = false },
            title = { Text(stringResource(R.string.privacy_policy)) },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = stringResource(R.string.privacy_policy_content),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyPolicyDialog = false }) {
                    Text(stringResource(R.string.close))
                }
            }
        )
    }

    // 服务条款对话框
    if (showTermsDialog) {
        AlertDialog(
            onDismissRequest = { showTermsDialog = false },
            title = { Text(stringResource(R.string.terms_of_service)) },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = stringResource(R.string.terms_of_service_content),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showTermsDialog = false }) {
                    Text(stringResource(R.string.close))
                }
            }
        )
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back)
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            // 主题设置
            item {
                SettingsSection(titleRes = R.string.appearance) {
                    SettingsItem(
                        icon = "\uD83C\uDF19",
                        iconBackground = Color(0xFFE3F2FD),
                        titleRes = R.string.settings_theme,
                        subtitle = themeSubtitleText,
                        onClick = { showThemeDialog = true }
                    )
                }
            }

            // 语言设置
            item {
                SettingsSection(titleRes = R.string.language) {
                    SettingsItem(
                        icon = "\uD83C\uDF10",
                        iconBackground = Color(0xFFE8F5E9),
                        titleRes = R.string.language,
                        subtitle = languageSubtitleText,
                        onClick = { showLanguageDialog = true }
                    )
                }
            }

            // 通用设置
            item {
                SettingsSection(titleRes = R.string.general) {
                    SettingsItem(
                        icon = "\uD83D\uDD04",
                        iconBackground = Color(0xFFE8F5E9),
                        titleRes = R.string.reset_to_default,
                        subtitle = stringResource(R.string.reset_settings_desc),
                        onClick = {
                            viewModel.resetToDefaults()
                            activity?.recreate()
                            scope.launch {
                                snackbarHostState.showSnackbar(context.getString(R.string.snackbar_settings_reset))
                            }
                        }
                    )
                }
            }

            // 法律信息
            item {
                SettingsSection(titleRes = R.string.legal) {
                    SettingsItem(
                        icon = "\uD83D\uDCC4",
                        iconBackground = Color(0xFFE3F2FD),
                        titleRes = R.string.privacy_policy,
                        subtitle = stringResource(R.string.privacy_policy_desc),
                        onClick = { showPrivacyPolicyDialog = true }
                    )
                    SettingsItem(
                        icon = "\uD83D\uDCC4",
                        iconBackground = Color(0xFFFFF8E1),
                        titleRes = R.string.terms_of_service,
                        subtitle = stringResource(R.string.terms_of_service_desc),
                        onClick = { showTermsDialog = true }
                    )
                }
            }

            // 关于
            item {
                SettingsSection(titleRes = R.string.about) {
                    SettingsItem(
                        icon = "\u2139\uFE0F",
                        iconBackground = Color(0xFFF5F5F5),
                        titleRes = R.string.about_rateflip,
                        subtitle = stringResource(R.string.version_format, "1.0.0"),
                        onClick = {
                            scope.launch {
                                snackbarHostState.currentSnackbarData?.dismiss()
                                snackbarHostState.showSnackbar(context.getString(R.string.snackbar_rateflip_version, "1.0.0"))
                            }
                        }
                    )
                    SettingsItem(
                        icon = "\uD83D\uDCE7",
                        iconBackground = Color(0xFFE8F5E9),
                        titleRes = R.string.contact_us,
                        subtitle = stringResource(R.string.contact_email),
                        onClick = {
                            scope.launch {
                                snackbarHostState.currentSnackbarData?.dismiss()
                                snackbarHostState.showSnackbar(context.getString(R.string.snackbar_contact, "support@rateflip.com"))
                            }
                        }
                    )
                }
            }

            // 广告设置
            item {
                SettingsSection(titleRes = R.string.ad_settings) {
                    SettingsItem(
                        icon = "\uD83D\uDCCA",
                        iconBackground = Color(0xFFF5F5F5),
                        titleRes = R.string.about_ads,
                        subtitle = stringResource(R.string.about_ads_desc),
                        onClick = {
                            scope.launch {
                                snackbarHostState.showSnackbar(context.getString(R.string.snackbar_ads_help))
                            }
                        }
                    )
                    SettingsItem(
                        icon = "\uD83D\uDD17",
                        iconBackground = Color(0xFFF5F5F5),
                        titleRes = R.string.ad_personalization,
                        subtitle = stringResource(R.string.ad_personalization_desc),
                        onClick = {
                            scope.launch {
                                snackbarHostState.showSnackbar(context.getString(R.string.snackbar_coming_soon))
                            }
                        }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

/**
 * 主题选项
 */
@Composable
private fun ThemeOption(
    @StringRes titleRes: Int,
    @StringRes descRes: Int,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onSelect
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = stringResource(titleRes), style = MaterialTheme.typography.bodyLarge)
            Text(
                text = stringResource(descRes),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

/**
 * 设置分区
 */
@Composable
private fun SettingsSection(
    @StringRes titleRes: Int,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = stringResource(titleRes),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 1.dp
        ) {
            Column(content = content)
        }
    }
}

/**
 * 设置项
 */
@Composable
private fun SettingsItem(
    icon: String,
    iconBackground: Color,
    @StringRes titleRes: Int,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 图标
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconBackground),
            contentAlignment = Alignment.Center
        ) {
            Text(text = icon, style = MaterialTheme.typography.titleMedium)
        }

        Spacer(modifier = Modifier.width(14.dp))

        // 文字
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(titleRes),
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        )
    }
}
