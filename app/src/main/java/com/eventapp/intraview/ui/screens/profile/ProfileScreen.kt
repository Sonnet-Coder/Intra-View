package com.eventapp.intraview.ui.screens.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    val error by viewModel.error.collectAsState()
    val isEditMode by viewModel.isEditMode.collectAsState()

    var displayName by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var instagramHandle by remember { mutableStateOf("") }
    var twitterHandle by remember { mutableStateOf("") }
    var pinterestHandle by remember { mutableStateOf("") }
    var tiktokHandle by remember { mutableStateOf("") }
    var youtubeHandle by remember { mutableStateOf("") }

    LaunchedEffect(user) {
        user?.let {
            displayName = it.displayName
            bio = it.bio
            instagramHandle = it.instagramHandle
            twitterHandle = it.twitterHandle
            pinterestHandle = it.pinterestHandle
            tiktokHandle = it.tiktokHandle
            youtubeHandle = it.youtubeHandle
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.uploadProfileImage(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (isEditMode) {
                        IconButton(
                            onClick = {
                                user?.let {
                                    viewModel.updateUser(
                                        it.copy(
                                            displayName = displayName,
                                            bio = bio,
                                            instagramHandle = instagramHandle,
                                            twitterHandle = twitterHandle,
                                            pinterestHandle = pinterestHandle,
                                            tiktokHandle = tiktokHandle,
                                            youtubeHandle = youtubeHandle
                                        )
                                    )
                                    viewModel.saveProfile()
                                }
                            },
                            enabled = !isSaving
                        ) {
                            if (isSaving) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(Icons.Default.Check, contentDescription = "Save")
                            }
                        }
                    } else {
                        IconButton(onClick = { viewModel.toggleEditMode() }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading && user == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(AppSpacing.normal),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Picture
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .clickable(enabled = isEditMode) {
                            imagePickerLauncher.launch("image/*")
                        }
                ) {
                    AsyncImage(
                        model = user?.photoUrl,
                        contentDescription = "Profile Picture",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    if (isEditMode) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoCamera,
                                contentDescription = "Change Photo",
                                modifier = Modifier.size(32.dp),
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(AppSpacing.large))

                // Display Name
                ProfileTextField(
                    value = displayName,
                    onValueChange = { displayName = it },
                    label = "Full Name",
                    icon = Icons.Default.Person,
                    enabled = isEditMode
                )

                Spacer(modifier = Modifier.height(AppSpacing.medium))

                // Email (read-only)
                ProfileTextField(
                    value = user?.email ?: "",
                    onValueChange = {},
                    label = "Email",
                    icon = Icons.Default.Email,
                    enabled = false
                )

                Spacer(modifier = Modifier.height(AppSpacing.medium))

                // Bio
                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Bio") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isEditMode,
                    minLines = 3,
                    maxLines = 5,
                    leadingIcon = {
                        Icon(Icons.Default.Description, contentDescription = null)
                    }
                )

                Spacer(modifier = Modifier.height(AppSpacing.large))

                // Social Media Section
                Text(
                    text = "Social Media",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(AppSpacing.medium))

                ProfileTextField(
                    value = instagramHandle,
                    onValueChange = { instagramHandle = it },
                    label = "Instagram",
                    icon = Icons.Default.PhotoCamera,
                    enabled = isEditMode,
                    prefix = "@"
                )

                Spacer(modifier = Modifier.height(AppSpacing.small))

                ProfileTextField(
                    value = twitterHandle,
                    onValueChange = { twitterHandle = it },
                    label = "Twitter/X",
                    icon = Icons.Default.Tag,
                    enabled = isEditMode,
                    prefix = "@"
                )

                Spacer(modifier = Modifier.height(AppSpacing.small))

                ProfileTextField(
                    value = pinterestHandle,
                    onValueChange = { pinterestHandle = it },
                    label = "Pinterest",
                    icon = Icons.Default.PushPin,
                    enabled = isEditMode,
                    prefix = "@"
                )

                Spacer(modifier = Modifier.height(AppSpacing.small))

                ProfileTextField(
                    value = tiktokHandle,
                    onValueChange = { tiktokHandle = it },
                    label = "TikTok",
                    icon = Icons.Default.VideoLibrary,
                    enabled = isEditMode,
                    prefix = "@"
                )

                Spacer(modifier = Modifier.height(AppSpacing.small))

                ProfileTextField(
                    value = youtubeHandle,
                    onValueChange = { youtubeHandle = it },
                    label = "YouTube",
                    icon = Icons.Default.VideoLibrary,
                    enabled = isEditMode,
                    prefix = "@"
                )

                Spacer(modifier = Modifier.height(AppSpacing.large))

                // Error Message
                error?.let { errorMessage ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(AppSpacing.medium),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = errorMessage,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = { viewModel.clearError() }) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Dismiss",
                                    tint = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    enabled: Boolean,
    prefix: String = ""
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        enabled = enabled,
        singleLine = true,
        leadingIcon = {
            Icon(icon, contentDescription = null)
        },
        prefix = if (prefix.isNotEmpty()) {
            { Text(prefix) }
        } else null
    )
}
