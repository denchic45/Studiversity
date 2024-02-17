package com.denchic45.studiversity.setup

import com.denchic45.studiversity.database.table.*
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.batchUpsert
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.rows
import org.jetbrains.kotlinx.dataframe.io.readCSV
import java.io.File

fun setupDatabase() = transaction {
    // add tables
    SchemaUtils.createMissingTablesAndColumns(
        // user tables
        Users, UsersRolesScopes, Roles, Scopes, Capabilities, // 5
//        RolesScopes,
        RolesCapabilities, RolesAssignments, ScopeTypes, RefreshTokens, MagicLinks, // 5
        // study group tables
        StudyGroups, StudyGroupsMembers, Specialties, // 3
        // courses tables
        Courses, CourseTopics, CourseElements, CourseWorks, CoursesStudyGroups, // 5
        Subjects, Submissions, Grades, Enrollments, UserEnrollments, // 5
        // timetable
        Events, Lessons, Periods, PeriodsMembers, Rooms, // 5
        // attachments
        Attachments, AttachmentReferences // 2
    )

    // add scope types
//    val scopeTypeId by column<Long>("scope_type_id")
//    val scopeTypeName by column<String>("scope_type_name")
//    val scopeTypeParent by column<Long?>("parent")
//    val scopeTypesTable = DataFrame.readCSV(File("resources/installdata/scope_type.csv"))
//    scopeTypesTable.forEach { row ->
//        ScopeTypeDao.new(row[scopeTypeId]) {
//            name = row[scopeTypeName]
//            parent = row[scopeTypeParent]?.let { ScopeTypeDao[it] }
//        }
//    }

    // add roles
//    val roleId by column<Long>("role_id")
//    val roleName by column<String>("role_name")
//    val roleShortname by column<String>("shortname")
//    val roleScopeTypeId by column<Long>("scope_type_id")
//    val roleParent by column<Long?>("parent")
//    val roleOrder by column<Int?>("role_order")
//    val rolesTable = DataFrame.readCSV(File("resources/installdata/role.csv"))
//    rolesTable.forEach { row ->
//        RoleDao.new(row[roleId]) {
//            name = row[roleName]
//            shortname = row[roleShortname]
//            scopeType = ScopeTypeDao[row[roleScopeTypeId]]
//            parent = row[roleParent]?.let { RoleDao[it] }
//            order = row[roleOrder]
//        }
//    }

    // add capabilities
//    val capabilityId by column<Long>("capability_id")
//    val capabilityName by column<String>("capability_name")
//    val capabilityResource by column<String>("capability_resource")
//    val capabilityScopeTypeId by column<Long>("scope_type_id")
//    val capabilitiesTable = DataFrame.readCSV(File("resources/installdata/capability.csv"))
//    Capabilities.batchUpsert(capabilitiesTable.rows()) { row ->
//        this[Capabilities.id] = row[capabilityId]
//        this[Capabilities.name] = row[capabilityName]
//        this[Capabilities.resource] = row[capabilityResource]
//        this[Capabilities.scopeType] = row[capabilityScopeTypeId]
//    }

    // add role capabilities
//    val permission by column<Permission>()
//    val rolesCapabilitiesTable = DataFrame.readCSV(File("resources/installdata/role_capability.csv"))
//    RolesCapabilities.batchUpsert(rolesCapabilitiesTable.rows()) { row ->
//        this[RolesCapabilities.roleId] = row[roleId]
//        this[RolesCapabilities.capabilityResource] = row[capabilityResource]
//        this[RolesCapabilities.permission] = row[permission]
//    }

    // add roles assignments
//    val assignableRoleId by column<Long>()
//    val rolesAssignments = DataFrame.readCSV(File("resources/installdata/role_assignment.csv"))
//    RolesAssignments.batchUpsert(rolesAssignments.rows()) { row ->
//        this[RolesAssignments.roleId] = row[roleId]
//        this[RolesAssignments.assignableRoleId] = row[assignableRoleId]
//    }

    // add roles capabilities


    ScopeTypes.importFromCSV(DataFrame.readCSV(getFileWithCSV("scope_type")))
    Roles.importFromCSV(DataFrame.readCSV(getFileWithCSV("role")))
    Capabilities.importFromCSV(DataFrame.readCSV(getFileWithCSV("capability")))
    RolesCapabilities.importFromCSV(DataFrame.readCSV(getFileWithCSV("role_capability")))
    RolesAssignments.importFromCSV(DataFrame.readCSV(getFileWithCSV("role_assignment")))
}

private fun getFileWithCSV(tableName: String) = File("resources/installdata/$tableName.csv")

fun Table.importFromCSV(
    dataframe: DataFrame<*>
) {
    batchUpsert(dataframe.rows()) { row ->
        columns.forEach { column ->
            this[column as Column<Any?>] = row.get(column.name)
        }
    }
}