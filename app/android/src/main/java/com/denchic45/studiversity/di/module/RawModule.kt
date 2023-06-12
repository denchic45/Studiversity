package com.denchic45.studiversity.di.module

import android.content.Context
import com.denchic45.studiversity.R
import com.denchic45.studiversity.data.model.domain.ListItem
import com.denchic45.studiversity.util.JsonUtil
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
object RawModule {

    @Named("options_user")
    @Provides
    fun provideUserOptions(context: Context): List<ListItem>  =
        JsonUtil.parseToList(context,R.raw.options_user)

    @Named("options_group")
    @Provides
    fun provideGroupOptions(context: Context): List<ListItem> =
        JsonUtil.parseToList(context,R.raw.options_group)

    @Named("options_subject")
    @Provides
    fun provideSubjectOptions(context: Context): List<ListItem>  =
       JsonUtil.parseToList(context,R.raw.options_subject)

    @Named("genders")
    @Provides
    fun provideGenders(context: Context): List<ListItem> =
        JsonUtil.parseToList(context, R.raw.genders)

    @Named("courses")
    @Provides
    fun provideCourses(context: Context): List<ListItem> =
        JsonUtil.parseToList(context, R.raw.courses)

}