package com.github.darkxanter

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant

public open class KotlockTable(tableName: String = "kotlocks"): Table(tableName) {
    public val name: Column<String> = text("name")
    public val lockUntil: Column<Instant> = timestamp("lock_until")
    public val lockedAt: Column<Instant> = timestamp("locked_at")
    public val lockedBy: Column<String> = text("locked_by")

    public override val primaryKey: PrimaryKey = PrimaryKey(name)
}

//CREATE TABLE kotlocks(name VARCHAR(64) NOT NULL, lock_until TIMESTAMPTZ NOT NULL,
//locked_at TIMESTAMPTZ NOT NULL, locked_by VARCHAR(255) NOT NULL, PRIMARY KEY (name));

