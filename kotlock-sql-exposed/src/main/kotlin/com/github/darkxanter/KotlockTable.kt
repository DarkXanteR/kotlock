package com.github.darkxanter

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

internal object KotlockTable: Table("kotlocks") {
    val name = varchar("name", 64)
    val lockUntil = timestamp("lock_until")
    val lockedAt = timestamp("locked_at")
    val lockedBy = varchar("locked_by", 255)

    override val primaryKey = PrimaryKey(name)
}

//CREATE TABLE kotlocks(name VARCHAR(64) NOT NULL, lock_until TIMESTAMPTZ NOT NULL,
//locked_at TIMESTAMPTZ NOT NULL, locked_by VARCHAR(255) NOT NULL, PRIMARY KEY (name));

