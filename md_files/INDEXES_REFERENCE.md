# Firestore Indexes Reference Table

## Index Overview

| # | Collection | Fields | Purpose | File:Line | Status |
|---|------------|--------|---------|-----------|--------|
| 1 | `events` | `hostId` ↑ + `date` ↓ | My hosted events, sorted by date | EventRepository.kt:117 | ⚠️ Required |
| 2 | `events` | `guestIds` (array) + `date` ↓ | Events I'm invited to | EventRepository.kt:132 | ⚠️ Required |
| 3 | `events` | `inviteCode` ↑ | Find event by code | EventRepository.kt:148 | ✅ Field override |
| 4 | `photos` | `eventId` ↑ + `uploadedAt` ↓ | Event gallery, newest first | PhotoRepository.kt:97 | ⚠️ Required |
| 5 | `invitations` | `userId` ↑ + `eventId` ↑ | Check user's invitation | InvitationRepository.kt:87 | ⚠️ Required |
| 6 | `invitations` | `qrToken` ↑ + `eventId` ↑ | Validate QR for check-in | InvitationRepository.kt:119 | ⚠️ Required |
| 7 | `invitations` | `eventId` ↑ + `status` ↑ | Filter by RSVP status | Future use | 💡 Optional |
| 8 | `invitations` | `eventId` ↑ + `checkedIn` ↑ | Show checked-in guests | Future use | 💡 Optional |

**Legend:**
- ↑ = Ascending order
- ↓ = Descending order
- (array) = Array-contains query
- ⚠️ = Must have (queries will fail without it)
- ✅ = Single field (simple override)
- 💡 = Nice to have (for future features)

## Query Patterns Explained

### Pattern 1: Equality + Sort (Different Fields)
```kotlin
where("field1", "==", value).orderBy("field2")
```
**Example:** `hostId == user123` + sort by `date`
**Needs:** Composite index

### Pattern 2: Array Contains + Sort
```kotlin
whereArrayContains("arrayField", value).orderBy("field2")
```
**Example:** `guestIds` contains `user123` + sort by `date`
**Needs:** Composite index (always!)

### Pattern 3: Multiple Equality Filters
```kotlin
where("field1", "==", value1).where("field2", "==", value2)
```
**Example:** `userId == user123` AND `eventId == event456`
**Needs:** Composite index

### Pattern 4: Single Equality
```kotlin
where("field", "==", value)
```
**Example:** `inviteCode == "ABC123"`
**Needs:** Field override (simple index)

## Index Configuration Breakdown

### Events: Host Query
```json
{
  "collectionGroup": "events",
  "fields": [
    { "fieldPath": "hostId", "order": "ASCENDING" },
    { "fieldPath": "date", "order": "DESCENDING" }
  ]
}
```
**Storage:** ~50 bytes per event
**Use case:** Home screen "Hosting" tab

### Events: Guest Query (Special!)
```json
{
  "collectionGroup": "events",
  "fields": [
    { "fieldPath": "guestIds", "arrayConfig": "CONTAINS" },
    { "fieldPath": "date", "order": "DESCENDING" }
  ]
}
```
**Storage:** ~50 bytes × avg guests per event
**Note:** Array indexes create entry for each array element!
**Use case:** Home screen "Invited" tab

### Photos: Gallery Query
```json
{
  "collectionGroup": "photos",
  "fields": [
    { "fieldPath": "eventId", "order": "ASCENDING" },
    { "fieldPath": "uploadedAt", "order": "DESCENDING" }
  ]
}
```
**Storage:** ~40 bytes per photo
**Use case:** Photo gallery, chronological display

### Invitations: Lookup Query
```json
{
  "collectionGroup": "invitations",
  "fields": [
    { "fieldPath": "userId", "order": "ASCENDING" },
    { "fieldPath": "eventId", "order": "ASCENDING" }
  ]
}
```
**Storage:** ~40 bytes per invitation
**Use case:** Check if user invited to specific event

