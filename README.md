# Lumen - Android Event Management App

A modern, feature-rich Android event management application built with Jetpack Compose and Firebase. This clean architecture app enables hosts to create events, manage guests, share photos, and coordinate activities in real-time.

## 📱 Features

### Event Management
- **Create & Manage Events**: Organize events with details like name, description, date, location, duration
- **Custom Backgrounds**: Choose from 6 preset background images for event customization
- **Real-time Updates**: Automatic synchronization across all devices using Firebase Firestore
- **Event Settings**: Comprehensive settings for guest limits, visibility, collaborative sections

### Guest Management
- **Invite System**: Share 6-character invite codes via any app
- **Approval Workflow**: Optional guest approval system for hosts
- **Guest Lists**: View and manage confirmed guests and pending requests
- **Guest Details**: See individual guest profiles, check-in status, and RSVP information

### Photo Sharing
- **Upload Photos**: Share event memories with automatic image compression
- **Real-time Gallery**: View photos as they're uploaded by other guests
- **Full-screen View**: Tap photos for detailed view
- **Smart Permissions**: Hosts and guests can delete their own photos, hosts can delete any

### QR Check-In
- **Generate QR Codes**: Unique UUID-based QR codes for each guest
- **Scanner**: Hosts can scan QR codes using CameraX and ML Kit
- **Real-time Tracking**: Instant check-in status updates
- **Guest Display**: Guests can display their QR code from their device

### Music Playlists
- **YouTube Integration**: Add YouTube playlist URLs to events
- **Multiple Playlists**: Support for multiple playlists per event
- **Embedded Player**: Play music directly in the app
- **Easy Management**: Add and remove playlists on the fly

### User Profiles
- **Profile Management**: View and edit user information
- **Activity Tracking**: See events hosted and events attended as a guest
- **Google Integration**: Seamless Google Sign-In with profile sync

### Additional Features
- **Location Selection**: Interactive map-based location picker
- **Collaborative Sections**: Toggle collaborative features for guests
- **Visibility Controls**: Public or private event settings
- **Guest Limit Validation**: Set and enforce maximum guest capacity
- **Dark Theme Support**: Material 3 design with dynamic theming

## 🛠 Tech Stack

### Core

- **Language**: Kotlin
- **UI**: Jetpack Compose with Material 3
- **Architecture**: Clean Architecture (Repository Pattern)
- **Dependency Injection**: Hilt
- **Minimum SDK**: 23 (Android 6.0)
- **Target SDK**: 34 (Android 14)

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
- **Maps**: Google Maps Compose
- **Coroutines**: Kotlin Coroutines & Flow
- **Permissions**: Accompanist Permissions

## 📁 Project Structure

```
app/src/main/java/com/eventapp/intraview/
├── EventApplication.kt         # Application class with Hilt
├── MainActivity.kt             # Main activity with Compose
│
├── data/
│   ├── model/                  # Data classes
│   │   ├── User.kt            # User data model
│   │   ├── Event.kt           # Event data model
│   │   ├── Invitation.kt      # Invitation with status enum
│   │   ├── Photo.kt           # Photo data model
│   │   └── PendingGuest.kt    # Pending guest requests
│   │
│   └── repository/            # Repository layer
│       ├── AuthRepository.kt          # Authentication & user management
│       ├── UserRepository.kt          # User profile operations
│       ├── EventRepository.kt         # Event CRUD & real-time updates
│       ├── InvitationRepository.kt    # Invitation & check-in logic
│       ├── PendingGuestRepository.kt  # Guest approval workflow
│       └── PhotoRepository.kt         # Photo upload & retrieval
│
├── di/                        # Dependency injection modules
│   ├── AppModule.kt          # App-level DI
│   └── FirebaseModule.kt     # Firebase DI
│
├── ui/
│   ├── theme/                 # App theme
│   │   ├── Color.kt          # Color palette
│   │   ├── Type.kt           # Typography
│   │   ├── Theme.kt          # Material 3 theme
│   │   ├── Spacing.kt        # Spacing system
│   │   └── Animation.kt      # Animation utilities
│   │
│   ├── components/            # Reusable UI components
│   │   ├── EventCard.kt              # Event card component
│   │   ├── PhotoGrid.kt              # Photo grid component
│   │   ├── LoadingState.kt           # Loading indicator
│   │   ├── ErrorState.kt             # Error display
│   │   ├── EmptyState.kt             # Empty state display
│   │   ├── MapLocationPicker.kt      # Location selection
│   │   ├── FloatingActionMenu.kt     # FAB menu
│   │   └── EventSettingsDialog.kt    # Event settings
│   │
│   ├── navigation/            # Navigation setup
│   │   ├── Routes.kt         # Navigation routes
│   │   └── NavGraph.kt       # Navigation graph
│   │
│   └── screens/               # Feature screens
│       ├── auth/              # Authentication
│       │   ├── AuthViewModel.kt
│       │   ├── LoginViewModel.kt
│       │   └── LoginScreen.kt
│       │
│       ├── home/              # Home screen
│       │   ├── HomeViewModel.kt
│       │   └── HomeScreen.kt
│       │
│       ├── event/             # Event management
│       │   ├── CreateEventViewModel.kt
│       │   ├── CreateEventScreen.kt
│       │   ├── EventDetailViewModel.kt
│       │   └── EventDetailScreen.kt
│       │
│       ├── guest/             # Guest management
│       │   ├── GuestListViewModel.kt
│       │   ├── GuestListScreen.kt
│       │   ├── GuestDetailViewModel.kt
│       │   └── GuestDetailScreen.kt
│       │
│       ├── profile/           # User profile
│       │   ├── ProfileViewModel.kt
│       │   └── ProfileScreen.kt
│       │
│       ├── photo/             # Photo gallery
│       │   ├── PhotoViewModel.kt
│       │   └── PhotoGalleryScreen.kt
│       │
│       ├── qr/                # QR code features
│       │   ├── QRViewModel.kt
│       │   ├── QRDisplayScreen.kt
│       │   └── QRScannerScreen.kt
│       │
│       └── playlist/          # Playlist management
│           ├── PlaylistViewModel.kt
│           └── PlaylistScreen.kt
│
└── util/                      # Utility classes
    ├── Constants.kt           # App constants
    ├── DateFormatter.kt       # Date formatting
    ├── ImageCompressor.kt     # Image compression
    ├── InviteCodeGenerator.kt # Code generation
    ├── InvitationMigrationUtil.kt # Data migration
    └── Result.kt              # Result sealed class
```

