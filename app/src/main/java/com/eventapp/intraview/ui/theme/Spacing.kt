package com.eventapp.intraview.ui.theme

import androidx.compose.ui.unit.dp

/**
 * Responsive Spacing System for Consistent Design
 * Following 8dp grid system for visual harmony
 */
object AppSpacing {
    // Base spacing units
    val none = 0.dp
    val extraSmall = 4.dp
    val small = 8.dp
    val medium = 12.dp
    val normal = 16.dp
    val large = 20.dp
    val extraLarge = 24.dp
    val huge = 32.dp
    val massive = 40.dp
    val gigantic = 48.dp
    
    // Semantic spacing
    val cardPadding = normal
    val screenPadding = normal
    val sectionSpacing = extraLarge
    val itemSpacing = medium
    val minimalSpacing = small
}

/**
 * Component Dimensions for Consistency
 */
object AppDimensions {
    // Button heights
    val buttonHeightSmall = 36.dp
    val buttonHeightMedium = 48.dp
    val buttonHeightLarge = 56.dp
    
    // Icon sizes
    val iconSizeSmall = 16.dp
    val iconSizeMedium = 24.dp
    val iconSizeLarge = 32.dp
    val iconSizeExtraLarge = 48.dp
    
    // Card dimensions
    val cardElevationNone = 0.dp
    val cardElevationLow = 1.dp
    val cardElevationMedium = 4.dp
    val cardElevationHigh = 8.dp
    val cardElevationExtraHigh = 16.dp
    
    // Border radius
    val cornerRadiusSmall = 8.dp
    val cornerRadiusMedium = 12.dp
    val cornerRadiusLarge = 16.dp
    val cornerRadiusExtraLarge = 24.dp
    val cornerRadiusFull = 999.dp
    
    // Specific component sizes
    val minTouchTarget = 48.dp
    val dividerThickness = 1.dp
    val progressIndicatorSize = 48.dp
    val progressIndicatorSizeSmall = 24.dp
    
    // Image sizes
    val avatarSizeSmall = 32.dp
    val avatarSizeMedium = 48.dp
    val avatarSizeLarge = 64.dp
    val avatarSizeExtraLarge = 96.dp
    
    // Event card
    val eventCardHeight = 220.dp
    val eventCardMinHeight = 180.dp
}

/**
 * Responsive breakpoints for adaptive layouts
 */
object Breakpoints {
    val compact = 600.dp
    val medium = 840.dp
    val expanded = 1200.dp
}

