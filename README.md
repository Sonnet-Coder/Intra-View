# Intra-View - Simplified Android Event Management App

A clean architecture Android event management application built with Jetpack Compose and Firebase, designed for 100-1000 users.

## Features

- **Event Management**: Create and manage events with custom backgrounds
- **Guest Invitations**: Share invite codes to add guests to events
- **Photo Sharing**: Upload and view event photos in real-time
- **QR Check-in**: Display QR codes for guests and scan them for check-in
- **Music Playlists**: Add and manage YouTube playlists for events
- **Real-time Updates**: Firebase Firestore for instant synchronization

## Tech Stack

### Core
- **Language**: Kotlin
- **UI**: Jetpack Compose with Material 3
- **Architecture**: Clean Architecture (Repository Pattern)
- **Dependency Injection**: Hilt

### Firebase Services
- **Authentication**: Google Sign-In
- **Database**: Cloud Firestore
- **Storage**: Cloud Storage for photos
- **Dynamic Links**: (Optional) For invitation deep links

### Key Libraries
- **Navigation**: Jetpack Navigation Compose
- **Image Loading**: Coil
- **QR Code**: ML Kit Barcode Scanning, ZXing
- **Camera**: CameraX
- **Coroutines**: Kotlin Coroutines & Flow

## Project Structure

```
app/src/main/java/com/eventapp/intraview/
├── EventApplication.kt
├── MainActivity.kt
│
├── data/
│   ├── model/                  # Data classes (User, Event, Invitation, Photo)
│   └── repository/             # Repository layer (Auth, Event, Invitation, Photo)
│
├── di/                         # Dependency injection modules
│   ├── AppModule.kt
│   └── FirebaseModule.kt
│
├── ui/
│   ├── theme/                  # App theme (Color, Type, Theme)
│   ├── components/             # Reusable UI components
│   ├── navigation/             # Navigation setup
│   └── screens/                # Feature screens
│       ├── auth/               # Login screen
│       ├── home/               # Home screen
│       ├── event/              # Create & Detail screens
│       ├── invitation/         # Invitation preview
│       ├── photo/              # Photo gallery
│       ├── qr/                 # QR display & scanner
│       └── playlist/           # Playlist management
│
└── util/                       # Utility classes
    ├── Constants.kt
    ├── DateFormatter.kt
    ├── ImageCompressor.kt
    ├── InviteCodeGenerator.kt
    └── Result.kt
```

## Setup Instructions

### 1. Firebase Configuration

1. Create a new Firebase project at [Firebase Console](https://console.firebase.google.com/)

2. Enable the following services:
   - **Authentication**: Enable Google Sign-In
   - **Cloud Firestore**: Create database in production mode
   - **Cloud Storage**: Enable storage bucket
   - **(Optional) Dynamic Links**: For invitation deep links

3. Download `google-services.json` from Firebase Console:
   - Go to Project Settings → Your Apps → Android app
   - Click "Download google-services.json"
   - Place it in `app/` directory

4. Get Web Client ID for Google Sign-In:
   - Go to Firebase Console → Authentication → Sign-in method → Google
   - Copy the "Web client ID"
   - Replace `YOUR_WEB_CLIENT_ID` in `AuthRepository.kt` line 93

### 2. Firestore Security Rules

Deploy the security rules to Firebase:

```bash
# Install Firebase CLI
npm install -g firebase-tools

# Login to Firebase
firebase login

# Initialize Firebase in project
firebase init firestore

# Deploy rules
firebase deploy --only firestore:rules
firebase deploy --only storage:rules
```

Or manually copy the contents of `firestore.rules` and `storage.rules` to Firebase Console.

### 3. Build & Run

1. Open project in Android Studio
2. Sync Gradle dependencies
3. Connect Android device or start emulator
4. Run the app

## Key Architecture Decisions

### 1. Network-First, Not Offline-First
- Assumes connectivity for real-time features
- Shows loading states during network operations
- Simpler implementation suitable for showcase app

### 2. Client-Side Business Logic
- No Cloud Functions required initially
- Validation in repositories and ViewModels
- Server-side validation via Firestore security rules

### 3. Simplified Invitations
- 6-character invite codes instead of Firebase Dynamic Links
- Can be upgraded to Dynamic Links when scaling
- Users share invite codes via standard share intent

### 4. Direct Photo Uploads
- Coroutine-based uploads with loading states
- No background workers (WorkManager)
- User waits for upload completion (acceptable for 1-10 photos)

### 5. Simple QR Verification
- UUID-based tokens stored in Firestore
- Online-only validation
- No encryption or offline support needed

## Firebase Cost Estimate

### 100 Users
- **Firestore**: Free (under limits)
- **Storage**: Free (under 5GB)
- **Total**: $0/month

### 1000 Users
- **Firestore**: ~3M reads/month (Free)
- **Storage**: ~20GB ($0.40/month)
- **Bandwidth**: ~100GB ($2.40/month)
- **Total**: ~$3/month

## Scalability Path

### At 500 users, add:
1. Cloud Functions for denormalization
2. Pagination on photo grids
3. Thumbnail generation

### At 1000 users, add:
4. Firebase Dynamic Links
5. Push notifications (FCM)
6. Background uploads (WorkManager)
7. Local caching (Room)

### At 5000+ users:
8. Advanced analytics
9. CDN optimization
10. Offline-first architecture

## Known Limitations (By Design)

1. **No offline support**: Requires internet connection
2. **No background sync**: Photos upload when user initiates
3. **Simple invite system**: Code-based instead of deep links
4. **No image thumbnails**: Uses same URL for thumbnail and full image
5. **No push notifications**: Users must refresh to see updates
6. **Basic error handling**: Shows error messages, no retry mechanisms

These limitations are acceptable for 100-1000 users and can be addressed when scaling.

## Security Notes

### Fixed Issues
- **Photo Read Permission**: Added host check to allow event hosts to view photos (not just guests)
- **Photo Delete Permission**: Hosts can delete any photo, users can delete their own

### Current Security Rules
- Users can only modify their own profile
- Event hosts have full control over their events
- Guests can view event details but not modify
- Photos are readable by event members (guests + host)
- QR tokens are validated server-side

## Testing

### Manual Testing Checklist
- [ ] Google Sign-In works
- [ ] Create event with background image
- [ ] Generate and share invite code
- [ ] Join event with invite code
- [ ] Upload photo to event
- [ ] View photos in gallery
- [ ] Display QR code for guest
- [ ] Scan QR code as host
- [ ] Add YouTube playlist
- [ ] Real-time updates work

## Contributing

This is a showcase project demonstrating simplified clean architecture for Android with Firebase.

## License

MIT License - feel free to use for learning and reference.

## Contact

For questions or feedback, please open an issue.

---

**Note**: Remember to replace `YOUR_WEB_CLIENT_ID` in `AuthRepository.kt` with your actual Firebase Web Client ID before running the app.

