package com.denchic45.kts.di.module

import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides

@Module
object FirebaseModule {

    @Provides
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()
}