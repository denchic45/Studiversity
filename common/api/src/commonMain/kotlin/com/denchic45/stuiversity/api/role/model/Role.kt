package com.denchic45.stuiversity.api.role.model

import kotlinx.serialization.Serializable

@Serializable
data class Role(val id: Long, val resource: String) {

    override fun toString(): String = resource

    companion object {
        val Guest: Role = Role(1, "guest")
        val User: Role = Role(2, "user")
        val Student: Role = Role(3, "student")
        val Teacher: Role = Role(4, "teacher")
        val Moderator: Role = Role(5, "moderator")
        val Headman: Role = Role(6, "headman")
        val Curator: Role = Role(7, "curator")
    }
}