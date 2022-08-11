package com.denchic45.kts.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthService @Inject constructor() {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    fun signOut() {
        firebaseAuth.signOut()
    }

    val listenAuthState: Flow<Boolean> = callbackFlow {
        val listener: (FirebaseAuth) -> Unit = {
            trySend(firebaseAuth.currentUser != null)
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose {
            firebaseAuth.removeAuthStateListener(listener)
        }
    }


    suspend fun signInWithEmailAndPassword(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .await()
    }

    fun createNewUser(email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.exception != null) {
                it.exception!!.printStackTrace()
            }
        }
    }

    suspend fun resetPassword(email: String) {
        firebaseAuth.sendPasswordResetEmail(email).await()
    }
}