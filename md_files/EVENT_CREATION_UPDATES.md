# Event Creation Feature Updates

## Summary
Comprehensive update to the event creation and management system with new features including Google Maps integration, optional fields, guest limits, and public/private events.

## Changes Implemented

### 1. **Google Maps Integration** ✅
- Added Google Maps dependencies to `build.gradle.kts`
- Created `MapLocationPicker` composable for interactive map-based location selection
- Created `CompactMapView` for displaying small map previews
- Added location permissions to `AndroidManifest.xml`
- Integrated map picker button in location field
- Event cards now show small map view in top-right corner when coordinates are available

**Note:** You need to add your Google Maps API key in `AndroidManifest.xml`:
```xml
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="YOUR_GOOGLE_MAPS_API_KEY" />
```

### 2. **Optional Music Playlist & Shared Album** ✅
- Added `musicPlaylistUrl` field (optional) to Event model
- Added `sharedAlbumUrl` field (optional) to Event model
- Created input fields in CreateEventScreen with appropriate icons
- Users can add Spotify, Apple Music, or other playlist URLs
- Users can add Google Photos, iCloud, or other shared album URLs

### 3. **Maximum Guest Limit** ✅
- Added `maxGuests` field to Event model (nullable for "No Limit")
- Created guest limit picker with predefined options:
  - No Limit
  - 10, 25, 50, 100, 200, 500 guests
- Integrated into CreateEventScreen with dropdown selector

### 4. **Public/Private Events** ✅
- Added `isPublic` boolean field to Event model
- Created elegant toggle switch in CreateEventScreen
- Public events will be visible on discover page (to be implemented later)
- Private events are invite-only
- Visual indicators with lock/public icons

### 5. **Floating Action Menu** ✅
- Created `FloatingActionMenu` component with expandable options
- Moved from top-right "+" button to bottom-right FAB menu
- Two options appear above the FAB:
  - **Host Event** - Opens create event screen
  - **Join Event** - Opens invite code dialog
- Smooth animations with spring physics
- Scrim overlay when expanded
- Matches reference design provided

### 6. **Updated Data Models** ✅

#### Event Model
```kotlin
data class Event(
    // ... existing fields ...
    val latitude: Double? = null,
    val longitude: Double? = null,
    val musicPlaylistUrl: String? = null,
    val sharedAlbumUrl: String? = null,
    val maxGuests: Int? = null,
    val isPublic: Boolean = false
)
```

### 7. **Updated Repository** ✅
- Updated `EventRepository.createEvent()` to accept all new parameters
- All new fields are properly saved to Firestore

### 8. **Updated ViewModel** ✅
- Added state flows for all new fields
- Added setter methods for each field
- Updated validation logic

### 9. **UI Enhancements** ✅
- All new fields use Material Design 3 components
- Consistent icon usage throughout
- Proper color theming
- Smooth animations and transitions
- Responsive layouts

## New Components Created

1. **FloatingActionMenu.kt** - Expandable FAB with Host/Join options
2. **MapLocationPicker.kt** - Full-screen map for location selection
3. **CompactMapView** - Small map preview for event cards

## Files Modified

1. `app/build.gradle.kts` - Added Google Maps dependencies
2. `app/src/main/AndroidManifest.xml` - Added location permissions and Maps API key
3. `app/src/main/java/com/eventapp/intraview/data/model/Event.kt` - Added new fields
4. `app/src/main/java/com/eventapp/intraview/data/repository/EventRepository.kt` - Updated createEvent method
5. `app/src/main/java/com/eventapp/intraview/ui/screens/event/CreateEventViewModel.kt` - Added new state and methods
6. `app/src/main/java/com/eventapp/intraview/ui/screens/event/CreateEventScreen.kt` - Added all new UI fields
7. `app/src/main/java/com/eventapp/intraview/ui/screens/home/HomeScreen.kt` - Integrated FloatingActionMenu
8. `app/src/main/java/com/eventapp/intraview/ui/components/EventCard.kt` - Added map preview
9. `app/src/main/res/values/strings.xml` - Added new string resources

## Setup Instructions

### 1. Get Google Maps API Key
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select existing
3. Enable "Maps SDK for Android"
4. Create credentials (API Key)
5. Restrict the key to Android apps
6. Add your package name and SHA-1 certificate fingerprint

### 2. Add API Key
Replace `YOUR_GOOGLE_MAPS_API_KEY` in `AndroidManifest.xml` with your actual API key.

### 3. Sync and Build
```bash
./gradlew clean
./gradlew assembleDebug
```

## Features in Action

### Create Event Flow
1. User taps bottom-right FAB
2. Menu expands with "Host Event" and "Join Event" options
3. User taps "Host Event"
4. CreateEventScreen opens with all fields:
   - Event Name (required)
   - Description (required)
   - Date & Time (required)
   - Location with map picker button (required)
   - Duration selector (required)
   - Maximum Guests selector (optional - defaults to "No Limit")
   - Music Playlist URL (optional)
   - Shared Album URL (optional)
   - Public/Private toggle (defaults to Private)
   - Background image selector (required)
5. User can tap map icon to pick location on Google Maps
6. User fills in optional fields as needed
7. User taps "Create Event" button
8. Event is created with all data saved to Firestore

### Event Card Display
- Shows event background image
- Displays event name, date, and guest count
- If location coordinates exist, shows small interactive map in top-right corner
- Smooth press animations

### Join Event Flow
1. User taps bottom-right FAB
2. Menu expands
3. User taps "Join Event"
4. Invite code dialog appears
5. User enters code and joins event

## Future Enhancements (Not Implemented Yet)

- **Discover Page**: Public events will appear here
- **Geocoding**: Convert addresses to coordinates automatically
- **Map Clustering**: Show multiple events on a map
- **Guest Limit Enforcement**: Prevent joining when limit reached
- **Playlist Integration**: Direct integration with Spotify/Apple Music APIs
- **Album Integration**: Direct integration with Google Photos/iCloud

## Testing Checklist

- [ ] Create event with all fields filled
- [ ] Create event with only required fields
- [ ] Pick location using map picker
- [ ] Toggle between public and private
- [ ] Select different guest limits including "No Limit"
- [ ] Add music playlist URL
- [ ] Add shared album URL
- [ ] Verify map shows on event card when coordinates exist
- [ ] Test FAB menu expand/collapse
- [ ] Test "Host Event" option
- [ ] Test "Join Event" option
- [ ] Verify all data saves to Firestore correctly

## Notes

- All optional fields are nullable in the database
- Location coordinates are only saved if user picks location from map
- Public/Private toggle defaults to Private for security
- Maximum guests defaults to "No Limit" (null)
- The FAB menu uses a scrim overlay for better UX
- All animations use Material Design motion principles

