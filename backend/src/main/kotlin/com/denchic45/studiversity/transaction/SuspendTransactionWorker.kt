package com.denchic45.studiversity.transaction

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

interface TransactionWorker {
    operator fun <T> invoke(block: () -> T): T
}

interface SuspendTransactionWorker {
    suspend operator fun <T> invoke(block: suspend () -> T): T
}

//class StubTransactionWorker : TransactionWorker {
//    override fun <T> invoke(block: () -> T): T = block()
//}

class DatabaseTransactionWorker : TransactionWorker {
    override fun <T> invoke(block: () -> T): T = transaction { block() }
}

class DatabaseSuspendedTransactionWorker : SuspendTransactionWorker {
    override suspend fun <T> invoke(block: suspend () -> T): T = newSuspendedTransaction(Dispatchers.IO) { block() }
}