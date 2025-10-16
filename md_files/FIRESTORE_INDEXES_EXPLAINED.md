# Firestore Indexes Explained

## Why Indexes Are Needed

Firestore requires **composite indexes** when you combine:
- Multiple `where` clauses
- `where` clause + `orderBy` on different fields
- `arrayContains` + `orderBy`

Without these indexes, your queries will fail with an error.

## Required Indexes for Your App

### 1. **Events Collection**

#### Index 1: Host Events Query
```kotlin
// EventRepository.kt - Line 117-118
whereEqualTo("hostId", currentUserId)
.orderBy("date", DESCENDING)
```
**Why needed:** Combines equality filter with ordering on different field
**Used for:** Showing events you're hosting on home screen

#### Index 2: Invited Events Query
```kotlin
// EventRepository.kt - Line 132-133
whereArrayContains("guestIds", currentUserId)
.orderBy("date", DESCENDING)
```
**Why needed:** `arrayContains` always requires composite index when combined with `orderBy`
**Used for:** Showing events you're invited to

#### Field Override: Invite Code
```kotlin
// EventRepository.kt - Line 148
whereEqualTo("inviteCode", inviteCode.uppercase())
```
**Why needed:** Single equality query on non-ID field needs explicit index
**Used for:** Joining events via invite code

### 2. **Photos Collection**

#### Index: Event Photos Query
```kotlin
// PhotoRepository.kt - Line 97-98
whereEqualTo("eventId", eventId)
.orderBy("uploadedAt", DESCENDING)
```
**Why needed:** Filter + sort on different fields
**Used for:** Displaying photos in event gallery, sorted by newest first

### 3. **Invitations Collection**

#### Index 1: User-Event Invitation Lookup
```kotlin
// InvitationRepository.kt - Line 87-88
whereEqualTo("userId", userId)
.whereEqualTo("eventId", eventId)
```
**Why needed:** Multiple equality filters on different fields
**Used for:** Checking if user has invitation for specific event

#### Index 2: QR Token Verification
```kotlin
// InvitationRepository.kt - Line 119-120
whereEqualTo("qrToken", qrToken)
.whereEqualTo("eventId", eventId)
```
**Why needed:** Multiple equality filters
**Used for:** Validating QR codes for event check-in

#### Index 3: Event Invitations by Status (Optional, for future)
**Why needed:** Filter invitations by event and status (accepted/pending/declined)
**Use case:** Show only accepted guests, or count pending RSVPs

#### Index 4: Checked-In Guests (Optional, for future)
**Why needed:** Filter guests who have checked in
**Use case:** See who's currently at the event

## Index Performance Impact

### Query Performance
- ✅ **With indexes:** Queries execute in milliseconds
- ❌ **Without indexes:** Query fails with "FAILED_PRECONDITION" error

### Storage Impact
- 📊 **Minimal overhead:** ~1-2% of document size per index
- 💾 **Worth it:** Dramatically faster queries

### Write Performance
- ⚡ **Negligible impact:** Firestore updates indexes automatically
- 🔄 **Automatic:** No manual maintenance needed

## How to Deploy Indexes

### Method 1: Firebase CLI (Recommended)

```bash
# Make sure Node v20 is active
export NVM_DIR="$HOME/.nvm"
source "$NVM_DIR/nvm.sh"
nvm use 20

# Navigate to project
cd /home/ani/Projects/Intra-View

# Deploy indexes
firebase deploy --only firestore:indexes
```

This will:
1. Upload `firestore.indexes.json` to Firebase
2. Build indexes in the background (may take a few minutes)
3. Enable fast queries once complete

### Method 2: Let Firestore Create Them Automatically

When you run a query that needs an index, Firestore will:
1. Show an error with a **direct link** to create the index
2. Click the link → Opens Firebase Console
3. Click "Create Index" → Done!

**Example error:**
```
FAILED_PRECONDITION: The query requires an index. 
You can create it here: https://console.firebase.google.com/...
```

### Method 3: Manual Creation in Console

1. Go to: https://console.firebase.google.com/project/lumen-f6699/firestore/indexes
2. Click "Create index"
3. Configure:
   - Collection: `events`
   - Fields:
     - `hostId` → Ascending
     - `date` → Descending
