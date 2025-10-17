package com.eventapp.intraview.ui.screens.guest

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.eventapp.intraview.ui.theme.AppSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuestListScreen(
    eventId: String,
    onNavigateBack: () -> Unit,
    onGuestClick: (String) -> Unit,
    viewModel: GuestListViewModel = hiltViewModel()
) {
    val guests by viewModel.guests.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(eventId) {
        viewModel.loadGuests(eventId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Guest List (${guests.size})") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(AppSpacing.normal),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.medium)
            ) {
                items(guests) { guest ->
                    GuestListItem(
                        guest = guest,
                        onClick = { onGuestClick(guest.userId) }
                    )
                }
            }
        }
    }
}

@Composable
fun GuestListItem(
    guest: com.eventapp.intraview.data.model.Invitation,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppSpacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
            ) {
                AsyncImage(
                    model = guest.userPhotoUrl,
                    contentDescription = "Profile Picture",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                if (guest.checkedIn) {
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

            Spacer(modifier = Modifier.width(AppSpacing.medium))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = guest.userName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = guest.userEmail,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (guest.checkedIn) {
                    Text(
                        text = "✓ Checked In",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
