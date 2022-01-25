package com.denchic45.kts.di.modules

import android.content.Context
import androidx.room.Room
import com.denchic45.kts.data.DataBase
import com.denchic45.kts.data.dao.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object DatabaseModule {

    @Provides
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Singleton
    @Provides
    fun provideDataBase(context: Context): DataBase = Room.databaseBuilder(
        context,
        DataBase::class.java, "database.db"
    )
//        .addCallback(object : RoomDatabase.Callback() {
//        override fun onCreate(db: SupportSQLiteDatabase) {
//            Executors.newSingleThreadExecutor().execute {
//                val dataBase = provideDataBase(context)
//                GlobalScope.launch {
//
//                    cancel()
//                }
//            }
//        }
//    })
        .build()

    @Provides
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()

    @Provides
    fun provideSubjectDao(dataBase: DataBase): SubjectDao = dataBase.subjectDao()

    @Provides
    fun provideUserDao(dataBase: DataBase): UserDao = dataBase.userDao()

    @Provides
    fun provideCourseDao(dataBase: DataBase): CourseDao = dataBase.courseDao()

    @Provides
    fun provideCourseContentDao(dataBase: DataBase): CourseContentDao = dataBase.courseContentDao()

    @Provides
    fun provideGroupDao(dataBase: DataBase): GroupDao = dataBase.groupDao()

    @Provides
    fun provideGroupCourseDao(dataBase: DataBase): GroupCourseDao = dataBase.groupCourseDao()

    @Provides
    fun provideLessonDao(dataBase: DataBase): LessonDao = dataBase.lessonDao()

    @Provides
    fun provideSpecialtyDao(dataBase: DataBase): SpecialtyDao = dataBase.specialtyDao()

    @Provides
    fun provideTeacherEventDao(dataBase: DataBase): TeacherEventDao = dataBase.teacherEventDao()

    @Provides
    fun provideDayDao(dataBase: DataBase): DayDao = dataBase.dayDao()

    @Provides
    fun provideSectionDao(dataBase: DataBase): SectionDao = dataBase.sectionDao()

    @Provides
    fun provideSubmissionDao(dataBase: DataBase): SubmissionDao = dataBase.submissionDao()

    @Provides
    fun provideSubmissionCommentDao(dataBase: DataBase): SubmissionCommentDao =
        dataBase.submissionCommentDao()

    @Provides
    fun provideContentCommentDao(dataBase: DataBase): ContentCommentDao =
        dataBase.contentCommentDao()
}