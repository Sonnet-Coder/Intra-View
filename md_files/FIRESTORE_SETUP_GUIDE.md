# Firestore Database Setup Guide

## Current Issue
Your app is trying to connect to the `(default)` database, but you created `lumen-1`. The Firebase Android SDK can only connect to a database named `(default)`.

## Solution: Create the Default Database

### Step 1: Create Default Database in Firebase Console

1. **Go to Firestore Console:**
   ```
   https://console.firebase.google.com/project/lumen-f6699/firestore
   ```

2. **Click the database dropdown** at the top (currently showing "lumen-1")

3. **Click "Create database"**

4. **Configure database:**
   - **Database ID:** Leave as `(default)`
   - **Location:** Select `nam5` (North America - same as lumen-1)
     - **Important:** You cannot change this later!
   
5. **Choose a mode:**

   **Option A: Test Mode (Quick Start)**
   ```
   ✅ Good for: Development & testing
   ❌ Bad for: Production (insecure!)
   ⏰ Expires: 30 days from creation
   ```
   - Allows all reads and writes
   - Perfect for getting started quickly
   - You can deploy secure rules later

   **Option B: Production Mode (Recommended)**
   ```
   ✅ Good for: Production apps
   ✅ Secure from day one
   📋 Requires: Security rules setup
   ```
   - Starts with deny-all rules
   - You'll deploy rules in Step 2

6. **Click "Enable"** and wait 1-2 minutes

### Step 2: Deploy Security Rules

Once the database is created:

#### Option A: Using Firebase CLI (Recommended)

1. **Install Firebase CLI** (if not installed):
   ```bash
   npm install -g firebase-tools
   ```

2. **Login to Firebase:**
   ```bash
   firebase login
   ```

3. **Initialize Firebase** (if not done):
   ```bash
   cd /home/ani/Projects/Intra-View
   firebase init firestore
   ```
   - Select your project: `lumen-f6699`
   - Use existing `firestore.rules` file? **Yes**
   - Use existing `firestore.indexes.json`? **Yes**

4. **Deploy the rules:**
   ```bash
   firebase deploy --only firestore:rules
   ```

#### Option B: Manual Copy-Paste

1. **Copy the production rules:**
   - Open `firestore.rules.production` file in your project
   - Or use the test rules in `firestore.rules` (less secure)

2. **Go to Firebase Console:**
   ```
   https://console.firebase.google.com/project/lumen-f6699/firestore/rules
   ```

3. **Paste the rules** and click **"Publish"**

### Step 3: Verify Setup

1. **Restart your app** (force close and reopen)

2. **Test authentication:**
   - Sign in with Google
   - Check for any errors in logcat

3. **Test event creation:**
   - Create a test event
   - Check if it appears in Firestore Console

4. **Test joining events:**
   - Sign in with different account
   - Use invite code to join event
   - Should work now! ✅

## Understanding Firestore Security

### Current Test Rules (firestore.rules)
```javascript
allow read, write: if request.time < timestamp.date(2025, 11, 11);
```
- ✅ Allows everything until Nov 11, 2025
- ❌ Very insecure - anyone can access your data!
- ⚠️ Use only for testing

### Production Rules (firestore.rules.production)
```javascript
// Users can only edit their own profile
// Event hosts and guests can manage events
// Only uploaders can delete their photos
```
- ✅ Secure and production-ready
- ✅ Follows principle of least privilege
- ✅ Prevents unauthorized access

## Why No Users in Security Section?

You mentioned seeing no users in the Firestore security section. This is **normal**! Here's why:

1. **Firebase Authentication ≠ Firestore Users**
   - Firebase Auth manages user authentication (login/logout)
   - Firestore stores user **data** (profiles, preferences)

2. **Where to find users:**
   - **Firebase Auth users:** 
     ```
     https://console.firebase.google.com/project/lumen-f6699/authentication/users
     ```
   - **Firestore user documents:**
     ```
     https://console.firebase.google.com/project/lumen-f6699/firestore/data/users
     ```

3. **Your app creates user documents** when someone signs in (see `AuthRepository.kt`)

## Troubleshooting

### Error: "The database (default) does not exist"
- ✅ **Solution:** Follow Step 1 to create the default database

### Error: "Permission denied"
- ✅ **Solution:** Deploy security rules (Step 2)
- Check that you're signed in
- Verify rules in Firebase Console

### Error: "FIRESTORE (X.X.X) [Firestore]: Listen for ... failed"
- ✅ **Solution:** Check internet connection
- Verify database is enabled
- Check security rules

### Events not appearing after creation
- Check Firestore Console → Data tab
- Verify the event document was created
- Check security rules allow read access

## What About "lumen-1" Database?

You can:
- **Keep it:** For testing or different environments
- **Delete it:** To avoid confusion
  ```
  https://console.firebase.google.com/project/lumen-f6699/firestore
  → Click database dropdown → Settings → Delete database
  ```

The `(default)` database is what your app will use.

## Next Steps

1. ✅ Create `(default)` database
2. ✅ Deploy security rules
3. ✅ Test your app
4. 📱 Start creating events!

## Quick Reference

- **Firebase Console:** https://console.firebase.google.com/project/lumen-f6699
- **Firestore Data:** https://console.firebase.google.com/project/lumen-f6699/firestore/data
- **Firestore Rules:** https://console.firebase.google.com/project/lumen-f6699/firestore/rules
- **Authentication:** https://console.firebase.google.com/project/lumen-f6699/authentication/users

## Questions?

If you encounter any issues:
1. Check the error in logcat
2. Verify database exists and is named `(default)`
3. Confirm security rules are deployed
4. Make sure you're signed in to the app

