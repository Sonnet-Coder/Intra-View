package com.eventapp.intraview.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.eventapp.intraview.R
import com.eventapp.intraview.ui.theme.AppDimensions
import com.eventapp.intraview.ui.theme.AppSpacing

@Composable
fun ErrorState(
    message: String,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    // Subtle shake animation for attention
    val infiniteTransition = rememberInfiniteTransition(label = "error")
    
    val shake by infiniteTransition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(100, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shake"
    )
    
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(AppSpacing.huge)
        ) {
            // Error icon with elegant background
            Box(
                modifier = Modifier
                    .size(AppDimensions.iconSizeExtraLarge * 2.5f)
                    .background(
                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.ErrorOutline,
                    contentDescription = null,
                    modifier = Modifier
                        .size(AppDimensions.iconSizeExtraLarge * 1.5f)
                        .offset(x = shake.dp),
                    tint = MaterialTheme.colorScheme.error
                )
            }
            
            Spacer(modifier = Modifier.height(AppSpacing.extraLarge))
            
            Text(
                text = message,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            
            if (onRetry != null) {
                Spacer(modifier = Modifier.height(AppSpacing.extraLarge))
                
                Button(
                    onClick = onRetry,
                    modifier = Modifier
                        .height(AppDimensions.buttonHeightMedium),
                    shape = RoundedCornerShape(AppDimensions.cornerRadiusMedium)
                ) {
                    Text(
                        text = stringResource(R.string.retry),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}


