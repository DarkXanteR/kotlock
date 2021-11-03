package com.github.darkxanter.provider

import com.github.darkxanter.Kotlock
import com.github.darkxanter.KotlockChallenger
import com.github.darkxanter.KotlockProvider
import com.github.darkxanter.KotlockResult
import com.github.darkxanter.failure
import com.github.darkxanter.success
import com.mongodb.client.model.Filters.and
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Filters.lte
import com.mongodb.client.model.FindOneAndUpdateOptions
import com.mongodb.client.model.Updates
import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoCollection
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.bson.Document
import java.time.Instant
import java.time.OffsetDateTime

public class MongoKotlockProvider(
    private val client: MongoClient,
    private val databaseName: String = "kotlock",
    private val collectionName: String = "kotlocks",
) : KotlockProvider {
    private companion object {
        private const val idField: String = "_id"
        private const val lockedUntilField: String = "lockedUntil"
        private const val lockedAtField: String = "lockedAt"
        private const val lockedByField: String = "lockedBy"
    }

    override suspend fun tryLock(challenger: KotlockChallenger): KotlockResult {
        return runCatching {
            val lockTime = OffsetDateTime.now()
            getCollection().findOneAndUpdate(
                and(eq(idField, challenger.name), lte(lockedUntilField, Instant.now())),
                Updates.combine(
                    Updates.set(lockedByField, challenger.lockBy),
                    Updates.set(lockedUntilField, challenger.lockUntil.toInstant()),
                    Updates.set(lockedAtField, lockTime.toInstant()),
                ),
                FindOneAndUpdateOptions().upsert(true)
            ).awaitFirstOrNull()
            challenger.success(lockedAt = lockTime)
        }.getOrElse {
            challenger.failure(throwable = it)
        }
    }

    override suspend fun release(kotlock: Kotlock) {
        getCollection().findOneAndUpdate(
            and(eq(idField, kotlock.name), lte(lockedByField, kotlock.lockedBy)),
            Updates.combine(Updates.set(lockedUntilField, kotlock.lockedUntil.toInstant()))
        ).awaitFirstOrNull()
    }

    private fun getCollection(): MongoCollection<Document> {
        return client
            .getDatabase(databaseName)
            .getCollection(collectionName)
    }
}