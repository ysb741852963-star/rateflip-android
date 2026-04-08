# RateFlip Android App

汇率换算应用 - 原生 Android 版本

## 技术栈

- **语言**：Kotlin
- **UI**：Jetpack Compose
- **架构**：MVVM + Clean Architecture
- **依赖注入**：Hilt
- **网络**：Retrofit + OkHttp
- **异步**：Kotlin Coroutines + Flow
- **最低 SDK**：API 26 (Android 8.0)
- **目标 SDK**：API 34 (Android 14)

## 项目结构

```
app/src/main/java/com/rateflip/app/
├── data/
│   ├── api/              # Retrofit API 接口
│   ├── model/            # 数据模型
│   └── repository/       # 数据仓库
├── di/                   # Hilt 依赖注入模块
├── ui/
│   ├── screens/
│   │   ├── converter/   # 换算器页面
│   │   └── settings/    # 设置页面
│   └── theme/           # 主题（颜色、字体）
├── util/                 # 工具类
├── MainActivity.kt       # 主 Activity
└── RateFlipApplication.kt # Application 类
```

## 开发环境

1. **Android Studio Hedgehog (2023.1.1)** 或更高版本
2. **JDK 17**
3. **Android SDK 34**

## 导入项目

1. 打开 Android Studio
2. 选择 `File` → `Open`
3. 选择 `projects/exchange-rate-app/android` 文件夹
4. 等待 Gradle 同步完成

## 配置后端地址

在 `di/NetworkModule.kt` 中配置后端 API 地址：

```kotlin
// 开发环境（模拟器访问本机后端）
private const val BASE_URL = "http://10.0.2.2:8080/"

// 正式环境
private const val BASE_URL = "https://your-backend-domain.com/"
```

## 运行应用

1. 确保后端服务已启动（参见 `backend/README.md`）
2. 在 Android Studio 中点击 `Run` → `Run 'app'`
3. 选择模拟器或真机设备

## 构建 APK

```bash
# 在 android 目录下执行
./gradlew assembleDebug

# APK 输出位置
# app/build/outputs/apk/debug/app-debug.apk
```

## 功能

### 已实现
- ✅ 双货币换算
- ✅ 货币选择器
- ✅ 汇率刷新
- ✅ 快捷货币对
- ✅ 更多货币网格
- ✅ 设置页面（主题、缓存、法律信息）
- ✅ 深色/浅色模式

### 待实现
- ⏳ AdMob 广告集成
- ⏳ 换算历史
- ⏳ 离线缓存
- ⏳ Google Play 上架

## 注意事项

- 应用需要网络权限来获取汇率数据
- Android 9+ 需要 HTTPS，HTTP 需要配置网络安全策略
- 模拟器访问本机后端使用 `10.0.2.2:8080`
