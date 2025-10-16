package com.eventapp.intraview.ui.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.eventapp.intraview.ui.screens.auth.AuthViewModel
import com.eventapp.intraview.ui.screens.auth.LoginScreen
import com.eventapp.intraview.ui.screens.event.CreateEventScreen
import com.eventapp.intraview.ui.screens.event.EventDetailScreen
import com.eventapp.intraview.ui.screens.home.HomeScreen
import com.eventapp.intraview.ui.screens.photo.PhotoGalleryScreen
import com.eventapp.intraview.ui.screens.playlist.PlaylistScreen
import com.eventapp.intraview.ui.screens.qr.QRDisplayScreen
import com.eventapp.intraview.ui.screens.qr.QRScannerScreen

@Composable
fun NavGraph(
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()
    
    val startDestination = if (isAuthenticated) {
        Log.d("NavGraph", "Starting at Home (authenticated)")
        Routes.Home.route
    } else {
        Log.d("NavGraph", "Starting at Login (not authenticated)")
        Routes.Login.route
    }
    
    // Observe auth state and navigate accordingly
    LaunchedEffect(isAuthenticated) {
        Log.d("NavGraph", "Auth state changed in LaunchedEffect: isAuthenticated=$isAuthenticated, currentRoute=${navController.currentDestination?.route}")
        if (isAuthenticated) {
            if (navController.currentDestination?.route == Routes.Login.route) {
                Log.d("NavGraph", "Navigating from Login to Home")
                navController.navigate(Routes.Home.route) {
                    popUpTo(Routes.Login.route) { inclusive = true }
                }
            }
        }
    }
    
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.Login.route) {
            Log.d("NavGraph", "Composing LoginScreen")
            LoginScreen(
                onLoginSuccess = {
                    Log.d("NavGraph", "onLoginSuccess callback triggered")
                    navController.navigate(Routes.Home.route) {
                        popUpTo(Routes.Login.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Routes.Home.route) {
            HomeScreen(
                onEventClick = { eventId ->
                    navController.navigate(Routes.EventDetail.createRoute(eventId))
                },
                onCreateEventClick = {
                    navController.navigate(Routes.CreateEvent.route)
                },
                onLogout = {
                    navController.navigate(Routes.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onProfileClick = {
                    navController.navigate(Routes.Profile.route)
                }
            )
        }
        
        composable(Routes.CreateEvent.route) {
            CreateEventScreen(
                onEventCreated = { eventId ->
                    navController.navigate(Routes.EventDetail.createRoute(eventId)) {
                        popUpTo(Routes.Home.route)
                    }
                },
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }
        
        composable(
            route = Routes.EventDetail.route,
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: return@composable
            EventDetailScreen(
                eventId = eventId,
                onNavigateBack = { navController.navigateUp() },
                onNavigateToPhotos = { navController.navigate(Routes.PhotoGallery.createRoute(eventId)) },
                onNavigateToQR = { navController.navigate(Routes.QRDisplay.createRoute(eventId)) },
                onNavigateToScanner = { navController.navigate(Routes.QRScanner.createRoute(eventId)) },
                onNavigateToPlaylist = { navController.navigate(Routes.Playlist.createRoute(eventId)) },
                onNavigateToGuestList = { navController.navigate(Routes.GuestList.createRoute(eventId)) }
            )
        }
        
        composable(
            route = Routes.PhotoGallery.route,
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: return@composable
            PhotoGalleryScreen(
                eventId = eventId,
                onNavigateBack = { navController.navigateUp() }
            )
        }
        
        composable(
            route = Routes.QRDisplay.route,
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: return@composable
            QRDisplayScreen(
                eventId = eventId,
                onNavigateBack = { navController.navigateUp() }
            )
        }
        
        composable(
            route = Routes.QRScanner.route,
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: return@composable
            QRScannerScreen(
                eventId = eventId,
                onNavigateBack = { navController.navigateUp() }
            )
        }
        
        composable(
            route = Routes.Playlist.route,
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: return@composable
            PlaylistScreen(
                eventId = eventId,
                onNavigateBack = { navController.navigateUp() }
            )
        }
        
        composable(Routes.Profile.route) {
            com.eventapp.intraview.ui.screens.profile.ProfileScreen(
                onNavigateBack = { navController.navigateUp() }
            )
        }
        
        composable(
            route = Routes.GuestList.route,
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: return@composable
            com.eventapp.intraview.ui.screens.guest.GuestListScreen(
                eventId = eventId,
                onNavigateBack = { navController.navigateUp() },
                onGuestClick = { userId ->
                    navController.navigate(Routes.GuestDetail.createRoute(userId))
                }
            )
        }
        
        composable(
            route = Routes.GuestDetail.route,
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
            com.eventapp.intraview.ui.screens.guest.GuestDetailScreen(
                userId = userId,
                onNavigateBack = { navController.navigateUp() }
            )
        }
    }
}


