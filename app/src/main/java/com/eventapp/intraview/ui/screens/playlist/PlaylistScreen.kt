package com.eventapp.intraview.ui.screens.playlist

import android.webkit.WebView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.eventapp.intraview.R
import com.eventapp.intraview.ui.components.EmptyState
import com.eventapp.intraview.ui.components.LoadingState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistScreen(
    eventId: String,
    onNavigateBack: () -> Unit,
    viewModel: PlaylistViewModel = hiltViewModel()
) {
    val event by viewModel.event.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val showAddDialog by viewModel.showAddDialog.collectAsState()
    val playlistUrl by viewModel.playlistUrl.collectAsState()
    
    LaunchedEffect(eventId) {
        viewModel.loadEvent(eventId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.playlists)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showAddDialog() }
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_playlist))
            }
        }
    ) { paddingValues ->
        when {
            isLoading && event == null -> {
                LoadingState()
            }
            event != null -> {
                if (event!!.playlistUrls.isEmpty()) {
                    EmptyState(message = "No playlists yet")
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(event!!.playlistUrls) { url ->
                            PlaylistItem(
                                url = url,
                                onDelete = { viewModel.removePlaylist(url) },
                                extractPlaylistId = { viewModel.extractPlaylistId(it) }
                            )
                        }
                    }
                }
            }
        }
    }
    
    // Add Playlist Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideAddDialog() },
            title = { Text(stringResource(R.string.add_playlist)) },
            text = {
                Column {
                    Text(
                        "Paste a YouTube playlist URL",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = playlistUrl,
                        onValueChange = { viewModel.setPlaylistUrl(it) },
                        label = { Text("Playlist URL") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.addPlaylist() }) {
                    Text(stringResource(R.string.add_playlist))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.hideAddDialog() }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
private fun PlaylistItem(
    url: String,
    onDelete: () -> Unit,
    extractPlaylistId: (String) -> String?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // YouTube Embed
            val playlistId = extractPlaylistId(url)
            if (playlistId != null) {
                AndroidView(
                    factory = { context ->
                        WebView(context).apply {
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            loadData("""
                                <html>
                                <body style="margin:0;padding:0;">
                                    <iframe 
                                        width="100%" 
                                        height="200" 
                                        src="https://www.youtube.com/embed/videoseries?list=$playlistId"
                                        frameborder="0" 
                                        allowfullscreen>
                                    </iframe>
                                </body>
                                </html>
                            """.trimIndent(), "text/html", "utf-8")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Invalid playlist URL")
                }
            }
            
            // Playlist Info and Actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "YouTube Playlist",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = url.take(40) + if (url.length > 40) "..." else "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = stringResource(R.string.delete),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}


