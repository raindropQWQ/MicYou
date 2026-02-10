package com.lanrhyme.androidmic_md

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Simple seed-based generation (simplified version of tonal palettes)
// In a real app, you'd use a full material-color-utilities library port
fun generateColorScheme(seed: Color, isDark: Boolean) = if (isDark) {
    darkColorScheme(
        primary = seed,
        secondary = seed.copy(alpha = 0.8f),
        tertiary = seed.copy(alpha = 0.6f),
        surface = Color(0xFF1C1B1F),
        background = Color(0xFF1C1B1F)
    )
} else {
    lightColorScheme(
        primary = seed,
        secondary = seed.copy(alpha = 0.8f),
        tertiary = seed.copy(alpha = 0.6f),
        surface = Color(0xFFFFFBFE),
        background = Color(0xFFFFFBFE)
    )
}

enum class ThemeMode {
    System, Light, Dark
}

val DefaultSeedColor = Color(0xFF6750A4)

@Composable
fun AppTheme(
    themeMode: ThemeMode = ThemeMode.System,
    seedColor: Color = DefaultSeedColor,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        ThemeMode.System -> isSystemInDarkTheme()
        ThemeMode.Light -> false
        ThemeMode.Dark -> true
    }

    val colorScheme = generateColorScheme(seedColor, isDark)

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
