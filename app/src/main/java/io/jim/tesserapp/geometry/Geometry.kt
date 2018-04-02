package io.jim.tesserapp.geometry

import io.jim.tesserapp.gui.Color
import io.jim.tesserapp.math.Vector

open class Geometry(val color: Color) {

    val points = ArrayList<Vector>()
    val lines = ArrayList<Pair<Int, Int>>()

    fun addPoints(vararg p: Vector) {
        points.addAll(p)
    }

    fun addLine(a: Int, b: Int) {
        lines.add(Pair(a, b))
    }

    /**
     * Extrudes the whole geometry in the given [direction].
     * This works by duplicating the whole geometry and then connecting all point duplicate counterparts.
     */
    fun extrude(direction: Vector) {
        val size = points.size
        points.addAll(points.map { it + direction })
        lines.addAll(lines.map { Pair(it.first + size, it.second + size) })
        for (i in 0 until size) {
            addLine(i, i + size)
        }
    }

}
