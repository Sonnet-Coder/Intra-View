# Event Settings Toggle Fix

## Issues Fixed

### 1. Firestore Boolean Field Mapping Issue
**Problem:** Firestore's `CustomClassMapper` was unable to properly serialize/deserialize boolean fields that start with `is` prefix (`isPublic`, `isCancelled`). This caused warnings and the toggles not to work properly.

**Root Cause:** Kotlin automatically strips the `is` prefix from boolean properties when generating getters/setters. So `isPublic` becomes `getPublic()` and `setPublic()`, causing Firestore to look for fields named `public` and `cancelled` instead of `isPublic` and `isCancelled`.

**Solution:** Added `@PropertyName` annotations to explicitly tell Firestore to use the full property names:
```kotlin
@get:PropertyName("isPublic")
val isPublic: Boolean = false

@get:PropertyName("isCancelled")
val isCancelled: Boolean = false
```

### 2. Toggle State Logic Issue
**Problem:** The shared album and music playlist toggles in the Event Settings dialog were not working properly. The checked state was based on `isNotEmpty()` instead of null checking.

**Root Cause:** 
- The toggle state checked `event.sharedAlbumUrl?.isNotEmpty() == true` instead of `event.sharedAlbumUrl != null`
- The toggle callbacks only handled turning OFF (setting to null) but not turning ON (prompting for URL input)

**Solution:**
1. Changed toggle state check from `isNotEmpty()` to null check:
   ```kotlin
   checked = event.sharedAlbumUrl != null
   checked = event.musicPlaylistUrl != null
   ```

2. Enhanced toggle callbacks to handle both ON and OFF states:
   - **Toggle OFF:** Set field to null (hide from guests)
   - **Toggle ON:** Show a dialog to input/edit the URL

## Files Modified

1. **`app/src/main/java/com/eventapp/intraview/data/model/Event.kt`**
   - Added `@PropertyName` import
   - Added annotations to `isPublic` and `isCancelled` fields

2. **`app/src/main/java/com/eventapp/intraview/ui/components/EventSettingsDialog.kt`**
   - Changed toggle state check from `isNotEmpty()` to null check

3. **`app/src/main/java/com/eventapp/intraview/ui/screens/event/EventDetailScreen.kt`**
   - Enhanced toggle callbacks to handle both ON and OFF states
   - Added dialogs for URL input when toggling shared album or playlist ON

## How It Works Now

### Shared Album Toggle:
- **OFF → ON:** Shows dialog to enter shared album URL
- **ON → OFF:** Removes the URL (sets to null), hiding it from guests

### Music Playlist Toggle:
- **OFF → ON:** Shows dialog to enter playlist URL  
- **ON → OFF:** Removes the URL (sets to null), hiding it from guests

### Public Event Toggle:
- Directly toggles between public and private
- No URL input needed

## Testing
After installing the updated APK:
1. Open an event you're hosting
2. Tap the Settings button (top right)
3. Try toggling Public Event, Shared Album, and Music Playlist
4. All toggles should now work correctly
5. When toggling album/playlist ON, you'll be prompted to enter a URL
6. When toggling OFF, the feature will be hidden from guests
7. No more Firestore warnings in logcat

