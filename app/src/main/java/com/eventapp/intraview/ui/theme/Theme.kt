package com.eventapp.intraview.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Modern Dark Color Scheme - Deep and elegant
private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlueLight,
    onPrimary = Color(0xFF0A1929),
    primaryContainer = PrimaryBlueDark,
    onPrimaryContainer = Color.White,

    secondary = SecondaryGreen,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE6E0FF),
    onSecondaryContainer = Color(0xFF1B1540),

    tertiary = SecondaryAmber,
    onTertiary = TextPrimary,
    tertiaryContainer = Color(0xFF424242),
    onTertiaryContainer = Color.White,

    error = ErrorRed,
    onError = Color.White,
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    background = BackgroundDark,
    onBackground = Color(0xFFE2E8F0),

    surface = SurfaceDark,
    onSurface = Color(0xFFE2E8F0),
    surfaceVariant = SurfaceDarkElevated,
    onSurfaceVariant = Color(0xFFCBD5E1),

    outline = Color(0xFF475569),
    outlineVariant = Color(0xFF334155)
)

// Modern Light Color Scheme - Clean and minimal
private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.White,
    primaryContainer = PrimaryBlueLight,
    onPrimaryContainer = TextPrimary,

    secondary = SecondaryGreen,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE6E0FF),
    onSecondaryContainer = TextPrimary,

    tertiary = SecondaryAmber,
    onTertiary = TextPrimary,
    tertiaryContainer = Color(0xFFEEEEEE),
    onTertiaryContainer = TextPrimary,

    error = ErrorRed,
    onError = Color.White,
    errorContainer = Color(0xFFFEE2E2),
    onErrorContainer = Color(0xFF410002),

    background = BackgroundLight,
    onBackground = TextPrimary,

    surface = SurfaceLight,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceElevated,
    onSurfaceVariant = TextSecondary,

    outline = DividerLight,
    outlineVariant = Color(0xFFF1F5F9)
)

@Composable
fun IntraViewTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
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
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Modern edge-to-edge with transparent status bar
            window.statusBarColor = android.graphics.Color.TRANSPARENT
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

