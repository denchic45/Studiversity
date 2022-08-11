package com.denchic45.kts.di.module

import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides

@Module
object StorageModule {

    @Provides
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()

//    @Provides
//    fun provideUserRemoteStorage(storage: FirebaseStorage) = UserRemoteStorage(storage)

//    @Provides
//    fun provideSubjectRemoteStorage(storage: FirebaseStorage) = SubjectRemoteStorage(storage)


//    @Provides
//    fun provideContentAttachmentRemoteStorage(
//        storage: FirebaseStorage,
//        httpClient: HttpClient,
//    ) = ContentAttachmentRemoteStorage(storage, httpClient)

//    @Provides
//    fun provideSubmissionAttachmentRemoteStorage(
//        storage: FirebaseStorage,
//        httpClient: HttpClient,
//    ) = SubmissionAttachmentRemoteStorage(storage, httpClient)

//    @Provides
//    fun provideSubmissionAttachmentLocalStorage() = SubmissionAttachmentLocalStorage()


}