package com.github.darkxanter.provider

import com.gitlab.darkxanter.tests.KotlockProviderTest
import com.mongodb.ConnectionString
import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

internal fun createMongoConnection(): MongoClient {
    return MongoClients.create(ConnectionString("mongodb://localhost:27017/kotlock"))
}

@Testcontainers
internal class MongoKotlockProviderTest : KotlockProviderTest() {
    @Container
    val container = GenericContainer(DockerImageName.parse("mongo:5-focal")).apply {
        withReuse(true)
    }

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