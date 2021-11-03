package com.github.darkxanter

import com.gitlab.darkxanter.tests.KotlockProviderTest
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

class ExposedKotlockProviderTest : KotlockProviderTest() {
    override val kotlockProvider = ExposedKotlockProvider()
    private var db: Database? = null
    @BeforeEach
    fun connect() {
        Database.connect("jdbc:postgresql://localhost:5433/test", user = "postgres", password = "postgres")
        transaction {
            SchemaUtils.drop(KotlockTable)
            SchemaUtils.createMissingTablesAndColumns(KotlockTable)
        }
    }

    @AfterEach
    fun disconnect() {
        db?.let {
            TransactionManager.closeAndUnregister(it)
        }
    }
}