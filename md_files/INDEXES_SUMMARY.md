# Firestore Indexes Quick Summary

## ✅ Analysis Complete

Based on your models and repository queries, here's what needs indexing:

## Required Indexes (7 Total)

### 🎯 Events Collection (2 indexes)

1. **Host Events Query**
   ```kotlin
   // Shows events you're hosting
   whereEqualTo("hostId") + orderBy("date", DESCENDING)
   ```
   
2. **Invited Events Query**
   ```kotlin
   // Shows events you're invited to
   whereArrayContains("guestIds") + orderBy("date", DESCENDING)
   ```

### 📸 Photos Collection (1 index)

3. **Event Gallery Query**
   ```kotlin
   // Photos in an event, newest first
   whereEqualTo("eventId") + orderBy("uploadedAt", DESCENDING)
   ```

### 📨 Invitations Collection (4 indexes)

4. **User-Event Lookup**
   ```kotlin
   // Check if user has invitation
   whereEqualTo("userId") + whereEqualTo("eventId")
   ```

5. **QR Token Validation**
   ```kotlin
   // Validate QR code for check-in
   whereEqualTo("qrToken") + whereEqualTo("eventId")
   ```

6. **Filter by Status** (Optional, for future)
   ```kotlin
   // Show accepted/pending guests
   whereEqualTo("eventId") + whereEqualTo("status")
   ```

7. **Checked-In Guests** (Optional, for future)
   ```kotlin
   // See who's checked in
   whereEqualTo("eventId") + whereEqualTo("checkedIn")
   ```

## What Happens Without These Indexes?

❌ **Your app will crash** with errors like:
```
FAILED_PRECONDITION: The query requires an index
```

✅ **With indexes:** All queries work perfectly!

## How to Deploy

### Quick Method (1 command):
```bash
# Activate Node v20
export NVM_DIR="$HOME/.nvm"
source "$NVM_DIR/nvm.sh"
nvm use 20

# Deploy
cd /home/ani/Projects/Intra-View
firebase deploy --only firestore:indexes
```

### Manual Method:
1. Copy index definitions from `firestore.indexes.json`
2. Paste in Firebase Console → Indexes
3. Wait for build to complete (~2-5 minutes)

## Files Updated

✅ `firestore.indexes.json` - Index definitions
✅ `FIRESTORE_INDEXES_EXPLAINED.md` - Detailed explanation
✅ `INDEXES_SUMMARY.md` - This quick reference

## What's Already Single-Field (No Index Needed)

These queries work without composite indexes:
- ✅ `whereEqualTo("inviteCode")` - finding event by code
- ✅ `whereEqualTo("eventId")` - listing invitations for event
- ✅ Simple reads by document ID

## Performance Impact

- 📊 **Storage:** ~50 bytes per document per index
- ⚡ **Speed:** Queries go from "fails" to "milliseconds"
- 💰 **Cost:** Free for small apps (under 1GB storage)

## Next Steps

1. **Deploy indexes:**
   ```bash
   firebase deploy --only firestore:indexes
   ```

2. **Wait for build** (~2-5 minutes)

3. **Test your app:**
   - Load home screen (hosting/invited tabs)
   - View event photo gallery
   - Join event with invite code
   - QR check-in

4. **Verify:** Check for no "FAILED_PRECONDITION" errors in logcat

## Questions?

See detailed explanations in: `FIRESTORE_INDEXES_EXPLAINED.md`




