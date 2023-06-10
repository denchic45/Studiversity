package com.denchic45.studiversity.database.expression

import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.QueryBuilder
import java.time.temporal.Temporal

class NowTimestamp<T : Temporal> : Expression<T>() {
    override fun toQueryBuilder(queryBuilder: QueryBuilder) = queryBuilder {
        +"NOW()"
    }
}