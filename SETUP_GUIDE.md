# Firebase Setup Guide for Intra-View

This guide will walk you through setting up Firebase for the Intra-View app.

## Prerequisites

- Android Studio installed
- Google account for Firebase Console
- Basic understanding of Firebase services

## Step 1: Create Firebase Project

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click "Add project" or "Create a project"
3. Enter project name: `intra-view` (or your preferred name)
4. Enable Google Analytics (optional, recommended)
5. Click "Create project"

## Step 2: Add Android App to Firebase

1. In Firebase Console, click on your project
2. Click the Android icon (🤖) to add an Android app
3. Register app with these details:
   - **Android package name**: `com.eventapp.intraview` (must match exactly)
   - **App nickname**: `Intra-View` (optional)
   - **Debug signing certificate SHA-1**: (optional for now, required for Google Sign-In)
4. Click "Register app"

### Getting SHA-1 Certificate (Required for Google Sign-In)

Run this command in your project directory:

```bash
# For debug certificate (development)
keytool -list -v -alias androiddebugkey -keystore ~/.android/debug.keystore

# Default password is: android
```

Copy the SHA-1 fingerprint and add it to Firebase Console:
- Go to Project Settings → Your Apps → Add fingerprint

## Step 3: Download google-services.json

1. In the Firebase setup flow, click "Download google-services.json"
2. Place the file in your project: `Intra-View/app/google-services.json`
3. **Important**: Do not commit this file to version control

## Step 4: Enable Firebase Authentication

1. In Firebase Console, go to **Build → Authentication**
2. Click "Get started"
3. Click on **Sign-in method** tab
4. Enable **Google** sign-in provider:
   - Click on "Google"
   - Toggle "Enable"
   - Enter project support email
   - Click "Save"
