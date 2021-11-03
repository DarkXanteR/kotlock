package com.github.darkxanter

import java.net.InetAddress
import java.time.Duration
import java.time.OffsetDateTime

public interface KotlockProvider {
    public suspend fun tryLock(challenger: KotlockChallenger): KotlockResult
    public suspend fun release(kotlock: Kotlock)
}

public suspend inline fun <reified T> KotlockProvider.withLock(
    name: String,
    atMostFor: Duration,
    atLeastFor: Duration = Duration.ZERO,
    lockBy: String = hostname,
    ifLocked: () -> T? = { null },
    action: () -> T
): T? {
    require(name.isNotBlank()) { "Lock name must be not empty" }
    require(atMostFor > atLeastFor) { "Invalid lock durations: atMostFor must be greater than atLeastFor" }

    val now = OffsetDateTime.now()
    val lockAtLeastUntil = now + atLeastFor
    val lockAtMostUntil = now + atMostFor

    val lockResult = tryLock(
        KotlockChallenger(
            name = name,
            lockBy = lockBy,
            lockUntil = lockAtMostUntil
        )
    )

    return when (lockResult) {
        is KotlockSuccess -> try {
            action()
        } finally {
            val lockedUntil = when {
                lockAtLeastUntil.isAfter(OffsetDateTime.now()) -> lockAtLeastUntil
                else -> OffsetDateTime.now()
            }
            release(lockResult.kotlock.copy(lockedUntil = lockedUntil))
        }
        is KotlockFailure -> ifLocked()
    }
}

public val KotlockProvider.hostname: String by lazy {
    System.getenv("HOSTNAME")?.ifBlank { null }
        ?: System.getenv("COMPUTERNAME")?.ifBlank { null }
        ?: runCatching { InetAddress.getLocalHost().hostName }.getOrNull()
        ?: "localhost"
}