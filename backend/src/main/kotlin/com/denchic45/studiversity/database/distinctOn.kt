package com.denchic45.studiversity.database

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Function
import org.jetbrains.exposed.sql.QueryBuilder

fun <T> Column<T>.distinctOn(vararg extraColumns: Column<T>) = DistinctOn(this, extraColumns)

class DistinctOn<T>(expr: Column<T>, columns: Array<out Column<*>>) : Function<T>(expr.columnType) {

	private val distinctNames = listOf(expr, *columns)
		.joinToString(
			separator = ", ",
			transform = {
				"${it.table.tableName}.${it.name}"
			}
		)

	private val colName = expr.table.tableName + "." + expr.name

	override fun toQueryBuilder(queryBuilder: QueryBuilder) {
		queryBuilder {
			append("DISTINCT ON ($distinctNames) $colName ")
		}
	}
}