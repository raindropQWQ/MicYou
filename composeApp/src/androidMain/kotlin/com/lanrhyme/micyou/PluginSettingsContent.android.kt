package com.lanrhyme.micyou

import androidx.compose.runtime.Composable

@Composable
actual fun OpenPluginWindow(
    pluginId: String,
    viewModel: MainViewModel,
    onClose: () -> Unit
) {
    // Android 平台暂不支持独立窗口，使用对话框方式
    // 可以在未来实现为全屏 Activity 或底部弹窗
    onClose()
}

@Composable
actual fun OpenPluginSettings(
    pluginId: String,
    viewModel: MainViewModel,
    onClose: () -> Unit
) {
    // Android 平台暂不支持
    onClose()
}
