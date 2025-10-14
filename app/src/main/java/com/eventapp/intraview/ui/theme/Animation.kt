package com.eventapp.intraview.ui.theme

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer

/**
 * Modern Animation Utilities for Elegant UI Transitions
 */

// Standard animation durations following Material Design 3 guidelines
object AnimationDuration {
    const val FAST = 150
    const val NORMAL = 300
    const val SLOW = 500
    const val EXTRA_SLOW = 700
}

// Easing curves for natural motion
object AnimationEasing {
    val STANDARD = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1f)
    val DECELERATE = CubicBezierEasing(0.0f, 0.0f, 0.2f, 1f)
    val ACCELERATE = CubicBezierEasing(0.4f, 0.0f, 1f, 1f)
    val EMPHASIZED = CubicBezierEasing(0.2f, 0.0f, 0f, 1f)
    val EMPHASIZED_DECELERATE = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f)
}

// Standard spring specs for bouncy animations
object SpringSpec {
    val DEFAULT = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )
    
    val GENTLE = spring<Float>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessVeryLow
    )
    
    val BOUNCY = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium
    )
}

// Fade animations (these don't need @Composable as they just create animation specs)
fun fadeInAnimation(durationMillis: Int = AnimationDuration.NORMAL): EnterTransition {
    return fadeIn(
        animationSpec = tween(
            durationMillis = durationMillis,
            easing = AnimationEasing.DECELERATE
        )
    )
}

fun fadeOutAnimation(durationMillis: Int = AnimationDuration.FAST): ExitTransition {
    return fadeOut(
        animationSpec = tween(
            durationMillis = durationMillis,
            easing = AnimationEasing.ACCELERATE
        )
    )
}

// Slide animations
fun slideInVerticallyAnimation(
    initialOffsetY: (fullHeight: Int) -> Int = { it },
    durationMillis: Int = AnimationDuration.NORMAL
): EnterTransition {
    return slideInVertically(
        animationSpec = tween(
            durationMillis = durationMillis,
            easing = AnimationEasing.EMPHASIZED_DECELERATE
        ),
        initialOffsetY = initialOffsetY
    )
}

fun slideOutVerticallyAnimation(
    targetOffsetY: (fullHeight: Int) -> Int = { it },
    durationMillis: Int = AnimationDuration.FAST
): ExitTransition {
    return slideOutVertically(
        animationSpec = tween(
            durationMillis = durationMillis,
            easing = AnimationEasing.ACCELERATE
        ),
        targetOffsetY = targetOffsetY
    )
}

// Scale animations
fun scaleInAnimation(
    initialScale: Float = 0.8f,
    durationMillis: Int = AnimationDuration.NORMAL
): EnterTransition {
    return scaleIn(
        animationSpec = tween(
            durationMillis = durationMillis,
            easing = AnimationEasing.EMPHASIZED_DECELERATE
        ),
        initialScale = initialScale
    )
}

fun scaleOutAnimation(
    targetScale: Float = 0.8f,
    durationMillis: Int = AnimationDuration.FAST
): ExitTransition {
    return scaleOut(
        animationSpec = tween(
            durationMillis = durationMillis,
            easing = AnimationEasing.ACCELERATE
        ),
        targetScale = targetScale
    )
}

// Combined animations for elegant transitions
fun elegantEnterTransition(): EnterTransition {
    return fadeInAnimation() + scaleInAnimation(initialScale = 0.95f)
}

fun elegantExitTransition(): ExitTransition {
    return fadeOutAnimation() + scaleOutAnimation(targetScale = 0.95f)
}

fun slideUpEnterTransition(): EnterTransition {
    return slideInVerticallyAnimation(
        initialOffsetY = { it / 4 }
    ) + fadeInAnimation()
}

fun slideDownExitTransition(): ExitTransition {
    return slideOutVerticallyAnimation(
        targetOffsetY = { it / 4 }
    ) + fadeOutAnimation()
}

// Modifier extensions for animated effects
fun Modifier.animatedScale(
    targetScale: Float,
    animationSpec: AnimationSpec<Float> = tween(
        durationMillis = AnimationDuration.NORMAL,
        easing = AnimationEasing.EMPHASIZED_DECELERATE
    )
): Modifier = this.graphicsLayer {
    scaleX = targetScale
    scaleY = targetScale
}

fun Modifier.pressAnimation(
    pressed: Boolean,
    scaleDown: Float = 0.95f
): Modifier = this.scale(if (pressed) scaleDown else 1f)

// Shimmer effect for loading states
@Composable
fun rememberShimmerAnimation(): InfiniteTransition {
    return rememberInfiniteTransition(label = "shimmer")
}

@Composable
fun InfiniteTransition.shimmerAlpha(): androidx.compose.runtime.State<Float> {
    return animateFloat(
        initialValue = 0.3f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmerAlpha"
    )
}

// Stagger animations for lists
fun staggeredDelay(index: Int, baseDelay: Int = 50): Int {
    return (index * baseDelay).coerceAtMost(500)
}

