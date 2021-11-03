package com.github.darkxanter.sql

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.IDateColumnType
import org.jetbrains.exposed.sql.Table
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

internal class JavaOffsetDateTimeColumnType : ColumnType(), IDateColumnType {
    override fun sqlType(): String = "TIMESTAMP WITH TIME ZONE"

    override fun nonNullValueToString(value: Any): String {
        val instant = when (value) {
            is String -> return value
            is OffsetDateTime -> return value.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            is java.sql.Timestamp -> Instant.ofEpochSecond(value.time / 1000, value.nanos.toLong())
            else -> error("Unexpected value: $value of ${value::class.qualifiedName}")
        }
        return "'${DateTimeFormatter.ISO_ZONED_DATE_TIME.format(instant)}'"
    }

    override fun valueFromDB(value: Any): Any = when (value) {
        is OffsetDateTime -> value
        is java.sql.Timestamp -> longToOffsetDateTime(value.time / 1000, value.nanos.toLong())
        is Int -> longToOffsetDateTime(value.toLong())
        is Long -> longToOffsetDateTime(value)
        is String -> OffsetDateTime.parse(value, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        else -> valueFromDB(value.toString())
    }

    override fun notNullValueToDB(value: Any): Any = when (value) {
        is OffsetDateTime -> java.sql.Timestamp(value.toInstant().toEpochMilli())
        else -> value
    }

    override val hasTimePart: Boolean
        get() = true

    private fun longToOffsetDateTime(millis: Long) = OffsetDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneOffset.systemDefault())
    private fun longToOffsetDateTime(seconds: Long, nanos: Long) = OffsetDateTime.ofInstant(Instant.ofEpochSecond(seconds, nanos), ZoneOffset.systemDefault())
}

internal fun Table.offsetDateTime(name: String): Column<OffsetDateTime> = registerColumn(name, JavaOffsetDateTimeColumnType())