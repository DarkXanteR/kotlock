package com.github.darkxanter

import java.net.InetAddress
import java.time.Duration
import java.time.OffsetDateTime

public abstract class KotlockProvider {
    public abstract suspend fun tryLock(challenger: KotlockChallenger): KotlockResult
    public abstract suspend fun release(kotlock: Kotlock)

    /**
     * @param name Lock name
     * @param atMostFor How long the lock should be kept in case the machine which obtained the lock died before releasing it.
     * @param atLeastFor The lock will be held at least for this duration. Can be used if you really need to execute the task
     * at most once in given period of time. If the duration of the task is shorter than clock difference between nodes, the task can
     * be theoretically executed more than once (one node after another). By setting this parameter, you can make sure that the
     * lock will be kept at least for given period of time.
     * @param lockBy Node name
     * @param ifLocked Callback when lock fails
     * @param action Action to execute
     * @return The action result or null if locked
     */
    public suspend inline fun <reified T> withLock(
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
}

public val KotlockProvider.hostname: String by lazy {
    System.getenv("HOSTNAME")?.ifBlank { null }
        ?: System.getenv("COMPUTERNAME")?.ifBlank { null }
        ?: runCatching { InetAddress.getLocalHost().hostName }.getOrNull()
        ?: "localhost"
}