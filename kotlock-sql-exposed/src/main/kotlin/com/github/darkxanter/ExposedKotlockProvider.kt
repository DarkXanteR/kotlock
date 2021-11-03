package com.github.darkxanter

import com.github.darkxanter.sql.upsert
import kotlinx.coroutines.CoroutineDispatcher
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.update
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId

public class ExposedKotlockProvider(
    private val db: Database? = null,
    private val context: CoroutineDispatcher? = null,
    private val transactionIsolation: Int? = null,
) : KotlockProvider {
    override suspend fun tryLock(challenger: KotlockChallenger): KotlockResult {
        return runCatching {
            val now = Instant.now()
            val insertedCount = newSuspendedTransaction(db = db, context = context, transactionIsolation = transactionIsolation) {
                val result = KotlockTable.upsert({
                    KotlockTable.lockUntil less now and (KotlockTable.name eq challenger.name)
                }) {
                    it[lockUntil] = challenger.lockUntil.toInstant()
                    it[lockedAt] = Instant.now()
                    it[lockedBy] = challenger.lockBy
                    it[name] = challenger.name
                }
                result.insertedCount
            }
            if (insertedCount > 0)
                challenger.success(OffsetDateTime.ofInstant(now, ZoneId.systemDefault()))
            else
                challenger.failure()
        }.getOrElse {
            challenger.failure(it)
        }
    }

    override suspend fun release(kotlock: Kotlock) {
        newSuspendedTransaction(db = db, context = context, transactionIsolation = transactionIsolation) {
            KotlockTable.update ({
                KotlockTable.lockedBy eq kotlock.lockedBy and (KotlockTable.name eq kotlock.name)
            }) {
                it[lockUntil] = kotlock.lockedUntil.toInstant()
            }
        }
    }
}