package com.nkechinnaji.thinkbit.ui.theme

import android.app.Activity
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
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun ThinkBitTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LighterColorScheme
            //LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

private val LighterColorScheme = lightColorScheme(
    primary = Purple40,                 // Dark Purple for main buttons
    onPrimary = Color.White,            // Text on primary buttons
    primaryContainer = Purple80,        // Lighter purple for containers (e.g., selected Card)
    onPrimaryContainer = PurpleGrey40,  // Text on primary container

    secondary = Pink40,                 // Dark Pink for secondary actions/accents
    onSecondary = Color.White,
    secondaryContainer = Pink80,
    onSecondaryContainer = Pink40,

    tertiary = PurpleGrey40,            // Dark Purple-Grey for other accents
    onTertiary = Color.White,
    tertiaryContainer = PurpleGrey80,
    onTertiaryContainer = Purple40,

    error = Color(0xFFB00020),          // Standard Error Red
    onError = Color.White,
    errorContainer = Color(0xFFFCD8DF),
    onErrorContainer = Color(0xFFB00020),

    background = Pink10,                // Very light pinkish background for the view
    onBackground = PurpleGrey40,        // Dark grey text on light background

    surface = Pink10,                   // Surface for Cards, SearchBar input area
    onSurface = PurpleGrey40,           // Text on surfaces

    surfaceVariant = PurpleGrey80,      // Slightly different surface (e.g., SearchBar outline)
    onSurfaceVariant = PurpleGrey40,    // Text on surface variant

    outline = PurpleGrey80              // Outlines for components like OutlinedTextField
    // inversePrimary, surfaceTint, etc. can be left to defaults or customized
)