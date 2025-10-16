package com.eventapp.intraview.ui.screens.event

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.eventapp.intraview.R
import com.eventapp.intraview.ui.components.LocationData
import com.eventapp.intraview.ui.components.MapLocationPicker
import com.eventapp.intraview.ui.theme.AppDimensions
import com.eventapp.intraview.ui.theme.AppSpacing
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventScreen(
    onEventCreated: (String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: CreateEventViewModel = hiltViewModel()
) {
    val name by viewModel.name.collectAsState()
    val description by viewModel.description.collectAsState()
    val date by viewModel.date.collectAsState()
    val location by viewModel.location.collectAsState()
    val latitude by viewModel.latitude.collectAsState()
    val longitude by viewModel.longitude.collectAsState()
    val selectedBackgroundIndex by viewModel.selectedBackgroundIndex.collectAsState()
    val musicPlaylistUrl by viewModel.musicPlaylistUrl.collectAsState()
    val sharedAlbumUrl by viewModel.sharedAlbumUrl.collectAsState()
    val maxGuests by viewModel.maxGuests.collectAsState()
    val isPublic by viewModel.isPublic.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val createdEventId by viewModel.createdEventId.collectAsState()
    
    // State for date and time pickers
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showDurationPicker by remember { mutableStateOf(false) }
    var showMapPicker by remember { mutableStateOf(false) }
    var showMaxGuestsPicker by remember { mutableStateOf(false) }
    var showMusicPlaylist by remember { mutableStateOf(false) }
    var showSharedAlbum by remember { mutableStateOf(false) }
    
    val durationMinutes by viewModel.durationMinutes.collectAsState()
    
    val calendar = Calendar.getInstance().apply {
        time = date
    }
    
    // Duration options in minutes
    val durationOptions = listOf(
        30 to "30 minutes",
        60 to "1 hour",
        90 to "1.5 hours",
        120 to "2 hours",
        150 to "2.5 hours",
        180 to "3 hours",
        240 to "4 hours",
        300 to "5 hours",
        360 to "6 hours",
        480 to "8 hours",
        720 to "12 hours",
        1440 to "1 day"
    )
    
    LaunchedEffect(createdEventId) {
        createdEventId?.let { eventId ->
            onEventCreated(eventId)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.create_event)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(AppSpacing.normal),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.normal)
        ) {
            // Event Name with icon
            OutlinedTextField(
                value = name,
                onValueChange = { viewModel.setName(it) },
                label = { Text(stringResource(R.string.event_name)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Event,
                        contentDescription = null,
                        tint = if (name.isNotEmpty()) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(AppDimensions.cornerRadiusMedium)
            )
            
            // Description with icon
            OutlinedTextField(
                value = description,
                onValueChange = { viewModel.setDescription(it) },
                label = { Text(stringResource(R.string.description)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Description,
                        contentDescription = null,
                        tint = if (description.isNotEmpty()) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5,
                shape = RoundedCornerShape(AppDimensions.cornerRadiusMedium)
            )
            
            // Date & Time with icon
            OutlinedTextField(
                value = SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault()).format(date),
                onValueChange = { },
                label = { Text(stringResource(R.string.date_time)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Schedule,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.ArrowDropDown,
                        contentDescription = null
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true },
                readOnly = true,
                enabled = false,
                shape = RoundedCornerShape(AppDimensions.cornerRadiusMedium),
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLeadingIconColor = MaterialTheme.colorScheme.primary,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            
            // Location with icon and map picker button
            OutlinedTextField(
                value = location,
                onValueChange = { viewModel.setLocation(it) },
                label = { Text(stringResource(R.string.location)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Place,
                        contentDescription = null,
                        tint = if (location.isNotEmpty()) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { showMapPicker = true }) {
                        Icon(
                            imageVector = Icons.Outlined.Map,
                            contentDescription = stringResource(R.string.pick_location),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(AppDimensions.cornerRadiusMedium)
            )
            
            // Duration with icon
            OutlinedTextField(
                value = durationOptions.find { it.first == durationMinutes }?.second ?: "$durationMinutes minutes",
                onValueChange = { },
                label = { Text(stringResource(R.string.duration)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Timer,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.ArrowDropDown,
                        contentDescription = null
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDurationPicker = true },
                readOnly = true,
                enabled = false,
                shape = RoundedCornerShape(AppDimensions.cornerRadiusMedium),
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLeadingIconColor = MaterialTheme.colorScheme.primary,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            
            // Maximum Guests with icon
            OutlinedTextField(
                value = maxGuests?.toString() ?: stringResource(R.string.no_limit),
                onValueChange = { },
                label = { Text(stringResource(R.string.max_guests)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Group,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.ArrowDropDown,
                        contentDescription = null
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showMaxGuestsPicker = true },
                readOnly = true,
                enabled = false,
                shape = RoundedCornerShape(AppDimensions.cornerRadiusMedium),
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLeadingIconColor = MaterialTheme.colorScheme.primary,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            
            // Music Playlist Toggle and URL (Optional)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(AppDimensions.cornerRadiusMedium))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(AppDimensions.cornerRadiusMedium)
                    )
                    .padding(AppSpacing.normal),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.small),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.MusicNote,
                        contentDescription = null,
                        tint = if (showMusicPlaylist) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = stringResource(R.string.music_playlist),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Switch(
                    checked = showMusicPlaylist,
                    onCheckedChange = { 
                        showMusicPlaylist = it
                        if (!it) viewModel.setMusicPlaylistUrl("")
                    }
                )
            }
            
            AnimatedVisibility(
                visible = showMusicPlaylist,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                OutlinedTextField(
                    value = musicPlaylistUrl,
                    onValueChange = { viewModel.setMusicPlaylistUrl(it) },
                    label = { Text("Playlist URL") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(AppDimensions.cornerRadiusMedium),
                    placeholder = { Text("Spotify, Apple Music, etc.") }
                )
            }
            
            // Shared Album Toggle and URL (Optional)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(AppDimensions.cornerRadiusMedium))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(AppDimensions.cornerRadiusMedium)
                    )
                    .padding(AppSpacing.normal),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.small),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PhotoLibrary,
                        contentDescription = null,
                        tint = if (showSharedAlbum) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = stringResource(R.string.shared_album),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Switch(
                    checked = showSharedAlbum,
                    onCheckedChange = { 
                        showSharedAlbum = it
                        if (!it) viewModel.setSharedAlbumUrl("")
                    }
                )
            }
            
            AnimatedVisibility(
                visible = showSharedAlbum,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                OutlinedTextField(
                    value = sharedAlbumUrl,
                    onValueChange = { viewModel.setSharedAlbumUrl(it) },
                    label = { Text("Album URL") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(AppDimensions.cornerRadiusMedium),
                    placeholder = { Text("Google Photos, iCloud, etc.") }
                )
            }
            
            // Public/Private Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(AppDimensions.cornerRadiusMedium))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(AppDimensions.cornerRadiusMedium)
                    )
                    .padding(AppSpacing.normal),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.small),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isPublic) Icons.Outlined.Public else Icons.Outlined.Lock,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Column {
                        Text(
                            text = if (isPublic) stringResource(R.string.public_event) else stringResource(R.string.private_event),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = if (isPublic) "Visible on discover page" else "Invite only",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Switch(
                    checked = isPublic,
                    onCheckedChange = { viewModel.setIsPublic(it) }
                )
            }
            
            // Background Selection with modern header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.small)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Image,
                    contentDescription = null,
                    modifier = Modifier.size(AppDimensions.iconSizeMedium),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = stringResource(R.string.background),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                )
            }
            
            Spacer(modifier = Modifier.height(AppSpacing.small))
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.medium)
            ) {
                itemsIndexed(viewModel.backgroundImages) { index, imageUrl ->
                    val isSelected = index == selectedBackgroundIndex
                    val scale by animateFloatAsState(
                        targetValue = if (isSelected) 1.05f else 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        ),
                        label = "backgroundScale"
                    )
                    
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .scale(scale)
                            .clip(RoundedCornerShape(AppDimensions.cornerRadiusMedium))
                            .border(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected) 
                                    MaterialTheme.colorScheme.primary 
                                else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(AppDimensions.cornerRadiusMedium)
                            )
                            .clickable { viewModel.setSelectedBackgroundIndex(index) }
                    ) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = "Background $index",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.CheckCircle,
                                    contentDescription = "Selected",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(AppDimensions.iconSizeLarge)
                                )
                            }
                        }
                    }
                }
            }
            
            // Error message with animation
            AnimatedVisibility(
                visible = error != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(AppDimensions.cornerRadiusMedium),
                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
                ) {
                    Row(
                        modifier = Modifier.padding(AppSpacing.medium),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AppSpacing.small)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ErrorOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(AppDimensions.iconSizeMedium)
                        )
                        Text(
                            text = error ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(AppSpacing.large))
            
            // Create Button with modern design
            Button(
                onClick = { viewModel.createEvent() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AppDimensions.buttonHeightLarge),
                enabled = !isLoading && name.isNotBlank() && location.isNotBlank(),
                shape = RoundedCornerShape(AppDimensions.cornerRadiusLarge)
            ) {
                if (isLoading) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(AppDimensions.iconSizeMedium),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(AppSpacing.medium))
                        Text(
                            text = "Creating...",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                } else {
                    Text(
                        text = stringResource(R.string.create_event),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
        
        // Date Picker Dialog
        if (showDatePicker) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = calendar.timeInMillis
            )
            
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selectedCalendar = Calendar.getInstance().apply {
                                timeInMillis = millis
                                set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY))
                                set(Calendar.MINUTE, calendar.get(Calendar.MINUTE))
                            }
                            viewModel.setDate(selectedCalendar.time)
                        }
                        showDatePicker = false
                        showTimePicker = true
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
        
        // Time Picker Dialog
        if (showTimePicker) {
            val timePickerState = rememberTimePickerState(
                initialHour = calendar.get(Calendar.HOUR_OF_DAY),
                initialMinute = calendar.get(Calendar.MINUTE),
                is24Hour = false
            )
            
            AlertDialog(
                onDismissRequest = { showTimePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        val selectedCalendar = Calendar.getInstance().apply {
                            time = date
                            set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                            set(Calendar.MINUTE, timePickerState.minute)
                        }
                        viewModel.setDate(selectedCalendar.time)
                        showTimePicker = false
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showTimePicker = false }) {
                        Text("Cancel")
                    }
                },
                text = {
                    TimePicker(state = timePickerState)
                }
            )
        }
        
        // Duration Picker Dialog
        if (showDurationPicker) {
            AlertDialog(
                onDismissRequest = { showDurationPicker = false },
                confirmButton = {
                    TextButton(onClick = { showDurationPicker = false }) {
                        Text("Close")
                    }
                },
                title = { Text(stringResource(R.string.duration)) },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                    ) {
                        durationOptions.forEach { (minutes, label) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.setDurationMinutes(minutes)
                                        showDurationPicker = false
                                    }
                                    .padding(vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(label)
                                if (minutes == durationMinutes) {
                                    Text(
                                        "✓",
                                        color = MaterialTheme.colorScheme.primary,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                            }
                        }
                    }
                }
            )
        }
        
        // Max Guests Picker Dialog
        if (showMaxGuestsPicker) {
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
                onDismissRequest = { showMaxGuestsPicker = false },
                confirmButton = {
                    TextButton(onClick = { showMaxGuestsPicker = false }) {
                        Text("Close")
                    }
                },
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
                                        viewModel.setMaxGuests(count)
                                        showMaxGuestsPicker = false
                                    }
                                    .padding(vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(label)
                                if (count == maxGuests) {
                                    Text(
                                        "✓",
                                        color = MaterialTheme.colorScheme.primary,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                            }
                        }
                    }
                }
            )
        }
        
        // Map Location Picker Dialog
        if (showMapPicker) {
            MapLocationPicker(
                initialLocation = if (latitude != null && longitude != null) {
                    LocationData(latitude!!, longitude!!, location)
                } else null,
                onLocationSelected = { locationData ->
                    viewModel.setLocation(locationData.address)
                    viewModel.setLocationCoordinates(locationData.latitude, locationData.longitude)
                },
                onDismiss = { showMapPicker = false }
            )
        }
    }
}


