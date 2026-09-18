package com.openjarvis.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object VoidColor {
    val Void950 = Color(0xFF0A0A12)
    val Void900 = Color(0xFF10101C)
    val Void800 = Color(0xFF181829)
    val Void700 = Color(0xFF222238)
    val Void600 = Color(0xFF33334E)
    
    val BorderSubtle = Color(0xFF2E2E48)
    val BorderGlow = Color(0xFF7C3AED)
    
    val Violet = Color(0xFF8B5CF6)
    val VioletDim = Color(0xFF5B21B6)
    val Cyan = Color(0xFF06B6D4)
    val Green = Color(0xFF10B981)
    val Amber = Color(0xFFF59E0B)
    val Red = Color(0xFFEF4444)
    
    val TextPrimary = Color(0xFFFFFFFF)
    val TextSecondary = Color(0xFF94A3B8)
    val TextDisabled = Color(0xFF64748B)
}

private val DarkColorScheme = darkColorScheme(
    primary = VoidColor.Violet,
    secondary = VoidColor.Cyan,
    tertiary = VoidColor.Amber,
    background = VoidColor.Void950,
    surface = VoidColor.Void900,
    surfaceVariant = VoidColor.Void800,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = VoidColor.TextPrimary,
    onSurface = VoidColor.TextPrimary,
    onSurfaceVariant = VoidColor.TextSecondary,
    error = VoidColor.Red
)

@Composable
fun OpenJarvisTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