4. Click "Create"
5. Wait 2-5 minutes for index to build

## Index Build Status

After deploying, check index status:
```bash
firebase firestore:indexes
```

Or in console: https://console.firebase.google.com/project/lumen-f6699/firestore/indexes

Status indicators:
- 🟢 **Enabled:** Ready to use
- 🟡 **Building:** In progress (may take minutes for large datasets)
- 🔴 **Error:** Check configuration

## Index Configuration Details

### Events - Host Query Index
```json
{
  "collectionGroup": "events",
  "fields": [
    { "fieldPath": "hostId", "order": "ASCENDING" },
    { "fieldPath": "date", "order": "DESCENDING" }
  ]
}
```
**Size estimate:** ~50 bytes per event
**Build time:** < 1 minute for first 1000 events

### Events - Guest Query Index (Array Contains)
```json
{
  "collectionGroup": "events",
  "fields": [
    { "fieldPath": "guestIds", "arrayConfig": "CONTAINS" },
    { "fieldPath": "date", "order": "DESCENDING" }
  ]
}
```
**Special note:** Array indexes are more expensive (one entry per array element)
**Size estimate:** ~50 bytes × number of guests per event

### Photos - Event Gallery Index
```json
{
  "collectionGroup": "photos",
  "fields": [
    { "fieldPath": "eventId", "order": "ASCENDING" },
    { "fieldPath": "uploadedAt", "order": "DESCENDING" }
  ]
}
```
**Size estimate:** ~40 bytes per photo
**Build time:** < 30 seconds for first 1000 photos

## Testing Your Indexes

### 1. Deploy Indexes
```bash
firebase deploy --only firestore:indexes
```

### 2. Wait for Build Complete
Check console: Indexes should show "Enabled" status

### 3. Test Queries in App
- **Home screen:** Should load hosted and invited events ✅
- **Event gallery:** Should load photos sorted by date ✅
- **Join event:** Should find event by invite code ✅
- **QR check-in:** Should validate QR tokens ✅

### 4. Check Logs
No "FAILED_PRECONDITION" errors should appear!

## Common Issues

### ❌ "Query requires an index"
**Solution:** Deploy indexes or click the error link to create

### ❌ "Index building takes too long"
**Cause:** Large dataset (>10,000 documents)
**Solution:** Be patient, can take up to 30 minutes

### ❌ "Index already exists"
**Cause:** Duplicate index definition
**Solution:** Remove duplicate from `firestore.indexes.json`

### ❌ "Permission denied"
**Cause:** Security rules blocking query
**Solution:** Check `firestore.rules` allows the query

## Cost Considerations

### Free Tier Limits
- ✅ **Index storage:** 1 GB (plenty for most apps)
- ✅ **Index writes:** Same as document writes (no extra charge)

### Paid Tier
- 💰 **Index storage:** $0.18/GB/month
- 💰 **Minimal cost:** ~$0.01-0.10/month for small apps

**Bottom line:** Indexes are free for small-to-medium apps!

## Best Practices

### ✅ DO:
- Deploy indexes before launching to production
- Test queries with realistic data volumes
- Monitor index sizes in Firebase Console
- Remove unused indexes to save storage

### ❌ DON'T:
- Over-index (don't create "just in case" indexes)
- Ignore index errors (they prevent queries from working)
- Delete indexes in production without testing
- Create indexes on fields that are rarely queried

## Future Index Considerations

### When to Add More Indexes:

1. **Event filtering by date range:**
   ```kotlin
   where("date", ">", startDate)
   .where("date", "<", endDate)
   .orderBy("date")
   ```
   Needs index: `date` (ascending) + `date` (descending)

2. **Search by location:**
   ```kotlin
   where("location", "==", "New York")
   .orderBy("date")
   ```
   Needs index: `location` + `date`

3. **Filter by status:**
   ```kotlin
   where("status", "==", "ACCEPTED")
   .where("eventId", "==", eventId)
   ```
   Already included in optional indexes!

## Summary

✅ **7 composite indexes** configured
✅ **3 field overrides** for single-field queries
✅ **All current queries** covered
✅ **Future-proof** with optional indexes for common filters

Deploy with:
```bash
firebase deploy --only firestore:indexes
```

Then test your app - all queries should work smoothly! 🚀


