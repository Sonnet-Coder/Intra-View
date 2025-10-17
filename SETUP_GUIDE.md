# Firebase Setup Guide for Lumen

This comprehensive guide will walk you through setting up Firebase for the Lumen Android event management app, from initial configuration to production deployment.

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Create Firebase Project](#step-1-create-firebase-project)
3. [Add Android App](#step-2-add-android-app-to-firebase)
4. [Enable Authentication](#step-4-enable-firebase-authentication)
5. [Set up Cloud Firestore](#step-5-set-up-cloud-firestore)
6. [Set up Cloud Storage](#step-6-set-up-cloud-storage)
7. [Configure Google Maps (Optional)](#step-7-configure-google-maps-optional)
8. [Update App Configuration](#step-8-update-app-with-web-client-id)
9. [Verify Setup](#step-9-verify-setup)
10. [Production Deployment](#production-deployment)
11. [Troubleshooting](#troubleshooting)

## Prerequisites

- Android Studio (latest version recommended)
- Google account for Firebase Console
- Basic understanding of Firebase services
- JDK 17 or higher installed
- Git installed (for version control)

## Step 1: Create Firebase Project

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click "Add project" or "Create a project"
3. Enter project name: `lumen` (or your preferred name)
4. Enable Google Analytics (optional, recommended)
5. Click "Create project"

## Step 2: Add Android App to Firebase

1. In Firebase Console, click on your project
2. Click the Android icon (🤖) to add an Android app
3. Register app with these details:
   - **Android package name**: `com.eventapp.intraview` (must match exactly)
   - **App nickname**: `Lumen` (optional)
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
2. Place the file in your project: `Lumen/app/google-services.json`
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
cd /path/to/Lumen
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


## Step 7: Configure Google Maps (Optional)

The app includes map-based location selection. To enable this feature:

### 1. Enable Google Maps SDK

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Select your Firebase project
3. Navigate to **APIs & Services → Library**
4. Search for and enable:
   - **Maps SDK for Android**
   - **Places API** (for location search)

### 2. Create API Key

1. Go to **APIs & Services → Credentials**
2. Click **Create Credentials → API Key**
3. Copy the API key
4. Click **Restrict Key** to add restrictions:
   - **Application restrictions**: Select "Android apps"
   - Add your package name: `com.eventapp.intraview`
   - Add SHA-1 certificate fingerprint
   - **API restrictions**: Restrict to Maps SDK for Android and Places API

### 3. Add API Key to App

1. Open `app/src/main/AndroidManifest.xml`
2. Add your API key in the `<meta-data>` tag (already present in template):

```xml
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="YOUR_GOOGLE_MAPS_API_KEY"/>
```

**Note:** The map feature will not work without a valid API key. If you don't need location selection, you can skip this step.

## Production Deployment

### Before Going Live

#### 1. Update Security Rules

Replace the default Firestore rules with production rules:

```bash
# Copy production rules to main rules file
cp firestore.rules.production firestore.rules

# Deploy to Firebase
firebase deploy --only firestore:rules
firebase deploy --only storage:rules
```

**Important Production Rules:**

- Users can only modify their own data
- Event hosts have full control over events
- Guests have read-only access to event details
- Photos require event membership
- All operations require authentication

#### 2. Enable App Check

App Check helps protect your backend from abuse:

1. Go to Firebase Console → **Build → App Check**
2. Click **Get Started**
3. Register your app
4. Select attestation provider:
   - **Play Integrity** for production (requires app on Play Store)
   - **Debug provider** for development
5. Enable enforcement for:
   - Cloud Firestore
   - Cloud Storage

#### 3. Set Up Budget Alerts

Monitor costs and avoid surprises:

1. Go to Firebase Console → **Usage and billing**
2. Click **Details & Settings**
3. Set up **Budget alerts**:
   - Recommended: Alert at 50%, 75%, 90% of budget
   - Set initial budget: $10-20/month for 1000 users
4. Configure email notifications

#### 4. Enable Performance Monitoring

Track app performance:

```kotlin
// Add to app/build.gradle.kts dependencies
implementation("com.google.firebase:firebase-perf-ktx")

// Plugin already included in project
```

Rebuild and deploy - performance data appears in Firebase Console automatically.

#### 5. Set Up Crashlytics

Monitor crashes in production:

```kotlin
// Add to app/build.gradle.kts dependencies
implementation("com.google.firebase:firebase-crashlytics-ktx")

// Add plugin to app/build.gradle.kts
plugins {
    id("com.google.firebase.crashlytics")
}
```

### Production Checklist

Before deploying to production users:

- [ ] Production Firestore rules deployed
- [ ] Production Storage rules deployed
- [ ] App Check enabled and configured
- [ ] Budget alerts set up
- [ ] Performance Monitoring enabled
- [ ] Crashlytics configured
- [ ] Google Sign-In tested with multiple accounts
- [ ] All features tested on real devices
- [ ] Privacy policy prepared and linked
- [ ] Terms of service prepared and linked
- [ ] Release keystore created and secured
- [ ] ProGuard rules tested with release build
- [ ] APK/AAB signed with release key
- [ ] Version code and name updated

### Monitoring Production

#### Firebase Console Dashboards

1. **Authentication**: Track user sign-ups and activity
2. **Firestore**: Monitor read/write operations and data size
3. **Storage**: Track file uploads and bandwidth usage
4. **Performance**: View app startup time, network requests
5. **Crashlytics**: Review crashes and non-fatal errors

#### Key Metrics to Watch

- **Daily Active Users (DAU)**
- **Read/Write operations per user**
- **Storage usage growth rate**
- **Average session duration**
- **Crash-free users percentage**

#### Cost Optimization

- Implement pagination for large lists
- Generate and use thumbnail images
- Set appropriate cache settings
- Monitor and optimize expensive queries
- Use batch operations where possible

## Advanced Configuration

### 1. Dynamic Links (Optional)

For deep linking and invitation sharing:

1. Go to Firebase Console → **Engage → Dynamic Links**
2. Click **Get Started**
3. Set up domain: `yourapp.page.link`
4. Configure link behavior:
   - Android: Open app if installed, else Play Store
   - iOS: App Store (future iOS version)
5. Update invite sharing to use Dynamic Links

### 2. Cloud Functions (For Scaling)

When you reach 5000+ users, consider Cloud Functions:

```javascript
// Example: Denormalize user data
exports.onUserUpdate = functions.firestore
  .document('users/{userId}')
  .onUpdate((change, context) => {
    // Update user info in all events
    const newData = change.after.data();
    // ... update logic
  });
```

### 3. Firebase Extensions

Useful extensions for production:

- **Resize Images**: Auto-generate thumbnails
- **Delete User Data**: GDPR compliance
- **Trigger Email**: User notifications
- **Run Payments**: In-app purchases (if needed)

Install via Firebase Console → **Extensions**.

### 4. Backup Strategy

Protect your data:

1. Enable **Firestore automatic backups**:
   - Console → Firestore → Backups
   - Set schedule (daily recommended)
2. Export data periodically:

```bash
gcloud firestore export gs://your-bucket/backups
```

3. Test restore procedure before you need it

## Security Best Practices

### 1. Protect API Keys

- Never commit `google-services.json` to public repos
- Use environment variables for sensitive keys
- Restrict API keys to specific apps and APIs
- Rotate keys periodically

### 2. Implement Rate Limiting

Use Firebase security rules:

```javascript
// Example: Limit photo uploads per user per day
match /photos/{photoId} {
  allow create: if request.auth != null
    && request.auth.token.email_verified
    && request.time < resource.data.lastUpload + duration.value(1, 'd');
}
```

### 3. Validate User Input

- Sanitize all user inputs in app and rules
- Set maximum field lengths
- Validate email formats
- Check date ranges
- Enforce business rules server-side

### 4. Monitor Suspicious Activity

- Track failed auth attempts
- Monitor unusual data patterns
- Set up alerts for quota spikes
- Review security rules regularly

## Migration & Updates

### Updating Firebase Config

If you need to change Firebase projects:

1. Download new `google-services.json`
2. Replace in `app/` directory
3. Update Web Client ID in `AuthRepository.kt`
4. Clean and rebuild project
5. Test authentication thoroughly

### Data Migration

When updating data models:

1. Create migration scripts
2. Test on development database first
3. Use batch operations for efficiency
4. Keep old fields temporarily for rollback
5. Monitor for errors during migration

### App Updates

For seamless updates:

1. Use Firebase Remote Config for feature flags
2. Implement backwards compatibility
3. Test with old and new app versions
4. Staged rollout (10% → 50% → 100%)
5. Monitor crashes after each stage

---

## Additional Resources

### Documentation

- [Firebase Android Documentation](https://firebase.google.com/docs/android/setup)
- [Cloud Firestore Documentation](https://firebase.google.com/docs/firestore)
- [Firebase Authentication](https://firebase.google.com/docs/auth)
- [Cloud Storage](https://firebase.google.com/docs/storage)
- [Firebase Security Rules](https://firebase.google.com/docs/rules)

### Tools

- [Firebase CLI](https://firebase.google.com/docs/cli)
- [Firebase Local Emulator Suite](https://firebase.google.com/docs/emulator-suite)
- [Rules Playground](https://firebase.google.com/docs/rules/simulator)

### Community

- [Firebase Slack](https://firebase.community/)
- [Stack Overflow - Firebase Tag](https://stackoverflow.com/questions/tagged/firebase)
- [Firebase GitHub](https://github.com/firebase/)

---

**Last Updated:** October 2025

**Need Help?**

- Review [README.md](README.md) for project overview
- Check [ARCHITECTURE.md](ARCHITECTURE.md) for technical details
- Open an issue on GitHub for specific problems

---

**Important:** Always test thoroughly in development before deploying to production. Keep your Firebase configuration secure and monitor usage regularly.
