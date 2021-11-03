@file:Suppress("FunctionName")

package com.gitlab.darkxanter.tests

import com.github.darkxanter.KotlockProvider
import com.github.darkxanter.withLock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.time.Duration
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.assertEquals


public abstract class KotlockProviderTest {
    protected abstract val kotlockProvider: KotlockProvider

    @Test
    protected fun `lock at least for duration test`() {
        val counter = AtomicInteger(0)
        val times = 10

        runBlocking {
            repeat(times) {
                kotlockProvider.withLock(
                    name = "lock at least for duration",
                    atMostFor = Duration.ofMinutes(5),
                    atLeastFor = Duration.ofMinutes(1)
                ) {
                    counter.incrementAndGet()
                }
            }
        }
        assertEquals(1, counter.get())
    }

    @Test
    protected fun `lock at most for duration test`() {
        val counter = AtomicInteger(0)
        val times = 10

        runBlocking {
            repeat(times) {
                kotlockProvider.withLock(
                    name = "lock at most for duration",
                    atMostFor = Duration.ofMinutes(5),
                ) {
                    counter.incrementAndGet()
                }
            }
        }
        assertEquals(times, counter.get())
    }


    @Test
    protected fun `concurrent lock test`() {
        val counter = AtomicInteger(0)
        val times = 10

        runBlocking(Dispatchers.IO) {
            repeat(times) {
                launch {
                    delay(100)
                    kotlockProvider.withLock(
                        name = "concurrent lock",
                        atMostFor = Duration.ofMinutes(5),
                    ) {
                        delay(Duration.ofSeconds(1).toMillis())
                        counter.incrementAndGet()
                    }

                }
            }
        }
        assertEquals(1, counter.get())
    }

}