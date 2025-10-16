package com.eventapp.intraview.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.eventapp.intraview.R
import com.eventapp.intraview.data.model.Event
import com.eventapp.intraview.ui.theme.AppDimensions
import com.eventapp.intraview.ui.theme.AppSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventSettingsDialog(
    event: Event,
    onDismiss: () -> Unit,
    onChangeGuestLimit: (Int?) -> Unit,
    onTogglePublic: (Boolean) -> Unit,
    onToggleSharedAlbum: (Boolean) -> Unit,
    onTogglePlaylist: (Boolean) -> Unit,
    onCancelEvent: () -> Unit,
    onDeleteEvent: () -> Unit,
    onCheckInScan: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showGuestLimitPicker by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showCancelConfirm by remember { mutableStateOf(false) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
        title = {
            Text(
                text = "Event Settings",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.small)
            ) {
                // Check-in Scan
                SettingsOption(
                    icon = Icons.Outlined.QrCodeScanner,
                    title = "Check-in Scan",
                    subtitle = "Scan guest QR codes",
                    onClick = {
                        onDismiss()
                        onCheckInScan()
                    }
                )
                
                Divider()
                
                // Change Guest Limit
                SettingsOption(
                    icon = Icons.Outlined.Group,
                    title = stringResource(R.string.max_guests),
                    subtitle = event.maxGuests?.toString() ?: stringResource(R.string.no_limit),
                    onClick = { showGuestLimitPicker = true }
                )
                
                Divider()
                
                // Public/Private Toggle
                SettingsToggle(
                    icon = if (event.isPublic) Icons.Outlined.Public else Icons.Outlined.Lock,
                    title = if (event.isPublic) stringResource(R.string.public_event) else stringResource(R.string.private_event),
                    subtitle = if (event.isPublic) "Visible on discover page" else "Invite only",
                    checked = event.isPublic,
                    onCheckedChange = onTogglePublic
                )
                
                Divider()
                
                // Shared Photo Album Visibility (for guests)
                SettingsToggle(
                    icon = Icons.Outlined.PhotoLibrary,
                    title = "Shared Photo Album",
                    subtitle = "Allow guests to view and upload photos",
                    checked = event.showPhotosToGuests,
                    onCheckedChange = onToggleSharedAlbum
                )
                
                Divider()
                
                // Shared Music Playlist Visibility (for guests)
                SettingsToggle(
                    icon = Icons.Outlined.MusicNote,
                    title = "Shared Music Playlist",
                    subtitle = "Allow guests to view and add playlists",
                    checked = event.showPlaylistsToGuests,
                    onCheckedChange = onTogglePlaylist
                )
                
                Divider()
                
                // Cancel Event
                SettingsOption(
                    icon = Icons.Outlined.Cancel,
                    title = "Cancel Event",
                    subtitle = "Event will be marked as cancelled",
                    onClick = { showCancelConfirm = true },
                    isWarning = true
                )
                
                Divider()
                
                // Delete Event
                SettingsOption(
                    icon = Icons.Outlined.Delete,
                    title = stringResource(R.string.delete),
                    subtitle = "Permanently delete this event",
                    onClick = { showDeleteConfirm = true },
                    isDanger = true
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
    
    // Guest Limit Picker Dialog
    if (showGuestLimitPicker) {
        val guestOptions = listOf(
            null to stringResource(R.string.no_limit),
            10 to "10 guests",
            25 to "25 guests",
            50 to "50 guests",
            100 to "100 guests",
            200 to "200 guests",
            500 to "500 guests"
        )
        
        AlertDialog(
            onDismissRequest = { showGuestLimitPicker = false },
            title = { Text(stringResource(R.string.max_guests)) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    guestOptions.forEach { (count, label) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onChangeGuestLimit(count)
                                    showGuestLimitPicker = false
                                }
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(label)
                            if (count == event.maxGuests) {
                                Text(
                                    "✓",
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showGuestLimitPicker = false }) {
                    Text("Close")
                }
            }
        )
    }
    
    // Cancel Event Confirmation
    if (showCancelConfirm) {
        AlertDialog(
            onDismissRequest = { showCancelConfirm = false },
            icon = { Icon(Icons.Outlined.Cancel, contentDescription = null) },
            title = { Text("Cancel Event?") },
            text = { Text("The event will be marked as cancelled. Guests will still see the event but know it's cancelled.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onCancelEvent()
                        showCancelConfirm = false
                        onDismiss()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Cancel Event")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelConfirm = false }) {
                    Text("Keep Event")
                }
            }
        )
    }
    
    // Delete Event Confirmation
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            icon = { Icon(Icons.Outlined.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Delete Event?") },
            text = { Text("This action cannot be undone. All event data, photos, and playlists will be permanently deleted.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteEvent()
                        showDeleteConfirm = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
private fun SettingsOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    isWarning: Boolean = false,
    isDanger: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = AppSpacing.medium),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = when {
                isDanger -> MaterialTheme.colorScheme.error
                isWarning -> MaterialTheme.colorScheme.tertiary
                else -> MaterialTheme.colorScheme.primary
            },
            modifier = Modifier.size(AppDimensions.iconSizeMedium)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = if (isDanger) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            imageVector = Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SettingsToggle(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = AppSpacing.small),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(AppDimensions.iconSizeMedium)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

