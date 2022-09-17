package com.denchic45.kts.data.service

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import me.tatarka.inject.annotations.Inject

@Inject
actual class AuthService @javax.inject.Inject constructor() {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    actual val isAuthenticated: Boolean
        get() = firebaseAuth.currentUser != null


    actual val observeIsAuthenticated: Flow<Boolean> = callbackFlow {
        val listener: (FirebaseAuth) -> Unit = {
            trySend(firebaseAuth.currentUser != null)
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose {
            firebaseAuth.removeAuthStateListener(listener)
        }
    }


    actual suspend fun signInWithEmailAndPassword(email: String, password: String) {
       try {
           println("AUTH: try... android")
           firebaseAuth.signInWithEmailAndPassword(email, password)
               .await()
       } catch (t:Throwable) {
          println( t.message)
       }
    }

    actual fun signOut() {
        firebaseAuth.signOut()
    }

    actual suspend fun createNewUser(email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).await()
    }

    actual suspend fun resetPassword(email: String) {
        firebaseAuth.sendPasswordResetEmail(email).await()
    }
}