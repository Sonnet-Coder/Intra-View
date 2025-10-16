package com.eventapp.intraview.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.eventapp.intraview.R
import com.eventapp.intraview.ui.theme.AppDimensions
import com.eventapp.intraview.ui.theme.AppSpacing

@Composable
fun FloatingActionMenu(
    onHostEventClick: () -> Unit,
    onJoinEventClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    
    // Rotation animation for the FAB icon
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 45f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "fabRotation"
    )
    
    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomEnd
    ) {
        // Scrim overlay
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn(animationSpec = tween(200)),
            exit = fadeOut(animationSpec = tween(200))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { expanded = false }
                    )
            )
        }
        
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(AppSpacing.medium)
        ) {
            // Menu items
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn(animationSpec = tween(300, delayMillis = 50)) +
                        slideInVertically(
                            animationSpec = tween(300, delayMillis = 50),
                            initialOffsetY = { it / 2 }
                        ) +
                        scaleIn(
                            animationSpec = tween(300, delayMillis = 50),
                            initialScale = 0.8f
                        ),
                exit = fadeOut(animationSpec = tween(200)) +
                        slideOutVertically(
                            animationSpec = tween(200),
                            targetOffsetY = { it / 2 }
                        ) +
                        scaleOut(
                            animationSpec = tween(200),
                            targetScale = 0.8f
                        )
            ) {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.medium)
                ) {
                    // Host Event option
                    FloatingActionMenuItem(
                        icon = Icons.Outlined.Event,
                        label = stringResource(R.string.host_event),
                        onClick = {
                            expanded = false
                            onHostEventClick()
                        }
                    )
                    
                    // Join Event option
                    FloatingActionMenuItem(
                        icon = Icons.Outlined.QrCodeScanner,
                        label = stringResource(R.string.join_event),
                        onClick = {
                            expanded = false
                            onJoinEventClick()
                        }
                    )
                }
            }
            
            // Main FAB
            FloatingActionButton(
                onClick = { expanded = !expanded },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 6.dp,
                    pressedElevation = 8.dp
                )
            ) {
                Icon(
                    imageVector = if (expanded) Icons.Default.Close else Icons.Default.Add,
                    contentDescription = if (expanded) "Close menu" else "Open menu",
                    modifier = Modifier.rotate(rotation)
                )
            }
        }
    }
}

@Composable
private fun FloatingActionMenuItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.medium)
    ) {
        // Label
        Surface(
            shape = RoundedCornerShape(AppDimensions.cornerRadiusMedium),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 4.dp,
            tonalElevation = 2.dp
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(
                    horizontal = AppSpacing.normal,
                    vertical = AppSpacing.small
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        
        // Small FAB
        SmallFloatingActionButton(
            onClick = onClick,
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 4.dp,
                pressedElevation = 6.dp
            )
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(AppDimensions.iconSizeMedium)
            )
        }
    }
}

