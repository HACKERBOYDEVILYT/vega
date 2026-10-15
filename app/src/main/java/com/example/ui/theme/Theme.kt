package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ExecutiveDarkColorScheme = darkColorScheme(
    primary = VegaCyan,
    onPrimary = VegaOnCyan,
    primaryContainer = VegaCyanContainer,
    onPrimaryContainer = VegaCyan,
    secondary = VegaViolet,
    onSecondary = VegaOnViolet,
    secondaryContainer = VegaVioletContainer,
    onSecondaryContainer = VegaViolet,
    tertiary = VegaEmerald,
    background = VegaNavyBackground,
    surface = VegaNavySurface,
    surfaceVariant = VegaNavySurfaceVariant,
    onBackground = VegaTextPrimary,
    onSurface = VegaTextPrimary,
    onSurfaceVariant = VegaTextSecondary,
    outline = VegaNavyBorder,
    outlineVariant = VegaNavySurfaceVariant
)

private val ExecutiveLightColorScheme = lightColorScheme(
    primary = VegaCyanDark,
    onPrimary = VegaTextPrimary,
    primaryContainer = Color(0xFFE0F7FA),
    secondary = VegaViolet,
    tertiary = VegaEmerald,
    background = Color(0xFFF8FAFC),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFF1F5F9),
    onBackground = Color(0xFF0F172A),
    onSurface = Color(0xFF0F172A),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFFCBD5E1)
)

@Composable
fun VegaTheme(
    darkTheme: Boolean = true, // Default to Executive Dark theme
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) ExecutiveDarkColorScheme else ExecutiveLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    VegaTheme(darkTheme = true, content = content)
}
