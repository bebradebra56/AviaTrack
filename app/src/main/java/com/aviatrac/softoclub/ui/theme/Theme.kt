package com.aviatrac.softoclub.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val AviaTrackColorScheme = darkColorScheme(
    primary = AccentRed,
    onPrimary = TextWhite,
    primaryContainer = AccentRedDark,
    onPrimaryContainer = TextWhite,
    
    secondary = TextGray,
    onSecondary = BackgroundBlack,
    secondaryContainer = SurfaceDark,
    onSecondaryContainer = TextWhite,
    
    tertiary = AccentRed,
    onTertiary = TextWhite,
    
    background = BackgroundBlack,
    onBackground = TextWhite,
    
    surface = BackgroundDarkGraphite,
    onSurface = TextWhite,
    surfaceVariant = SurfaceDark,
    onSurfaceVariant = TextGray,
    
    outline = DividerGray,
    outlineVariant = TextDarkGray,
    
    error = AccentRed,
    onError = TextWhite,
)

@Composable
fun AviaTrackTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AviaTrackColorScheme,
        typography = Typography,
        content = content
    )
}
