# Intra-View Architecture Documentation

## Overview

This document provides a detailed explanation of the architectural decisions, patterns, and design philosophy behind the Intra-View event management app.

## Architecture Pattern: Clean Architecture (Simplified)

```
┌─────────────────────────────────────────────────────────┐
│                     Presentation Layer                   │
│  ┌────────────────────────────────────────────────────┐ │
│  │  Screens (Composables)                             │ │
│  │  - Display UI                                      │ │
│  │  - Handle user interactions                        │ │
│  │  - Observe state from ViewModels                   │ │
│  └────────────────┬───────────────────────────────────┘ │
│                   │                                      │
│  ┌────────────────┴───────────────────────────────────┐ │
│  │  ViewModels                                        │ │
│  │  - Manage UI state                                 │ │
│  │  - Handle business logic                           │ │
│  │  - Coordinate repository calls                     │ │
│  └────────────────┬───────────────────────────────────┘ │
└───────────────────┼──────────────────────────────────────┘
                    │
┌───────────────────┼──────────────────────────────────────┐
│                   │         Domain Layer                 │
│  ┌────────────────┴───────────────────────────────────┐ │
│  │  Repositories                                      │ │
│  │  - Abstract data sources                           │ │
│  │  - Coordinate data operations                      │ │
│  │  - Transform data between layers                   │ │
│  └────────────────┬───────────────────────────────────┘ │
└───────────────────┼──────────────────────────────────────┘
                    │
┌───────────────────┼──────────────────────────────────────┐
│                   │          Data Layer                  │
│  ┌────────────────┴───────────────────────────────────┐ │
│  │  Firebase Services                                 │ │
│  │  - Firestore (Database)                            │ │
│  │  - Storage (Files)                                 │ │
│  │  - Auth (Authentication)                           │ │
│  └────────────────────────────────────────────────────┘ │
└──────────────────────────────────────────────────────────┘
```

## Layer Breakdown

### 1. Presentation Layer

**Components:**
- **Screens**: Jetpack Compose UI components
- **ViewModels**: State holders with business logic
- **Navigation**: Compose Navigation for screen routing

**Key Features:**
- Reactive UI using Compose
- State management with StateFlow
- Single source of truth for UI state

**Example Flow:**
```kotlin
// Screen observes ViewModel state
@Composable
fun EventDetailScreen(viewModel: EventDetailViewModel) {
    val event by viewModel.event.collectAsState()
    
    // UI automatically updates when state changes
    if (event != null) {
        EventContent(event!!)
    }
}

// ViewModel manages state
class EventDetailViewModel {
    private val _event = MutableStateFlow<Event?>(null)
    val event: StateFlow<Event?> = _event.asStateFlow()
    
    fun loadEvent(eventId: String) {
        viewModelScope.launch {
            _event.value = repository.getEvent(eventId)
        }
    }
}
```

### 2. Domain Layer (Repository Pattern)

**Components:**
- **Repositories**: Data access abstraction
- **Data Models**: Domain entities
- **Utilities**: Helper functions

**Key Features:**
- Single source of truth for data operations
- Error handling with Result sealed class
- Real-time data streaming with Flow

**Repository Pattern:**
```kotlin
interface EventRepository {
    suspend fun createEvent(...): Result<Event>
    suspend fun getEvent(eventId: String): Event?
    fun observeEvent(eventId: String): Flow<Event?>
}
```

### 3. Data Layer

**Components:**
- **Firebase Auth**: User authentication
- **Cloud Firestore**: NoSQL database
- **Cloud Storage**: File storage

**Key Features:**
- Real-time data synchronization
- Offline caching (built-in)
- Scalable infrastructure

## Key Design Patterns

### 1. Repository Pattern

**Purpose**: Abstracts data sources and provides a clean API

**Implementation:**
```kotlin
@Singleton
class EventRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    fun observeEvent(eventId: String): Flow<Event?> = callbackFlow {
        val listener = firestore.collection("events")
            .document(eventId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.toObject<Event>())
            }
        awaitClose { listener.remove() }
    }
}
```

**Benefits:**
- Testable (can mock repositories)
- Flexible (can swap data sources)
- Maintainable (single place for data logic)

### 2. MVVM (Model-View-ViewModel)

**Purpose**: Separates UI logic from business logic

**Flow:**
```
User Action → Screen → ViewModel → Repository → Firebase
                ↑                      ↓
                └──── StateFlow ←──────┘
```

**Example:**
```kotlin
// User clicks button
Button(onClick = { viewModel.createEvent() })

// ViewModel processes
fun createEvent() {
    viewModelScope.launch {
        _isLoading.value = true
        val result = repository.createEvent(...)
        _isLoading.value = false
        
        when (result) {
            is Result.Success -> navigate()
            is Result.Error -> showError()
        }
    }
}
```

### 3. Dependency Injection (Hilt)

**Purpose**: Manages object creation and dependencies

**Setup:**
```kotlin
@HiltAndroidApp
class EventApplication : Application()

@AndroidEntryPoint
class MainActivity : ComponentActivity()

@HiltViewModel
class EventViewModel @Inject constructor(
    private val repository: EventRepository
) : ViewModel()
```

**Benefits:**
- Automatic dependency resolution
- Singleton management
- Testability

## Data Flow Patterns

### 1. Real-time Data (Flow)

**Use Case**: Event details, photo gallery

```kotlin
// Repository exposes Flow
fun observeEvent(eventId: String): Flow<Event?>

// ViewModel collects and exposes as StateFlow
val event: StateFlow<Event?> = repository
    .observeEvent(eventId)
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

// UI automatically updates
val event by viewModel.event.collectAsState()
```

