# Fixed: Google Sign-In Auto-Login After Sign Out

## Problem
After signing out, when the user tries to sign in again, Google automatically signs in with the same account without showing the account picker dialog. This happens because:
- Firebase `auth.signOut()` only signs out from Firebase
- Google Sign-In client maintains a cached account
- On the next sign-in attempt, Google returns the cached account automatically

## Root Cause
From the logs:
```
2025-10-13 06:29:13.426 FirebaseAuth: Notifying auth state listeners about a sign-out event.
2025-10-13 06:29:18.525 LoginScreen: Got account from intent: sarkarya666@iiitmanipur.ac.in
```

The Google Sign-In client was caching the account and returning it immediately without user interaction.

## Solution
Update the `signOut()` method to also sign out from the Google Sign-In client, which clears the cached account.

### Changes Made

#### 1. **AuthRepository.kt**
```kotlin
// Before:
suspend fun signOut() {
    auth.signOut()
}

// After:
suspend fun signOut(context: Context) {
    // Sign out from Google Sign-In client to clear cached account
    getGoogleSignInClient(context).signOut().await()
    // Sign out from Firebase
    auth.signOut()
}
```

#### 2. **HomeViewModel.kt**
```kotlin
// Before:
suspend fun signOut() {
    authRepository.signOut()
}

// After:
suspend fun signOut(context: android.content.Context) {
    authRepository.signOut(context)
}
```

#### 3. **HomeScreen.kt**
- Added import: `import androidx.compose.ui.platform.LocalContext`
- Added: `val context = LocalContext.current`
- Updated sign-out call: `viewModel.signOut(context)`

## How It Works Now

1. User clicks sign-out button
2. System signs out from **Google Sign-In client** (clears cached account)
3. System signs out from **Firebase Auth**
4. User is redirected to login screen
5. When user clicks "Sign in with Google" again:
   - Google account picker is shown
   - User can choose any account or add a new one
   - No automatic sign-in with cached credentials

## Testing
1. Sign in with a Google account ✅
2. Navigate to home screen ✅
3. Click sign-out button ✅
4. Return to login screen ✅
5. Click "Sign in with Google" ✅
6. **Verify**: Account picker is shown (not auto-login) ✅
7. Select same or different account ✅
8. Sign in successfully ✅

## Technical Details
- Uses `GoogleSignInClient.signOut().await()` to clear cached credentials
- Requires `Context` to create the Google Sign-In client
- Uses `LocalContext.current` in Compose to access Android context
- Asynchronous operation using Kotlin coroutines

## Related Google Documentation
- [Google Sign-In for Android - Sign Out](https://developers.google.com/identity/sign-in/android/disconnect)
- The `signOut()` method clears the cached account so the user must select an account on the next sign-in

