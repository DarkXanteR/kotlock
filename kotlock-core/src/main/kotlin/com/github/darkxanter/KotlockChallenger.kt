@file:Suppress("unused")

package com.github.darkxanter

import java.time.OffsetDateTime

public data class KotlockChallenger(
    val name: String,
    val lockBy: String,
    val lockUntil: OffsetDateTime
)

public fun KotlockChallenger.success(lockedAt: OffsetDateTime = OffsetDateTime.now()): KotlockResult {
    return KotlockSuccess(Kotlock(name = name, lockedBy = lockBy, lockedAt = lockedAt, lockedUntil = lockUntil))
}

public fun KotlockChallenger.failure(throwable: Throwable? = null): KotlockResult = KotlockFailure(throwable)
