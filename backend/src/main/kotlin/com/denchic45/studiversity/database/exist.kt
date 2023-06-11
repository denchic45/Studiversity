package com.denchic45.studiversity.database

import org.jetbrains.exposed.sql.*

fun <T : FieldSet> T.exists(where: SqlExpressionBuilder.() -> Op<Boolean>): Boolean {
    val existsOp = exists(this.select(where))
    val result = Table.Dual.slice(existsOp).selectAll().first()
    return result[existsOp]
}