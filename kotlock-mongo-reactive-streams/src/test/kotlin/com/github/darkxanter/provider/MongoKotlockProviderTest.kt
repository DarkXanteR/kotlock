package com.github.darkxanter.provider

import com.gitlab.darkxanter.tests.KotlockProviderTest
import com.mongodb.ConnectionString
import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach

internal fun createMongoConnection(): MongoClient {
    return MongoClients.create(ConnectionString("mongodb://localhost:27017/kotlock"))
}

internal class MongoKotlockProviderTest : KotlockProviderTest() {
    private val db = "kotlock"
    private val collection = "kotlock"

    private val mongoClient by lazy { createMongoConnection() }
    override val kotlockProvider by lazy {
        MongoKotlockProvider(mongoClient, databaseName = db, collectionName = collection)
    }

    @BeforeEach
    fun cleanup() {
        runBlocking {
            mongoClient.getDatabase(db).getCollection(collection).drop().awaitFirstOrNull()
        }
    }


}