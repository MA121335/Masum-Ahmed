package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = BloodRedLight,
    onPrimary = Color.Black,
    primaryContainer = BloodRedContainerDark,
    onPrimaryContainer = BloodRedOnContainerDark,
    secondary = UrgentAmberLight,
    onSecondary = Color.Black,
    secondaryContainer = UrgentAmberDark,
    onSecondaryContainer = Color.White,
    tertiary = VerifiedGreenDark,
    onTertiary = Color.Black,
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceDark,
    outline = OutlineDark
)

private val LightColorScheme = lightColorScheme(
    primary = BloodRed,
    onPrimary = Color.White,
    primaryContainer = BloodRedContainerLight,
    onPrimaryContainer = BloodRedOnContainerLight,
    secondary = UrgentAmber,
    onSecondary = Color.White,
    secondaryContainer = UrgentAmberLight,
    onSecondaryContainer = UrgentAmberDark,
    tertiary = VerifiedGreen,
    onTertiary = Color.White,
    tertiaryContainer = VerifiedGreenContainer,
    onTertiaryContainer = VerifiedGreen,
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnBackgroundLight,
    outline = OutlineLight
)

@Composable
fun SahiddirgonjBloodTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our brand colors for cohesive blood donor aesthetic
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
