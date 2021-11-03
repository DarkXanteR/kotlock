package com.github.darkxanter

import java.time.OffsetDateTime

public data class Kotlock(
    public val name: String,
    public val lockedBy: String,
    public val lockedAt: OffsetDateTime,
    public val lockedUntil: OffsetDateTime
)