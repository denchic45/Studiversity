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
        Users, UsersRolesScopes, Roles, Scopes, Capabilities, VerificationTokens, ConfirmCodes, // 7
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

    ScopeTypes.importFromCSV(getDataframeFromCSV("scope_type"))
    Roles.importFromCSV(getDataframeFromCSV("role"))
    Capabilities.importFromCSV(getDataframeFromCSV("capability"))
    RolesCapabilities.importFromCSV(getDataframeFromCSV("role_capability"))
    RolesAssignments.importFromCSV(getDataframeFromCSV("role_assignment"))
}

private fun getDataframeFromCSV(tableName: String): DataFrame<*> {
    return DataFrame.readCSV(File("src/main/resources/installdata/$tableName.csv"))
}

fun Table.importFromCSV(
    dataframe: DataFrame<*>
) {
    batchUpsert(dataframe.rows()) { row ->
        columns.forEach { column ->
            this[column as Column<Any?>] = row[column.name]
        }
    }
}