### 2. One-time Operations (Suspend Functions)

**Use Case**: Create event, upload photo

```kotlin
// Repository function
suspend fun createEvent(...): Result<Event> {
    return try {
        val event = ...
        firestore.collection("events").document().set(event).await()
        Result.Success(event)
    } catch (e: Exception) {
        Result.Error(e.message)
    }
}

// ViewModel calls in coroutine
viewModelScope.launch {
    val result = repository.createEvent(...)
}
```

## State Management

### StateFlow Pattern

**Why StateFlow?**
- Reactive (UI updates automatically)
- Lifecycle-aware (in Compose)
- Type-safe
- No memory leaks

**Implementation:**
```kotlin
// Private mutable state
private val _isLoading = MutableStateFlow(false)
// Public immutable state
val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

// Update state
_isLoading.value = true

// Observe in UI
val isLoading by viewModel.isLoading.collectAsState()
```

### Result Pattern

**Purpose**: Type-safe error handling

```kotlin
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

// Usage
when (result) {
    is Result.Success -> handleSuccess(result.data)
    is Result.Error -> showError(result.message)
    is Result.Loading -> showLoading()
}
```

## Firebase Integration

### 1. Authentication Flow

```
User opens app
    ↓
Check auth state
    ↓
Not authenticated → Login Screen → Google Sign-In
    ↓                                      ↓
Authenticated ← Create/Update User Doc ←─┘
    ↓
Home Screen
```

### 2. Firestore Data Model

```
/users/{userId}
  - email, displayName, photoUrl

/events/{eventId}
  - hostId, name, date, guestIds[], playlistUrls[]

/invitations/{invitationId}
  - eventId, userId, qrToken, checkedIn

/photos/{photoId}
  - eventId, userId, imageUrl
```

**Design Decisions:**
- Flat structure (no nested collections)
- Denormalized data (user info in photos)
- Array fields for lists (guestIds, playlistUrls)

### 3. Security Rules Strategy

```javascript
// Helper functions
function isAuthenticated() { ... }
function isEventHost(eventId) { ... }
function isEventGuest(eventId) { ... }

// Rules use helper functions
allow read: if isEventHost(eventId) || isEventGuest(eventId);
```

## Simplified Decisions

### What We Removed (For Simplicity)

1. **Use Cases Layer**: Combined into ViewModels
2. **Domain Models**: Using Firestore models directly
3. **Mappers**: Direct object conversion
4. **Complex State Machines**: Simple state enums
5. **Repository Interfaces**: Concrete implementations only

### Trade-offs Made

| Feature | Full Architecture | Our Approach | Reason |
|---------|------------------|--------------|--------|
| Use Cases | Separate layer | In ViewModels | Fewer files, simpler |
| Domain Models | Separate from data | Same as data | No transformation needed |
| Offline Support | Room + sync | Firestore cache | Built-in, simpler |
| Error Handling | Complex retry logic | Simple Result wrapper | Acceptable for scale |
| Testing | Full unit tests | Manual testing | Showcase, not production |

## Scalability Considerations

### Current Architecture Supports:

- **100-1000 users**: ✅ Current design
- **1000-5000 users**: ✅ Add pagination, thumbnails
- **5000-10000 users**: ⚠️ Need Cloud Functions, caching layer
- **10000+ users**: ❌ Requires redesign (microservices, CDN)

### Upgrade Path:

1. **Phase 1** (1000 users):
   - Add pagination
   - Implement thumbnails
   - Add basic caching

2. **Phase 2** (5000 users):
   - Cloud Functions for background tasks
   - Room database for offline
   - Push notifications

3. **Phase 3** (10000+ users):
   - Microservices architecture
   - Dedicated backend API
   - Advanced caching (Redis)

## Testing Strategy

### Current Approach (Manual Testing)

For a showcase project:
- Manual testing of all features
- Firebase Emulator for local testing
- Test accounts for different scenarios

### Production Approach (If Scaling)

```kotlin
// Unit Tests
class EventRepositoryTest {
    @Test
    fun `createEvent should return success`() { ... }
}

// UI Tests
class EventDetailScreenTest {
    @Test
    fun `should display event details`() { ... }
}
```

## Performance Optimizations

### 1. Lazy Loading

```kotlin
// Load images on demand
AsyncImage(model = photo.url, ...)
```

### 2. Pagination

```kotlin
// Load photos in batches (not yet implemented)
suspend fun getPhotos(limit: Int, lastVisible: Photo?)
```

### 3. Image Compression

```kotlin
// Compress before upload
val compressed = ImageCompressor.compressImage(uri, maxWidth = 1920)
```

## Error Handling Strategy

### 1. User-Facing Errors

```kotlin
when (result) {
    is Result.Error -> {
        // Show snackbar or error message
        showError(result.message)
    }
}
```

### 2. Silent Errors

```kotlin
// Log but don't show to user
catch (e: Exception) {
    Log.e(TAG, "Background operation failed", e)
}
```

### 3. Network Errors

```kotlin
// Show retry option
if (error != null) {
    ErrorState(
        message = error,
        onRetry = { reload() }
    )
}
```

## Conclusion

This architecture prioritizes:
1. **Simplicity**: Easy to understand and maintain
2. **Functionality**: All features work end-to-end
3. **Scalability**: Can grow to 5000+ users with incremental changes
4. **Developer Experience**: Fast development, clear structure

It's designed for a showcase project that demonstrates real-world Android development with modern tools and patterns, while being practical for actual deployment at small to medium scale.


