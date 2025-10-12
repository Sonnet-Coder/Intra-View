package com.eventapp.intraview.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.eventapp.intraview.R
import com.eventapp.intraview.ui.components.EmptyState
import com.eventapp.intraview.ui.components.EventCard
import com.eventapp.intraview.ui.components.LoadingState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onEventClick: (String) -> Unit,
    onCreateEventClick: () -> Unit,
    onLogout: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val myEvents by viewModel.myEvents.collectAsState()
    val invitedEvents by viewModel.invitedEvents.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val showInviteDialog by viewModel.showInviteDialog.collectAsState()
    val inviteCode by viewModel.inviteCode.collectAsState()
    val scope = rememberCoroutineScope()
    
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf(stringResource(R.string.hosting), stringResource(R.string.invited))
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.my_events)) },
                actions = {
                    IconButton(onClick = onCreateEventClick) {
                        Icon(Icons.Default.Add, contentDescription = stringResource(R.string.create_event))
                    }
                    IconButton(onClick = {
                        scope.launch {
                            viewModel.signOut()
                            onLogout()
                        }
                    }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = stringResource(R.string.sign_out))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showInviteDialog() }
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.join_event))
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tabs
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }
            
            // Content
            when {
                isLoading && myEvents.isEmpty() && invitedEvents.isEmpty() -> {
                    LoadingState()
                }
                else -> {
                    val events = if (selectedTab == 0) myEvents else invitedEvents
                    
                    if (events.isEmpty()) {
                        EmptyState(
                            message = stringResource(R.string.no_events)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(events) { event ->
                                EventCard(
                                    event = event,
                                    onClick = { onEventClick(event.eventId) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    
    // Invite Code Dialog
    if (showInviteDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideInviteDialog() },
            title = { Text(stringResource(R.string.enter_invite_code)) },
            text = {
                OutlinedTextField(
                    value = inviteCode,
                    onValueChange = { viewModel.setInviteCode(it) },
                    label = { Text("Invite Code") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            val event = viewModel.joinEventWithCode()
                            if (event != null) {
                                viewModel.hideInviteDialog()
                                onEventClick(event.eventId)
                            }
                        }
                    }
                ) {
                    Text(stringResource(R.string.join_event))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.hideInviteDialog() }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}