## 🚀 Setup Instructions

### Prerequisites

- Android Studio (latest version recommended)
- JDK 17 or higher
- Google account for Firebase Console
- Android device or emulator (API 23+)

### 1. Firebase Configuration

1. Create a new Firebase project at [Firebase Console](https://console.firebase.google.com/)
2. Add Android app to Firebase project
   - Package name: `com.eventapp.intraview`
   - Download `google-services.json` and place in `app/` directory
3. Enable Firebase services:
   - **Authentication**: Enable Google Sign-In
   - **Cloud Firestore**: Create database in production mode
   - **Cloud Storage**: Enable storage bucket
4. Get Web Client ID for Google Sign-In:
   - Go to Firebase Console → Authentication → Sign-in method → Google
   - Copy the "Web client ID"
   - Replace `YOUR_WEB_CLIENT_ID` in `AuthRepository.kt` (line 93)

**For detailed Firebase setup instructions, see [SETUP_GUIDE.md](SETUP_GUIDE.md)**

### 2. Firestore Security Rules

Deploy the security rules to Firebase:

```bash
# Install Firebase CLI
npm install -g firebase-tools

# Login to Firebase
firebase login

# Initialize Firebase in project (if not already done)
firebase init firestore

# Deploy production rules
firebase deploy --only firestore:rules
firebase deploy --only storage:rules
```

Or manually copy the contents of `firestore.rules.production` and `storage.rules` to Firebase Console.

### 3. Build & Run

1. Clone the repository:

   ```bash
   git clone https://github.com/yourusername/Lumen.git
   cd Lumen
   ```

2. Open project in Android Studio
3. Sync Gradle dependencies (automatically prompts)
4. Connect Android device or start emulator
5. Run the app (`Shift+F10` or click Run button)

### 4. Generate App Icons (Optional)

If you need custom app icons:

```bash
# Make the script executable
chmod +x generate_icons.sh

# Run the icon generation script
./generate_icons.sh path/to/your/icon.svg
```

Or use Android Studio: Right-click `res` → New → Image Asset

## 📦 Dependencies

### Firebase (BOM: 33.5.1)

- firebase-auth-ktx
- firebase-firestore-ktx
- firebase-storage-ktx
- firebase-dynamic-links-ktx
- play-services-auth: 20.7.0

### Jetpack Compose (BOM: 2023.10.01)

- compose-ui
- compose-material3
- compose-material-icons-extended
- compose-ui-tooling (debug)

### Core Android

- core-ktx: 1.12.0
- activity-compose: 1.8.2
- lifecycle-runtime-ktx: 2.6.2
- lifecycle-viewmodel-compose: 2.6.2
- lifecycle-runtime-compose: 2.6.2

### Navigation

- navigation-compose: 2.7.6

### Hilt (Dependency Injection)

- hilt-android: 2.52
- hilt-navigation-compose: 1.2.0

### Camera & ML Kit

- camera-camera2: 1.3.1
- camera-lifecycle: 1.3.1
- camera-view: 1.3.1
- barcode-scanning: 17.3.0
- zxing-core: 3.5.2

### Maps

- maps-compose: 4.3.0
- play-services-maps: 18.2.0
- play-services-location: 21.0.1

### Other

- coil-compose: 2.5.0 (image loading)
- accompanist-permissions: 0.32.0 (permissions)
- exifinterface: 1.3.7 (image rotation)
- guava: 32.1.3-android (CameraX dependency)
- kotlinx-coroutines-android: 1.7.3
- kotlinx-coroutines-play-services: 1.7.3

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

## 🔒 Security Notes

### Production Security Rules

The app includes production-ready Firestore security rules in `firestore.rules.production`:

- **User Profiles**: Users can read any profile but only write their own
- **Events**: Event hosts have full control; guests can view and update limited fields
- **Invitations**: Only hosts and invited guests can access invitation data
- **Photos**: Event members (host + guests) can read; users can delete their own photos
- **Authentication**: All operations require Firebase Authentication

### Security Best Practices

- **google-services.json**: Never commit to version control (already in `.gitignore`)
- **API Keys**: Keep Firebase configuration secure
- **Rules Testing**: Use Firebase Console Rules Playground before deployment
- **App Check**: Consider enabling for additional security in production
- **Rate Limiting**: Monitor usage and set up alerts

## 🧪 Testing

### Manual Testing Checklist

- [ ] Google Sign-In authentication works
- [ ] Create event with all details (name, description, date, location)
- [ ] Select event background image
- [ ] Generate and share invite code
- [ ] Join event using invite code
- [ ] Guest approval workflow (if enabled)
- [ ] View and manage guest list
- [ ] Upload photos to event gallery
- [ ] View photos in full-screen
- [ ] Delete own photos
- [ ] Display QR code for check-in (guest)
- [ ] Scan QR codes for check-in (host)
- [ ] Add YouTube playlist URL
- [ ] View and manage event settings
- [ ] Edit user profile
- [ ] Select location on map
- [ ] Real-time synchronization across devices
- [ ] Sign out functionality

### Testing Tips

1. Test with multiple devices simultaneously to verify real-time updates
2. Try different screen sizes and orientations
3. Test with poor network conditions
4. Verify camera permissions for QR scanner
5. Check Firebase Console for data integrity

## 🎨 Design System

The app follows Material 3 design guidelines with:

- **Dynamic Theming**: Adapts to Android 12+ dynamic colors
- **Dark Mode**: Full dark theme support
- **Responsive Layout**: Works on phones and tablets
- **Consistent Spacing**: Using a spacing system (8dp base)
- **Typography**: Material 3 typography scale
- **Custom Animations**: Smooth transitions and interactions

See [DESIGN_SYSTEM.md](md_files/DESIGN_SYSTEM.md) for detailed design specifications.

## 🤝 Contributing

This project demonstrates clean architecture principles for Android with Firebase. While it's primarily a showcase project, contributions are welcome!

### How to Contribute

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Development Guidelines

- Follow Kotlin coding conventions
- Use Jetpack Compose best practices
- Maintain clean architecture separation
- Add comments for complex logic
- Test on multiple devices before submitting PR

## 📝 License

This project is licensed under the MIT License - feel free to use for learning, reference, or as a starting point for your own projects.

## 📧 Contact

For questions, feedback, or issues:

- Open an issue on GitHub
- Check existing documentation in `/md_files/` for detailed guides
- Review [ARCHITECTURE.md](ARCHITECTURE.md) for architectural details

## 🙏 Acknowledgments

- **Firebase**: For providing excellent backend services
- **Jetpack Compose**: For modern Android UI development
- **Material Design 3**: For beautiful design components
- **ML Kit**: For QR code scanning capabilities
- **Coil**: For efficient image loading
- **Hilt**: For clean dependency injection

## 📚 Additional Documentation

- [SETUP_GUIDE.md](SETUP_GUIDE.md) - Detailed Firebase setup instructions
- [ARCHITECTURE.md](ARCHITECTURE.md) - Architecture patterns and design decisions
- [md_files/](md_files/) - Feature-specific documentation and change logs

## ⚠️ Important Notes

1. **Replace Firebase Configuration**: Update `YOUR_WEB_CLIENT_ID` in `AuthRepository.kt` before running
2. **Security Rules**: Deploy `firestore.rules.production` for production use
3. **API Keys**: Keep `google-services.json` secure and never commit to public repos
4. **Testing**: Test thoroughly before deploying to production users
5. **Scalability**: Review scalability recommendations for 500+ users

---

**Built with ❤️ using Kotlin, Jetpack Compose, and Firebase**

*Last Updated: October 2025*

