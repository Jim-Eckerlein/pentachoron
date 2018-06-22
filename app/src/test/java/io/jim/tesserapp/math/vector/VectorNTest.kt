package io.jim.tesserapp.math.vector

import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.math.sqrt

class VectorNTest {

    @Test
    fun construction() {
        VectorN(1.0, 2.0, 5.0, 4.0).apply {
            assertEquals(1.0, x, 0.1)
            assertEquals(2.0, y, 0.1)
            assertEquals(5.0, z, 0.1)
            assertEquals(4.0, q, 0.1)

            assertEquals(1.0, x, 0.1)
            assertEquals(2.0, y, 0.1)
            assertEquals(5.0, z, 0.1)
            assertEquals(4.0, q, 0.1)
        }
    }

    @Test
    fun load() {
        VectorN(4).apply {
            load(1.0, 2.0, 5.0, 4.0)

            assertEquals(1.0, x, 0.1)
            assertEquals(2.0, y, 0.1)
            assertEquals(5.0, z, 0.1)
            assertEquals(4.0, q, 0.1)

            assertEquals(1.0, x, 0.1)
            assertEquals(2.0, y, 0.1)
            assertEquals(5.0, z, 0.1)
            assertEquals(4.0, q, 0.1)
        }
    }

    @Test
    fun addition() {
        val v = VectorN(1.0, 2.0, 5.0, 0.0)
        val u = VectorN(3.0, 1.0, 4.0, 1.0)

        v += u

        v.apply {
            assertEquals(4.0, x, 0.1)
            assertEquals(3.0, y, 0.1)
            assertEquals(9.0, z, 0.1)
            assertEquals(1.0, q, 0.1)
        }
    }

    @Test
    fun subtraction() {
        val v = VectorN(3.0, 1.0, 4.0, 1.0)
        val u = VectorN(1.0, 2.0, 5.0, 2.0)

        v -= u

        v.apply {
            assertEquals(2.0, x, 0.1)
            assertEquals(-1.0, y, 0.1)
            assertEquals(-1.0, z, 0.1)
            assertEquals(-1.0, q, 0.1)
        }
    }

    @Test
    fun scalarMultiplication() {
        val v = VectorN(3.0, 1.0, 4.0, 0.0)
        val u = VectorN(1.0, 2.0, 5.0, 3.0)
        assertEquals(25.0, v * u, 0.1)
    }

    @Test
    fun scalarMultiplicationEqualsSquaredLength() {
        VectorN(3.0, 1.0, 4.0).apply {
            assertEquals(26.0, this * this, 0.1)
        }
    }

    @Test
    fun division() {
        VectorN(3.0, 1.0, 4.0, 0.0).apply {
            this /= 2.0

            assertEquals(1.5, x, 0.1)
            assertEquals(0.5, y, 0.1)
            assertEquals(2.0, z, 0.1)
            assertEquals(0.0, q, 0.1)
        }
    }

    @Test
    fun scale() {
        VectorN(3.0, 1.0, 4.0, 4.0).apply {
            this *= 2.0

            assertEquals(6.0, x, 0.1)
            assertEquals(2.0, y, 0.1)
            assertEquals(8.0, z, 0.1)
            assertEquals(8.0, q, 0.1)
        }
    }

    @Test
    fun cross() {
        (VectorN(3)).apply {
            crossed(VectorN(3.0, 1.0, 4.0), VectorN(1.0, 2.0, 5.0))

            assertEquals(-3.0, x, 0.1)
            assertEquals(-11.0, y, 0.1)
            assertEquals(5.0, z, 0.1)
        }
    }

    @Test
    fun length() {
        assertEquals(sqrt(26.0), VectorN(3.0, 1.0, 4.0).length, 0.1)
    }

    @Test
    fun normalized() {
        VectorN(3.0, 1.0, 4.0, 5.0).apply {
            normalize()

            assertEquals(1.0, length, 0.1)
        }
    }

}
