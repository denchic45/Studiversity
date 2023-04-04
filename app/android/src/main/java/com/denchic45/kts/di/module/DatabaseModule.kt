package com.denchic45.kts.di.module

import android.content.Context
import com.denchic45.kts.AppDatabase
import com.denchic45.kts.data.db.local.DbHelper
import com.denchic45.kts.data.db.local.DriverFactory
import com.denchic45.kts.data.service.NetworkService
import app.cash.sqldelight.db.SqlDriver
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [DataBindModule::class, FirebaseModule::class])
object DatabaseModule {

//    @Provides
//    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

//    @Provides
//    fun provideUserRemoteDataSource(firestore: FirebaseFirestore): UserRemoteDataSource {
//        return UserRemoteDataSource(firestore)
//    }


//    @Provides
//    fun provideGroupRemoteDataSource(firestore: FirebaseFirestore): GroupRemoteDataSource {
//        return GroupRemoteDataSource(firestore)
//    }

//    @Provides
//    fun provideCourseRemoteDataSource(
//        firestore: FirebaseFirestore,
//        groupRemoteDataSource: GroupRemoteDataSource,
//    ): CourseRemoteDataSource {
//        return CourseRemoteDataSource(firestore, groupRemoteDataSource)
//    }

//    @Provides
//    fun provideSubjectRemoteDataSource(
//        firestore: FirebaseFirestore,
//        networkService: NetworkService,
//        appVersionService: AppVersionService,
//        courseRemoteDataSource: CourseRemoteDataSource,
//    ): SubjectRemoteDataSource {
//        return SubjectRemoteDataSource(
//            firestore,
//            networkService,
//            appVersionService,
//            courseRemoteDataSource
//        )
//    }

    @Provides
    fun provideNetwork(context: Context) = NetworkService(context)

//    @Provides
//    fun provideSpecialtyRemoteDataSource(firestore: FirebaseFirestore): SpecialtyRemoteDataSource {
//        return SpecialtyRemoteDataSource(firestore)
//    }

//    @Provides
//    fun provideEventRemoteDataSource(firestore: FirebaseFirestore): EventRemoteDataSource {
//        return EventRemoteDataSource(firestore)
//    }


    @Singleton
    @Provides
    fun provideSqlDriver(context: Context): SqlDriver {
        return DriverFactory(context).driver
    }

    @Singleton
    @Provides
    fun provideDbHelper(sqlDriver: SqlDriver): DbHelper {
        return DbHelper(sqlDriver)
    }

    @Singleton
    @Provides
    fun provideAppDatabase(dbHelper: DbHelper): AppDatabase {
        return dbHelper.database
    }

//    @Provides
//    fun provideUserLocalDataSource(appDatabase: AppDatabase): UserLocalDataSource {
//        return UserLocalDataSource(appDatabase)
//    }

//    @Provides
//    fun provideGroupLocalDataSource(appDatabase: AppDatabase): GroupLocalDataSource {
//        return GroupLocalDataSource(appDatabase)
//    }

//    @Provides
//    fun provideCourseLocalDataSource(appDatabase: AppDatabase): CourseLocalDataSource {
//        return CourseLocalDataSource(appDatabase)
//    }

//    @Provides
//    fun provideSubmissionCommentLocalDataSource(appDatabase: AppDatabase): SubmissionCommentLocalDataSource {
//        return SubmissionCommentLocalDataSource(appDatabase)
//    }

//    @Provides
//    fun provideContentCommentLocalDataSource(appDatabase: AppDatabase): ContentCommentLocalDataSource {
//        return ContentCommentLocalDataSource(appDatabase)
//    }

//    @Provides
//    fun provideCourseContentLocalDataSource(appDatabase: AppDatabase): CourseContentLocalDataSource {
//        return CourseContentLocalDataSource(appDatabase)
//    }

//    @Provides
//    fun provideSpecialtyLocalDataSource(appDatabase: AppDatabase): SpecialtyLocalDataSource {
//        return SpecialtyLocalDataSource(appDatabase)
//    }

//    @Provides
//    fun provideSubmissionLocalDataSource(appDatabase: AppDatabase): SubmissionLocalDataSource {
//        return SubmissionLocalDataSource(appDatabase)
//    }

//    @Provides
//    fun provideSectionLocalDataSource(appDatabase: AppDatabase): SectionLocalDataSource {
//        return SectionLocalDataSource(appDatabase)
//    }

//    @Provides
//    fun provideGroupCourseLocalDataSource(appDatabase: AppDatabase): GroupCourseLocalDataSource {
//        return GroupCourseLocalDataSource(appDatabase)
//    }

//    @Provides
//    fun provideEventLocalDataSource(appDatabase: AppDatabase): EventLocalDataSource {
//        return EventLocalDataSource(appDatabase)
//    }

//    @Provides
//    fun provideTeacherEventLocalDataSource(appDatabase: AppDatabase): TeacherEventLocalDataSource {
//        return TeacherEventLocalDataSource(appDatabase)
//    }

//    @Provides
//    fun provideDayLocalDataSource(appDatabase: AppDatabase): DayLocalDataSource {
//        return DayLocalDataSource(appDatabase)
//    }

//    @Provides
//    fun provideSubjectLocalDataSource(appDatabase: AppDatabase): SubjectLocalDataSource {
//        return SubjectLocalDataSource(appDatabase)
//    }

//    @Singleton
//    @Provides
//    fun provideDataBase(context: Context): DataBase = DataBase.getInstance(context)

//    @Provides
//    fun provideSubjectDao(dataBase: DataBase): SubjectDao = dataBase.subjectDao()
//
//    @Provides
//    fun provideUserDao(dataBase: DataBase): UserDao = dataBase.userDao()
//
//    @Provides
//    fun provideCourseDao(dataBase: DataBase): CourseDao = dataBase.courseDao()
//
//    @Provides
//    fun provideCourseContentDao(dataBase: DataBase): CourseContentDao = dataBase.courseContentDao()
//
//    @Provides
//    fun provideGroupDao(dataBase: DataBase): GroupDao = dataBase.groupDao()
//
//    @Provides
//    fun provideGroupCourseDao(dataBase: DataBase): GroupCourseDao = dataBase.groupCourseDao()
//
//    @Provides
//    fun provideLessonDao(dataBase: DataBase): EventDao = dataBase.lessonDao()
//
//    @Provides
//    fun provideSpecialtyDao(dataBase: DataBase): SpecialtyDao = dataBase.specialtyDao()
//
//    @Provides
//    fun provideTeacherEventDao(dataBase: DataBase): TeacherEventDao = dataBase.teacherEventDao()
//
//    @Provides
//    fun provideDayDao(dataBase: DataBase): DayDao = dataBase.dayDao()
//
//    @Provides
//    fun provideSectionDao(dataBase: DataBase): SectionDao = dataBase.sectionDao()
//
//    @Provides
//    fun provideSubmissionDao(dataBase: DataBase): SubmissionDao = dataBase.submissionDao()
//
//    @Provides
//    fun provideSubmissionCommentDao(dataBase: DataBase): SubmissionCommentDao =
//        dataBase.submissionCommentDao()
//
//    @Provides
//    fun provideContentCommentDao(dataBase: DataBase): ContentCommentDao =
//        dataBase.contentCommentDao()

}

@Module
interface DataBindModule {

//    @Binds
//    fun bindNetworkService(service: NetworkService): NetworkService

//    @Binds
//    fun provideUserRemoteDataSource(userRemoteDataSource: UserRemoteDataSource): UserRemoteDataSource
}