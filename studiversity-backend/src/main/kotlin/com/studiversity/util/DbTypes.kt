package com.studiversity.util

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.StringColumnType
import org.jetbrains.exposed.sql.Table

class VarCharMaxColumnType(collate: String? = null) : StringColumnType(collate) {
    override fun sqlType(): String = buildString {
        append("VARCHAR(MAX)")

        if (collate != null) {
            append(" COLLATE $collate")
        }
    }
}

fun Table.varcharMax(name: String, collate: String? = null): Column<String> =
    registerColumn(name, VarCharMaxColumnType(collate))