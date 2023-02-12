package com.studiversity.transaction

import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

interface TransactionWorker {
    operator fun <T> invoke(block: () -> T): T
}

interface SuspendTransactionWorker {
    suspend fun <T> suspendInvoke(block: suspend () -> T): T
}

class StubTransactionWorker : TransactionWorker {
    override fun <T> invoke(block: () -> T): T = block()
}

class DatabaseTransactionWorker : TransactionWorker, SuspendTransactionWorker {
    override fun <T> invoke(block: () -> T): T = transaction { block() }
    override suspend fun <T> suspendInvoke(block: suspend () -> T): T = newSuspendedTransaction { block() }
}