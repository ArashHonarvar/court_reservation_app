package it.polito.mad.reservationapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val ColorScheme = darkColorScheme(
    primary = Background,
    secondary = Background_Secondary,
    tertiary = Button,

    // Other default colors to override
    background = Background,
    surface = Background_Secondary,
    onPrimary = Text,
    onSecondary = Text,
    onTertiary = Text,
    onBackground = Text,
    onSurface = Text,
)

@Composable
fun ReservationAppThemeCustom(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = ColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}