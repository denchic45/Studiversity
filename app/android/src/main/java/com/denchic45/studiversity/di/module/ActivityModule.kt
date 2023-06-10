package com.denchic45.studiversity.di.module

import com.denchic45.studiversity.ui.login.LoginActivity
import com.denchic45.studiversity.ui.profile.fullAvatar.FullAvatarActivity
import com.denchic45.studiversity.ui.splash.SplashActivity
import com.denchic45.studiversity.ui.usereditor.UserEditorFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface ActivityModule {
//    @ContributesAndroidInjector
//    fun contributeMainActivity(): OldMainActivity

    @ContributesAndroidInjector
    fun contributeSplashActivity(): SplashActivity

    @ContributesAndroidInjector
    fun contributeLoginActivity(): LoginActivity

    @ContributesAndroidInjector(modules = [IntentModule::class, RawModule::class])
    fun contributeUserEditorActivity(): UserEditorFragment

//    @ContributesAndroidInjector
//    fun contributeEventEditorActivity(): EventEditorActivity

    @ContributesAndroidInjector(modules = [IntentModule::class])
    fun contributeFullAvatarActivity(): FullAvatarActivity
}