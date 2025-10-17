package com.eventapp.intraview.ui.screens.qr

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.eventapp.intraview.R
import com.eventapp.intraview.ui.components.LoadingState
import com.eventapp.intraview.ui.theme.AppDimensions
import com.eventapp.intraview.ui.theme.AppSpacing
import com.eventapp.intraview.util.DateFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QRDisplayScreen(
    eventId: String,
    onNavigateBack: () -> Unit,
    viewModel: QRViewModel = hiltViewModel()
) {
    val event by viewModel.event.collectAsState()
    val myInvitation by viewModel.myInvitation.collectAsState()
    val qrBitmap by viewModel.qrBitmap.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    LaunchedEffect(eventId) {
        viewModel.loadEventAndInvitation(eventId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.my_qr)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            isLoading -> {
                LoadingState()
            }
            event != null && myInvitation != null -> {
                // Pulsing animation for QR code
                val infiniteTransition = rememberInfiniteTransition(label = "qr")
                val qrScale by infiniteTransition.animateFloat(
                    initialValue = 0.98f,
                    targetValue = 1.02f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(2000, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "qrScale"
                )
                
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(AppSpacing.extraLarge),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Event Info
                    Text(
                        text = event!!.name,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(AppSpacing.small))
                    
                    Text(
                        text = DateFormatter.formatDate(event!!.date),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(AppSpacing.huge))
                    
                    // QR Code with modern card
                    qrBitmap?.let { bitmap ->
                        Card(
                            modifier = Modifier
                                .size(300.dp)
                                .scale(qrScale),
                            shape = RoundedCornerShape(AppDimensions.cornerRadiusExtraLarge),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = AppDimensions.cardElevationHigh
                            )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(AppSpacing.extraLarge),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    bitmap = bitmap.asImageBitmap(),
                                    contentDescription = "QR Code",
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(AppSpacing.huge))
                    
                    // Check-in status with modern badge
                    Surface(
                        shape = RoundedCornerShape(AppDimensions.cornerRadiusFull),
                        color = if (myInvitation!!.checkedIn)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Row(
                            modifier = Modifier.padding(
                                horizontal = AppSpacing.large,
                                vertical = AppSpacing.medium
                            ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(AppSpacing.small)
                        ) {
                            if (myInvitation!!.checkedIn) {
                                Icon(
                                    imageVector = Icons.Outlined.CheckCircle,
                                    contentDescription = "Checked in",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(AppDimensions.iconSizeMedium)
                                )
                                Text(
                                    text = stringResource(R.string.checked_in),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontWeight = FontWeight.SemiBold
                                )
                            } else {
                                Text(
                                    text = stringResource(R.string.pending),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(AppSpacing.large))
                    
                    Text(
                        text = "Show this QR code at the entrance",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = error ?: "No invitation found",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.error
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "Please try rejoining the event from the home screen",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Button(onClick = onNavigateBack) {
                            Text("Go Back")
                        }
                    }
                }
            }
        }
    }
}


