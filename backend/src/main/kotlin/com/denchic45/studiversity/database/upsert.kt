package com.denchic45.studiversity.database

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.statements.BatchInsertStatement
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.transactions.TransactionManager

/**
 * Example:
 * val item = ...
 * MyTable.upsert {
 * 	it[id] = item.id
 *	it[value1] = item.value1
 * }
 */

//fun <T : Table> T.upsert(
//    vararg keys: Column<*> = (primaryKey ?: throw IllegalArgumentException("primary key is missing")).columns,
//    body: T.(InsertStatement<Number>) -> Unit
//) =
//    InsertOrUpdate<Number>(this, keys = keys).apply {
//        body(this)
//        execute(TransactionManager.current())
//    }

//class InsertOrUpdate<Key : Any>(
//    table: Table,
//    isIgnore: Boolean = false,
//    private vararg val keys: Column<*>
//) : InsertStatement<Key>(table, isIgnore) {
//    override fun prepareSQL(transaction: Transaction, prepared: Boolean): String {
//        val tm = TransactionManager.current()
//        val updateSetter = (table.columns - keys).joinToString { "${tm.identity(it)} = EXCLUDED.${tm.identity(it)}" }
//        val onConflict = "ON CONFLICT (${keys.joinToString { tm.identity(it) }}) DO UPDATE SET $updateSetter"
//        return "${super.prepareSQL(transaction)} $onConflict"
//    }
//}

//@Deprecated("")
/**
 * Example:
 * val items = listOf(...)
 * MyTable.batchUpsert(items) { table, item  ->
 * 	table[id] = item.id
 *	table[value1] = item.value1
 * }
 */

//fun <T : Table, E> T.batchUpsert(
//    data: Collection<E>,
//    vararg keys: Column<*> = (primaryKey ?: throw IllegalArgumentException("primary key is missing")).columns,
//    body: T.(BatchInsertStatement, E) -> Unit
//) =
//    BatchInsertOrUpdate(this, keys = keys).apply {
//        data.forEach {
//            addBatch()
//            body(this, it)
//        }
//        execute(TransactionManager.current())
//    }

//class BatchInsertOrUpdate(
//    table: Table,
//    isIgnore: Boolean = false,
//    private vararg val keys: Column<*>
//) : BatchInsertStatement(table, isIgnore, shouldReturnGeneratedValues = false) {
//    override fun prepareSQL(transaction: Transaction): String {
//        val tm = TransactionManager.current()
//        val updateSetter = (table.columns - keys).joinToString { "${tm.identity(it)} = EXCLUDED.${tm.identity(it)}" }
//        val onConflict = "ON CONFLICT (${keys.joinToString { tm.identity(it) }}) DO UPDATE SET $updateSetter"
//        return "${super.prepareSQL(transaction)} $onConflict"
//    }
//}