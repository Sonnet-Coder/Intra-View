package com.eventapp.intraview.ui.screens.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.eventapp.intraview.R
import com.eventapp.intraview.ui.components.EmptyState
import com.eventapp.intraview.ui.components.EventCard
import com.eventapp.intraview.ui.components.FloatingActionMenu
import com.eventapp.intraview.ui.components.LoadingState
import com.eventapp.intraview.ui.theme.AppDimensions
import com.eventapp.intraview.ui.theme.AppSpacing
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onEventClick: (String) -> Unit,
    onCreateEventClick: () -> Unit,
    onLogout: () -> Unit,
    onProfileClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val myEvents by viewModel.myEvents.collectAsState()
    val invitedEvents by viewModel.invitedEvents.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val showInviteDialog by viewModel.showInviteDialog.collectAsState()
    val inviteCode by viewModel.inviteCode.collectAsState()
    val isJoiningEvent by viewModel.isJoiningEvent.collectAsState()
    val error by viewModel.error.collectAsState()
    val showLogoutDialog by viewModel.showLogoutDialog.collectAsState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf(stringResource(R.string.hosting), stringResource(R.string.invited))
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.my_events)) },
                actions = {
                    IconButton(onClick = onProfileClick) {
                        Icon(Icons.Default.Person, contentDescription = "Profile")
                    }
                    IconButton(onClick = { viewModel.showLogoutDialog() }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = stringResource(R.string.sign_out))
                    }
                }
            )
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
            
            // Content with smooth transitions
            AnimatedContent(
                targetState = isLoading && myEvents.isEmpty() && invitedEvents.isEmpty(),
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith
                            fadeOut(animationSpec = tween(300))
                },
                label = "loadingContent"
            ) { loading ->
                if (loading) {
                    LoadingState()
                } else {
                    val events = if (selectedTab == 0) myEvents else invitedEvents
                    
                    AnimatedContent(
                        targetState = events.isEmpty(),
                        transitionSpec = {
                            fadeIn(animationSpec = tween(400)) togetherWith
                                    fadeOut(animationSpec = tween(200))
                        },
                        label = "emptyContent"
                    ) { isEmpty ->
                        if (isEmpty) {
                            EmptyState(
                                message = stringResource(R.string.no_events)
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(AppSpacing.normal),
                                verticalArrangement = Arrangement.spacedBy(AppSpacing.medium)
                            ) {
                                itemsIndexed(
                                    items = events,
                                    key = { _, event -> event.eventId }
                                ) { index, event ->
                                    var visible by remember { mutableStateOf(false) }
                                    
                                    LaunchedEffect(Unit) {
                                        kotlinx.coroutines.delay((index * 50L).coerceAtMost(300))
                                        visible = true
                                    }
                                    
                                    AnimatedVisibility(
                                        visible = visible,
                                        enter = fadeIn(
                                            animationSpec = tween(400)
                                        ) + slideInVertically(
                                            animationSpec = tween(400),
                                            initialOffsetY = { it / 4 }
                                        )
                                    ) {
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
        }
        
        // Floating Action Menu in bottom right corner
        FloatingActionMenu(
            onHostEventClick = onCreateEventClick,
            onJoinEventClick = { viewModel.showInviteDialog() },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(AppSpacing.normal)
        )
    }
    
    // Invite Code Dialog with modern styling
    if (showInviteDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideInviteDialog() },
            shape = RoundedCornerShape(AppDimensions.cornerRadiusLarge),
            title = { 
                Text(
                    text = stringResource(R.string.enter_invite_code),
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.medium)
                ) {
                    OutlinedTextField(
                        value = inviteCode,
                        onValueChange = { viewModel.setInviteCode(it) },
                        label = { Text("Invite Code") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isJoiningEvent,
                        shape = RoundedCornerShape(AppDimensions.cornerRadiusMedium)
                    )
                    
                    AnimatedVisibility(
                        visible = error != null,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        val isSuccess = error?.contains("sent", ignoreCase = true) == true || 
                                       error?.contains("approval", ignoreCase = true) == true
                        Surface(
                            shape = RoundedCornerShape(AppDimensions.cornerRadiusSmall),
                            color = if (isSuccess) {
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                            } else {
                                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
                            }
                        ) {
                            Text(
                                text = error ?: "",
                                color = if (isSuccess) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.error
                                },
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(AppSpacing.small)
                            )
                        }
                    }
                    
                    AnimatedVisibility(
                        visible = isJoiningEvent,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(AppSpacing.small))
                            Text(
                                text = "Joining event...",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            val event = viewModel.joinEventWithCode()
                            if (event != null) {
                                viewModel.hideInviteDialog()
                                onEventClick(event.eventId)
                            }
                        }
                    },
                    enabled = !isJoiningEvent && inviteCode.isNotBlank(),
                    shape = RoundedCornerShape(AppDimensions.cornerRadiusMedium)
                ) {
                    Text(stringResource(R.string.join_event))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.hideInviteDialog() },
                    enabled = !isJoiningEvent
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
    
    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideLogoutDialog() },
            shape = RoundedCornerShape(AppDimensions.cornerRadiusLarge),
            icon = {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            title = {
                Text(
                    text = stringResource(R.string.logout_confirmation_title),
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.logout_confirmation_message),
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            viewModel.signOut(context)
                            viewModel.hideLogoutDialog()
                            onLogout()
                        }
                    },
                    shape = RoundedCornerShape(AppDimensions.cornerRadiusMedium),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(stringResource(R.string.logout_confirm))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.hideLogoutDialog() }
                ) {
                    Text(stringResource(R.string.logout_cancel))
                }
            }
        )
    }
}


