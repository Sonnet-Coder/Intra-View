package com.eventapp.intraview.ui.screens.event

import android.content.Intent
import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.eventapp.intraview.R
import com.eventapp.intraview.ui.components.ErrorState
import com.eventapp.intraview.ui.components.EventSettingsDialog
import com.eventapp.intraview.ui.components.LoadingState
import com.eventapp.intraview.ui.theme.AppDimensions
import com.eventapp.intraview.ui.theme.AppSpacing
import com.eventapp.intraview.util.DateFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    eventId: String,
    onNavigateBack: () -> Unit,
    onNavigateToPhotos: () -> Unit,
    onNavigateToQR: () -> Unit,
    onNavigateToScanner: () -> Unit,
    onNavigateToPlaylist: () -> Unit,
    viewModel: EventDetailViewModel = hiltViewModel()
) {
    val event by viewModel.event.collectAsState()
    val invitations by viewModel.invitations.collectAsState()
    val recentPhotos by viewModel.recentPhotos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val isHost by viewModel.isHost.collectAsState()
    val context = LocalContext.current
    
    // State for settings dialog
    var showSettingsDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(eventId) {
        viewModel.loadEvent(eventId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(event?.name ?: "") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (isHost) {
                        // Settings button - removed scanner from here
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            isLoading && event == null -> {
                LoadingState()
            }
            error != null && event == null -> {
                ErrorState(
                    message = error ?: "Unknown error",
                    onRetry = { viewModel.loadEvent(eventId) }
                )
            }
            event != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Event Header with Background
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    ) {
                        AsyncImage(
                            model = event!!.backgroundImageUrl,
                            contentDescription = event!!.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.Black.copy(alpha = 0.7f)
                                        )
                                    )
                                )
                        )
                        
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp)
                        ) {
                            Text(
                                text = event!!.name,
                                style = MaterialTheme.typography.headlineLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                textDecoration = if (event!!.isCancelled) TextDecoration.LineThrough else null
                            )
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            Text(
                                text = DateFormatter.formatDateTime(event!!.date),
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White.copy(alpha = 0.9f),
                                textDecoration = if (event!!.isCancelled) TextDecoration.LineThrough else null
                            )
                            
                            Text(
                                text = event!!.location,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.9f),
                                textDecoration = if (event!!.isCancelled) TextDecoration.LineThrough else null
                            )
                        }
                    }
                    
                    Column(modifier = Modifier.padding(AppSpacing.normal)) {
                        // Description
                        if (event!!.description.isNotEmpty()) {
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn() + expandVertically()
                            ) {
                                Column {
                                    Text(
                                        text = event!!.description,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    
                                    Spacer(modifier = Modifier.height(AppSpacing.extraLarge))
                                }
                            }
                        }
                        
                        // Quick Actions with modern cards
                        Text(
                            text = "Quick Actions",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        
                        Spacer(modifier = Modifier.height(AppSpacing.medium))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(AppSpacing.medium)
                        ) {
                            FilledTonalButton(
                                onClick = {
                                    val inviteCode = viewModel.getInviteCode()
                                    val shareText = """
                                        You're invited to ${event!!.name}!
                                        
                                        Date: ${DateFormatter.formatDateTime(event!!.date)}
                                        Location: ${event!!.location}
                                        
                                        Invite Code: $inviteCode
                                        
                                        Download the app and enter the code to join!
                                    """.trimIndent()
                                    
                                    val intent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_TEXT, shareText)
                                    }
                                    context.startActivity(Intent.createChooser(intent, "Share Invitation"))
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(AppDimensions.buttonHeightMedium),
                                shape = RoundedCornerShape(AppDimensions.cornerRadiusMedium)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Share,
                                    contentDescription = null,
                                    modifier = Modifier.size(AppDimensions.iconSizeMedium)
                                )
                                Spacer(modifier = Modifier.width(AppSpacing.small))
                                Text(
                                    text = stringResource(R.string.share_invite),
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                            
                            // Settings button for host, QR button for guests
                            if (isHost) {
                                FilledTonalButton(
                                    onClick = { showSettingsDialog = true },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(AppDimensions.buttonHeightMedium),
                                    shape = RoundedCornerShape(AppDimensions.cornerRadiusMedium)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Settings,
                                        contentDescription = null,
                                        modifier = Modifier.size(AppDimensions.iconSizeMedium)
                                    )
                                    Spacer(modifier = Modifier.width(AppSpacing.small))
                                    Text(
                                        text = "Settings",
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }
                            } else {
                                FilledTonalButton(
                                    onClick = onNavigateToQR,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(AppDimensions.buttonHeightMedium),
                                    shape = RoundedCornerShape(AppDimensions.cornerRadiusMedium)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.QrCode,
                                        contentDescription = null,
                                        modifier = Modifier.size(AppDimensions.iconSizeMedium)
                                    )
                                    Spacer(modifier = Modifier.width(AppSpacing.small))
                                    Text(
                                        text = stringResource(R.string.my_qr),
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(AppSpacing.huge))
                        
                        // Guests Section with modern header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(AppSpacing.small)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Group,
                                    contentDescription = null,
                                    modifier = Modifier.size(AppDimensions.iconSizeMedium),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "${stringResource(R.string.guests)} (${invitations.size})",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            
                            TextButton(
                                onClick = { 
                                    Log.d("EventDetailScreen", "View All clicked. Invitations: ${invitations.size}, GuestIds: ${event!!.guestIds.size}")
                                    // TODO: Navigate to guests list screen when implemented
                                },
                                shape = RoundedCornerShape(AppDimensions.cornerRadiusSmall)
                            ) {
                                Text(
                                    text = stringResource(R.string.view_all),
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(AppSpacing.medium))
                        
                        if (invitations.isNotEmpty()) {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(invitations.take(10)) { invitation ->
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.width(60.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(60.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.primaryContainer)
                                        ) {
                                            if (invitation.checkedIn) {
                                                Icon(
                                                    Icons.Default.CheckCircle,
                                                    contentDescription = "Checked in",
                                                    modifier = Modifier
                                                        .align(Alignment.BottomEnd)
                                                        .size(20.dp),
                                                    tint = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        
                        // Shared Photo Album Section (show to host always, to guests only if enabled)
                        if (isHost || event!!.showPhotosToGuests) {
                            Spacer(modifier = Modifier.height(AppSpacing.huge))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.small)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.PhotoLibrary,
                                        contentDescription = null,
                                        modifier = Modifier.size(AppDimensions.iconSizeMedium),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "Shared Photo Album (${event!!.photoCount})",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                
                                TextButton(
                                    onClick = onNavigateToPhotos,
                                    shape = RoundedCornerShape(AppDimensions.cornerRadiusSmall)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Add,
                                        contentDescription = null,
                                        modifier = Modifier.size(AppDimensions.iconSizeSmall)
                                    )
                                    Spacer(modifier = Modifier.width(AppSpacing.extraSmall))
                                    Text(
                                        text = stringResource(R.string.upload),
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(AppSpacing.medium))
                            
                            if (recentPhotos.isNotEmpty()) {
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(recentPhotos) { photo ->
                                        Card(
                                            modifier = Modifier
                                                .size(100.dp)
                                                .clickable(onClick = onNavigateToPhotos),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            AsyncImage(
                                                model = photo.thumbnailUrl,
                                                contentDescription = "Photo",
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                        }
                                    }
                                }
                            } else {
                                Text(
                                    text = stringResource(R.string.no_photos),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        
                        // Shared Music Playlist Section (show to host always, to guests only if enabled)
                        if (isHost || event!!.showPlaylistsToGuests) {
                            Spacer(modifier = Modifier.height(AppSpacing.huge))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.small)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.MusicNote,
                                        contentDescription = null,
                                        modifier = Modifier.size(AppDimensions.iconSizeMedium),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "Shared Music Playlist",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                
                                TextButton(
                                    onClick = onNavigateToPlaylist,
                                    shape = RoundedCornerShape(AppDimensions.cornerRadiusSmall)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Add,
                                        contentDescription = null,
                                        modifier = Modifier.size(AppDimensions.iconSizeSmall)
                                    )
                                    Spacer(modifier = Modifier.width(AppSpacing.extraSmall))
                                    Text(
                                        text = stringResource(R.string.add_playlist),
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(AppSpacing.medium))
                            
                            if (event!!.playlistUrls.isNotEmpty()) {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    event!!.playlistUrls.forEach { url ->
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable(onClick = onNavigateToPlaylist),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(16.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    Icons.Default.MusicNote,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.primary
                                                )
                                                Spacer(modifier = Modifier.width(16.dp))
                                                Text("Playlist", style = MaterialTheme.typography.bodyMedium)
                                            }
                                        }
                                    }
                                }
                            } else if (isHost) {
                                Text(
                                    text = "No playlists yet",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
        
        // Settings Dialog for hosts
        if (showSettingsDialog && event != null) {
            EventSettingsDialog(
                event = event!!,
                onDismiss = { showSettingsDialog = false },
                onChangeGuestLimit = { newLimit ->
                    viewModel.updateEventField("maxGuests", newLimit)
                },
                onTogglePublic = { isPublic ->
                    viewModel.updateEventField("isPublic", isPublic)
                },
                onToggleSharedAlbum = { visible ->
                    viewModel.updateEventField("showPhotosToGuests", visible)
                },
                onTogglePlaylist = { visible ->
                    viewModel.updateEventField("showPlaylistsToGuests", visible)
                },
                onCancelEvent = {
                    viewModel.updateEventField("isCancelled", true)
                },
                onDeleteEvent = {
                    viewModel.deleteEvent()
                    onNavigateBack()
                },
                onCheckInScan = onNavigateToScanner
            )
        }
    }
}