5. **Important**: Copy the "Web client ID" (you'll need this in Step 6)

## Step 5: Set up Cloud Firestore

1. In Firebase Console, go to **Build → Firestore Database**
2. Click "Create database"
3. Select **Start in production mode** (we'll add rules later)
4. Choose a location (select closest to your users):
   - `us-central1` for North America
   - `europe-west1` for Europe
   - `asia-southeast1` for Asia
5. Click "Enable"

### Deploy Firestore Security Rules

Option A: Using Firebase CLI (Recommended)

```bash
# Install Firebase CLI
npm install -g firebase-tools

# Login to Firebase
firebase login

# Initialize Firestore in your project
cd /path/to/Intra-View
firebase init firestore

# Select your Firebase project
# Use existing firestore.rules file

# Deploy rules
firebase deploy --only firestore:rules
```

Option B: Manual Copy-Paste

1. Open `firestore.rules` file from project
2. In Firebase Console → Firestore → Rules tab
3. Copy and paste the entire content
4. Click "Publish"

## Step 6: Set up Cloud Storage

1. In Firebase Console, go to **Build → Storage**
2. Click "Get started"
3. Select **Start in production mode** (we'll add rules later)
4. Use the same location as Firestore
5. Click "Done"

### Deploy Storage Security Rules

Option A: Using Firebase CLI

```bash
firebase deploy --only storage:rules
```

Option B: Manual Copy-Paste

1. Open `storage.rules` file from project
2. In Firebase Console → Storage → Rules tab
3. Copy and paste the entire content
4. Click "Publish"

## Step 7: Update App with Web Client ID

1. Open Firebase Console → Project Settings
2. Scroll down to "Your apps"
3. In the "Web API Key" section, find **Web client ID** (from OAuth 2.0 Client IDs)
   - Or go to Authentication → Sign-in method → Google → Web SDK configuration
4. Copy the Web client ID (looks like: `123456789-abcdefg.apps.googleusercontent.com`)

5. Open `app/src/main/java/com/eventapp/intraview/data/repository/AuthRepository.kt`
6. Find line 93 (in `getGoogleSignInClient` function)
7. Replace `YOUR_WEB_CLIENT_ID` with your actual Web Client ID:

```kotlin
fun getGoogleSignInClient(context: Context): GoogleSignInClient {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken("YOUR_WEB_CLIENT_ID")  // ← Replace this
        .requestEmail()
        .build()
    return GoogleSignIn.getClient(context, gso)
}
```

Example:
```kotlin
.requestIdToken("123456789-abcdefg.apps.googleusercontent.com")
```

## Step 8: Optional - Enable Firebase Dynamic Links (for future scaling)

1. In Firebase Console, go to **Engage → Dynamic Links**
2. Click "Get started"
3. Set up a domain prefix (e.g., `intraview.page.link`)
4. Update app configuration when ready to implement

## Step 9: Verify Setup

Build and run the app:

```bash
# In Android Studio
1. Sync Gradle files
2. Build → Make Project
3. Run app on device/emulator
```

### Test Checklist

- [ ] App builds successfully
- [ ] Google Sign-In works
- [ ] Can create an event (Firestore write)
- [ ] Can view events (Firestore read)
- [ ] Can upload photo (Storage write)
- [ ] Can view photos (Storage read)

## Troubleshooting

### Google Sign-In Fails

**Error**: "Developer Error" or "API not enabled"

**Solution**:
1. Verify SHA-1 certificate is added to Firebase
2. Verify Web Client ID is correct in AuthRepository.kt
3. Make sure google-services.json is in app/ directory
4. Clean and rebuild project

### Firestore Permission Denied

**Error**: "PERMISSION_DENIED: Missing or insufficient permissions"

**Solution**:
1. Verify Firestore rules are deployed
2. Check that user is authenticated before making requests
3. Test rules in Firebase Console → Rules Playground

### Storage Upload Fails

**Error**: "User does not have permission to access storage"

**Solution**:
1. Verify Storage rules are deployed
2. Make sure user is authenticated
3. Check internet connection

### App Crashes on Startup

**Solution**:
1. Verify google-services.json is present
2. Check AndroidManifest.xml has correct permissions
3. Look at Logcat for specific error messages
4. Ensure all dependencies are synced

## Security Notes

### Production Checklist

Before deploying to production:

- [ ] Update Firestore rules with proper access controls
- [ ] Update Storage rules with file size limits
- [ ] Enable App Check for additional security
- [ ] Set up Cloud Functions for sensitive operations
- [ ] Configure rate limiting
- [ ] Enable Firebase Performance Monitoring
- [ ] Set up Crashlytics for error tracking

### Firestore Security Rules Explained

The provided rules ensure:
- Users can only read/write their own profile
- Event hosts control their events
- Only event members (host + guests) can view event details
- Only event members can view photos (**FIXED**: includes host)
- QR tokens are validated before check-in
- All operations require authentication

## Cost Management

### Free Tier Limits (Spark Plan)

- **Firestore**: 50K reads, 20K writes, 20K deletes per day
- **Storage**: 5GB stored, 1GB/day bandwidth
- **Authentication**: Unlimited

### Monitor Usage

1. Go to Firebase Console → Usage and billing
2. Set up budget alerts
3. Monitor daily usage

For 100-1000 users, costs should stay under $5/month.

## Next Steps

1. Test all features thoroughly
2. Add more events and invite users
3. Monitor Firebase Console for usage
4. Plan for scaling (see README.md)

## Support

For issues:
1. Check Firebase documentation
2. Look at Logcat errors
3. Review security rules in Firebase Console
4. Test with Firebase Local Emulator Suite for debugging

## Useful Links

- [Firebase Console](https://console.firebase.google.com/)
- [Firebase Auth Documentation](https://firebase.google.com/docs/auth)
- [Firestore Documentation](https://firebase.google.com/docs/firestore)
- [Storage Documentation](https://firebase.google.com/docs/storage)
- [Android Setup Guide](https://firebase.google.com/docs/android/setup)

---

**Important**: Keep your `google-services.json` secure and never commit it to public repositories!