### Invitations: QR Validation
```json
{
  "collectionGroup": "invitations",
  "fields": [
    { "fieldPath": "qrToken", "order": "ASCENDING" },
    { "fieldPath": "eventId", "order": "ASCENDING" }
  ]
}
```
**Storage:** ~40 bytes per invitation
**Use case:** QR code check-in at event entrance

## Size Estimates for Your App

Assuming typical event app usage:

| Collection | Docs/Event | Index Size/Event | Notes |
|------------|------------|------------------|-------|
| Events | 1 | ~150 bytes | 3 indexes (host, guest, inviteCode) |
| Photos | 50 | ~2 KB | 50 photos × 40 bytes |
| Invitations | 20 | ~2.4 KB | 20 guests × 3 indexes × 40 bytes |
| **Total** | - | **~4.5 KB/event** | Very affordable! |

**For 100 events:** ~450 KB of index storage
**For 1000 events:** ~4.5 MB of index storage

**Firestore free tier:** 1 GB storage (enough for 200,000+ events!)

## Deployment Commands

### Option 1: Deploy All Indexes
```bash
cd /home/ani/Projects/Intra-View
export NVM_DIR="$HOME/.nvm" && source "$NVM_DIR/nvm.sh" && nvm use 20
firebase deploy --only firestore:indexes
```

### Option 2: List Current Indexes
```bash
firebase firestore:indexes
```

### Option 3: Delete Unused Index
```bash
firebase firestore:indexes:delete <index-id>
```

## Monitoring Index Performance

### Check Index Status
```bash
firebase firestore:indexes --project lumen-f6699
```

### In Firebase Console
1. Go to: https://console.firebase.google.com/project/lumen-f6699/firestore/indexes
2. Check "Status" column:
   - 🟢 **Enabled:** Ready to use
   - 🟡 **Building:** In progress
   - 🔴 **Error:** Check configuration

### Build Time Estimates
- Empty database: < 30 seconds
- 100 documents: 1-2 minutes
- 1,000 documents: 2-5 minutes
- 10,000 documents: 10-30 minutes

## Testing Checklist

After deploying indexes, test these flows:

- [ ] **Home Screen - Hosting Tab**
  - Should load events you created
  - Sorted by date, newest first
  - Uses: `hostId` + `date` index

- [ ] **Home Screen - Invited Tab**
  - Should load events where you're a guest
  - Sorted by date, newest first
  - Uses: `guestIds` (array) + `date` index

- [ ] **Join Event with Code**
  - Enter invite code (e.g., "ABC123")
  - Should find and join event
  - Uses: `inviteCode` field override

- [ ] **Photo Gallery**
  - Open event → View photos
  - Should load in chronological order
  - Uses: `eventId` + `uploadedAt` index

- [ ] **QR Check-In** (if implemented)
  - Scan QR code at event
  - Should validate and check in
  - Uses: `qrToken` + `eventId` index

## Troubleshooting

### Error: "The query requires an index"
✅ **Solution:** Deploy indexes with command above

### Error: "Index already exists"
✅ **Solution:** Index is already created, you're good!

### Error: "Permission denied"
✅ **Solution:** Check `firestore.rules`, not an index issue

### Queries are slow
✅ **Solution:** Check index status (might still be building)

## Quick Reference Commands

```bash
# Deploy indexes
firebase deploy --only firestore:indexes

# Check status
firebase firestore:indexes

# List all Firebase projects
firebase projects:list

# Switch project
firebase use lumen-f6699
```

## Related Files

- 📄 `firestore.indexes.json` - Index definitions
- 📄 `firestore.rules` - Security rules
- 📄 `FIRESTORE_INDEXES_EXPLAINED.md` - Detailed guide
- 📄 `INDEXES_SUMMARY.md` - Quick summary
- 📄 `INDEXES_REFERENCE.md` - This file

---

**Ready to deploy?** Run:
```bash
firebase deploy --only firestore:indexes
```




