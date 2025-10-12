package com.eventapp.intraview.data.repository

import android.content.Context
import com.eventapp.intraview.data.model.User
import com.eventapp.intraview.util.Constants
import com.eventapp.intraview.util.Result
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    val currentUser: FirebaseUser?
        get() = auth.currentUser
    
    val currentUserId: String?
        get() = auth.currentUser?.uid
    
    fun isUserAuthenticated(): Boolean = auth.currentUser != null
    
    fun observeAuthState(): Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }
    
    suspend fun signInWithGoogle(account: GoogleSignInAccount): Result<User> {
        return try {
            android.util.Log.d("AuthRepository", "Starting Firebase sign-in with idToken")
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            val authResult = auth.signInWithCredential(credential).await()
            val firebaseUser = authResult.user ?: throw Exception("No user returned")
            
            android.util.Log.d("AuthRepository", "Firebase sign-in successful: uid=${firebaseUser.uid}, email=${firebaseUser.email}")
            
            // Create or update user document
            val user = User(
                userId = firebaseUser.uid,
                email = firebaseUser.email ?: "",
                displayName = firebaseUser.displayName ?: "",
                photoUrl = firebaseUser.photoUrl?.toString() ?: "",
                createdAt = Timestamp.now()
            )
            
            android.util.Log.d("AuthRepository", "Saving user to Firestore")
            firestore.collection(Constants.COLLECTION_USERS)
                .document(firebaseUser.uid)
                .set(user)
                .await()
            
            android.util.Log.d("AuthRepository", "User saved successfully, returning Success")
            Result.Success(user)
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "Sign-in failed: ${e.message}", e)
            Result.Error(e.message ?: "Sign in failed")
        }
    }
    
    suspend fun signOut() {
        auth.signOut()
    }
    
    suspend fun getCurrentUser(): User? {
        return try {
            val userId = currentUserId ?: return null
            firestore.collection(Constants.COLLECTION_USERS)
                .document(userId)
                .get()
                .await()
                .toObject(User::class.java)
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun getUserById(userId: String): User? {
        return try {
            firestore.collection(Constants.COLLECTION_USERS)
                .document(userId)
                .get()
                .await()
                .toObject(User::class.java)
        } catch (e: Exception) {
            null
        }
    }
    
    fun getGoogleSignInClient(context: Context): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("825057951463-tppogjhqf7ro0n4k9f2j4kjm9nbn1gnh.apps.googleusercontent.com")
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(context, gso)
    }
}


