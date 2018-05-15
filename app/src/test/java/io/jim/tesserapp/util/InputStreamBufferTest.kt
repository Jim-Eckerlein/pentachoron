package io.jim.tesserapp.util

import org.junit.Assert.assertEquals
import org.junit.Test

class InputStreamBufferTest {

    private val buffer = InputStreamBuffer(allocationGranularity = 2, vectorsPerElement = 2)

    @Test
    fun initialCapacity() {
        assertEquals(2, buffer.elementCapacity)
        assertEquals(0f, buffer[0, 0, 0], 0.1f)
        assertEquals(0f, buffer[0, 0, 1], 0.1f)
        assertEquals(0f, buffer[1, 0, 0], 0.1f)
        assertEquals(0f, buffer[1, 0, 1], 0.1f)
    }

    @Test(expected = RuntimeException::class)
    fun invalidElementIndex() {
        buffer[3, 0, 0]
    }

    @Test(expected = RuntimeException::class)
    fun invalidVectorIndex() {
        buffer[0, 8, 0]
    }

    @Test(expected = RuntimeException::class)
    fun invalidFloatIndex() {
        buffer[0, 0, 8]
    }

    @Test(expected = RuntimeException::class)
    fun invalidElement() {
        buffer.record {
            buffer.write(3f, 4f, 5f, 1f)
        }
    }

    @Test
    fun writeRead() {
        buffer.record {
            buffer.write(8f, 9f, 0f, 0f)
            buffer.write(0f, 0f, 0f, 0f)

            buffer.write(1f, 2f, 0f, 0f)
            buffer.write(0f, 0f, 0f, 0f)
        }

        assertEquals(1f, buffer[1, 0, 0], 0.1f)
        assertEquals(2f, buffer[1, 0, 1], 0.1f)
    }

    @Test
    fun increaseMemoryAndPreserveOldData() {

        buffer.record {
            buffer.write(4f, 5f, 0f, 0f)
            buffer.write(0f, 0f, 0f, 0f)

            buffer.write(7f, 8f, 0f, 0f)
            buffer.write(0f, 0f, 0f, 0f)

            buffer.write(1f, 2f, 0f, 0f)
            buffer.write(0f, 0f, 0f, 0f)
        }

        assertEquals(4, buffer.elementCapacity)

        assertEquals(4f, buffer[0, 0, 0], 0.1f)
        assertEquals(5f, buffer[0, 0, 1], 0.1f)

        assertEquals(7f, buffer[1, 0, 0], 0.1f)
        assertEquals(8f, buffer[1, 0, 1], 0.1f)

        assertEquals(1f, buffer[2, 0, 0], 0.1f)
        assertEquals(2f, buffer[2, 0, 1], 0.1f)
    }

}
