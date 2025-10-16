package com.eventapp.intraview.ui.screens.guest

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.eventapp.intraview.ui.theme.AppSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuestDetailScreen(
    userId: String,
    onNavigateBack: () -> Unit,
    viewModel: GuestDetailViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(userId) {
        viewModel.loadUser(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Guest Details") },
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
            user?.let { guestUser ->
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
                    ) {
                        AsyncImage(
                            model = guestUser.photoUrl,
                            contentDescription = "Profile Picture",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(AppSpacing.large))

                    // Display Name
                    Text(
                        text = guestUser.displayName,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(AppSpacing.small))

                    // Email
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AppSpacing.small)
                    ) {
                        Icon(
                            Icons.Default.Email,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = guestUser.email,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(AppSpacing.large))

                    // Bio
                    if (guestUser.bio.isNotEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(AppSpacing.medium)
                            ) {
                                Text(
                                    text = "Bio",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(AppSpacing.small))
                                Text(
                                    text = guestUser.bio,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(AppSpacing.medium))
                    }

                    // Social Media
                    val hasSocials = guestUser.instagramHandle.isNotEmpty() ||
                            guestUser.twitterHandle.isNotEmpty() ||
                            guestUser.pinterestHandle.isNotEmpty() ||
                            guestUser.tiktokHandle.isNotEmpty() ||
                            guestUser.youtubeHandle.isNotEmpty()

                    if (hasSocials) {
                        Text(
                            text = "Social Media",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(AppSpacing.medium))

                        if (guestUser.instagramHandle.isNotEmpty()) {
                            SocialMediaCard(
                                icon = Icons.Default.PhotoCamera,
                                label = "Instagram",
                                handle = "@${guestUser.instagramHandle}",
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/${guestUser.instagramHandle}"))
                                    context.startActivity(intent)
                                }
                            )
                            Spacer(modifier = Modifier.height(AppSpacing.small))
                        }

                        if (guestUser.twitterHandle.isNotEmpty()) {
                            SocialMediaCard(
                                icon = Icons.Default.Tag,
                                label = "Twitter/X",
                                handle = "@${guestUser.twitterHandle}",
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/${guestUser.twitterHandle}"))
                                    context.startActivity(intent)
                                }
                            )
                            Spacer(modifier = Modifier.height(AppSpacing.small))
                        }

                        if (guestUser.pinterestHandle.isNotEmpty()) {
                            SocialMediaCard(
                                icon = Icons.Default.PushPin,
                                label = "Pinterest",
                                handle = "@${guestUser.pinterestHandle}",
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://pinterest.com/${guestUser.pinterestHandle}"))
                                    context.startActivity(intent)
                                }
                            )
                            Spacer(modifier = Modifier.height(AppSpacing.small))
                        }

                        if (guestUser.tiktokHandle.isNotEmpty()) {
                            SocialMediaCard(
                                icon = Icons.Default.VideoLibrary,
                                label = "TikTok",
                                handle = "@${guestUser.tiktokHandle}",
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://tiktok.com/@${guestUser.tiktokHandle}"))
                                    context.startActivity(intent)
                                }
                            )
                            Spacer(modifier = Modifier.height(AppSpacing.small))
                        }

                        if (guestUser.youtubeHandle.isNotEmpty()) {
                            SocialMediaCard(
                                icon = Icons.Default.VideoLibrary,
                                label = "YouTube",
                                handle = "@${guestUser.youtubeHandle}",
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://youtube.com/@${guestUser.youtubeHandle}"))
                                    context.startActivity(intent)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SocialMediaCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    handle: String,
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
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(AppSpacing.medium))
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = handle,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                Icons.Default.OpenInNew,
                contentDescription = "Open",
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
