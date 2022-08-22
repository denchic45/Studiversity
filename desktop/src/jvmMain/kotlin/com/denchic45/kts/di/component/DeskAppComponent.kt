package com.denchic45.kts.di.component

import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Inject
import me.tatarka.inject.annotations.Provides

@Component
abstract class DeskAppComponent {

    abstract val repo: Repository

    @Provides
    protected fun user(): UserModel = UserModel(111,"Ivan...")
}

@Inject
data class UserModel(val id:Int,val name:String)

@Inject
class Repository(userModel: UserModel) {

    init {
        println(userModel)
    }

}