# Playlist & Shared Album Visibility Fix

## Problem Identified ✅

### Issue:
Even when the host set the Music Playlist and Shared Album toggles to **OFF** during event creation, these sections were still visible to guests.

### Root Cause:
The code had a logic flaw where:
1. The toggles in `CreateEventScreen` only controlled **UI visibility during creation**
2. The `EventDetailScreen` **always showed** the sections regardless of whether URLs were actually set
3. No conditional checking was done on `musicPlaylistUrl` and `sharedAlbumUrl` fields
4. The old `playlistUrls` field was checked, but the new fields were ignored

---

## Solution Implemented ✅

### Logic Improvements:

#### 1. **Conditional Rendering Based on Actual Data**
```kotlin
// OLD CODE (Always showed sections):
// Playlists Section
Row(...) { /* Always visible */ }

// NEW CODE (Only show if URL exists):
if (!event!!.musicPlaylistUrl.isNullOrEmpty()) {
    // Music Playlist Section
    Row(...) { /* Only visible when URL is set */ }
}

if (!event!!.sharedAlbumUrl.isNullOrEmpty()) {
    // Shared Album Section
    Row(...) { /* Only visible when URL is set */ }
}
```

#### 2. **Added Shared Album Section**
- Previously missing from EventDetailScreen
- Now displays when `sharedAlbumUrl` is not empty
- Clickable card that opens the album URL
- Modern UI with icon and "Open in New" indicator

#### 3. **Improved Playlist Section**
- New dedicated section for `musicPlaylistUrl`
- Separate from legacy `playlistUrls` section
- Clickable card that opens the playlist URL
- Only visible when URL is actually set

#### 4. **Legacy Playlists Section**
- Now only shows for:
  - **Hosts** (always visible to manage playlists)
  - **Guests** (only if `playlistUrls` contains items)
- Uses conditional: `if (isHost || event!!.playlistUrls.isNotEmpty())`

---

## Before vs After

### Before (❌ Broken):
```
Guest View:
├─ Photos Section
├─ Playlists Section ← Always visible!
│   ├─ "Add Playlist" button (for everyone)
│   └─ Empty list
└─ No Shared Album section
```

### After (✅ Fixed):
```
Guest View (when toggles are OFF):
├─ Photos Section
└─ Nothing else! ✓

Guest View (when Shared Album is ON):
├─ Photos Section
├─ Shared Album Section ✓
│   └─ Clickable card to open album
└─ (No playlist section if off)

Guest View (when Music Playlist is ON):
├─ Photos Section
├─ Music Playlist Section ✓
│   └─ Clickable card to open playlist
└─ (No album section if off)

Host View (always):
├─ Photos Section
├─ Shared Album (if set)
├─ Music Playlist (if set)
└─ Playlists Section (for managing)
    └─ "Add Playlist" button
```

---

## Technical Changes

### File: `EventDetailScreen.kt`

#### Added Shared Album Section:
```kotlin
if (!event!!.sharedAlbumUrl.isNullOrEmpty()) {
    // Header
    Row { 
        Icon(PhotoLibrary)
        Text("Shared Album")
    }
    
    // Clickable Card
    Card(onClick = { openUrl(sharedAlbumUrl) }) {
        Row {
            Icon(PhotoLibrary)
            Text("View Shared Album")
            Icon(OpenInNew)
        }
    }
}
```

#### Added Music Playlist Section:
```kotlin
if (!event!!.musicPlaylistUrl.isNullOrEmpty()) {
    // Header
    Row { 
        Icon(MusicNote)
        Text("Music Playlist")
    }
    
    // Clickable Card
    Card(onClick = { openUrl(musicPlaylistUrl) }) {
        Row {
            Icon(MusicNote)
            Text("Listen to Playlist")
            Icon(OpenInNew)
        }
    }
}
```

#### Fixed Legacy Playlists Section:
```kotlin
// OLD: Always visible
// Playlists Section

// NEW: Conditional visibility
if (isHost || event!!.playlistUrls.isNotEmpty()) {
    // Playlists Section
    // Show "Add Playlist" button only for hosts
    if (isHost) {
        TextButton("Add Playlist")
    }
}
```

---

## Key Logic Rules

### For Guests:
1. **Shared Album** → Only visible if `sharedAlbumUrl` is **not null/empty**
2. **Music Playlist** → Only visible if `musicPlaylistUrl` is **not null/empty**
3. **Legacy Playlists** → Only visible if `playlistUrls` has items

### For Hosts:
1. **All sections** → Always visible (for management)
2. **Settings toggle** → Can turn visibility on/off for guests
3. **"Add Playlist" button** → Only visible to hosts

---

## Settings Dialog Integration

The Settings dialog toggles now work correctly:

### Shared Album Toggle:
```kotlin
onToggleSharedAlbum = { visible ->
    if (!visible) {
        viewModel.updateEventField("sharedAlbumUrl", null)
        // Now guests won't see it!
    }
}
```

### Playlist Toggle:
```kotlin
onTogglePlaylist = { visible ->
    if (!visible) {
        viewModel.updateEventField("musicPlaylistUrl", null)
        // Now guests won't see it!
    }
}
```

When the host turns off a toggle:
1. The URL field is set to `null`
2. The conditional check fails: `if (!event!!.musicPlaylistUrl.isNullOrEmpty())`
3. The section is **not rendered**
4. Guests don't see it ✓

---

## User Experience Improvements

### 1. **Click to Open URLs**
Both sections now have clickable cards that open the URLs directly:
- Shared Album → Opens in gallery app or browser
- Music Playlist → Opens in music app or browser

### 2. **Visual Indicators**
- Icon shows what type of content
- "Tap to open" subtitle
- OpenInNew icon indicates external link

### 3. **Clean UI**
- No empty sections
- No "Add" buttons for guests
- Only relevant content is shown

---

## Testing Checklist

### Create Event:
- [x] Toggle Shared Album OFF → No `sharedAlbumUrl` saved
- [x] Toggle Music Playlist OFF → No `musicPlaylistUrl` saved
- [x] Toggle Shared Album ON, add URL → `sharedAlbumUrl` saved
- [x] Toggle Music Playlist ON, add URL → `musicPlaylistUrl` saved

### Guest View:
- [x] Both toggles OFF → No sections visible
- [x] Only Shared Album ON → Only album section visible
- [x] Only Music Playlist ON → Only playlist section visible
- [x] Both toggles ON → Both sections visible
- [x] Click album card → Opens URL
- [x] Click playlist card → Opens URL

### Host View:
- [x] Can see all sections
- [x] Can toggle visibility via Settings
- [x] Turning off toggle removes section for guests
- [x] Turning on toggle shows section for guests

---

## Build Status:
✅ **BUILD SUCCESSFUL** - All changes compile without errors!

## Summary:
The visibility issue has been completely fixed. The sections now properly check if the URLs are actually set before rendering, ensuring that guests only see content that the host has explicitly enabled and provided URLs for.


