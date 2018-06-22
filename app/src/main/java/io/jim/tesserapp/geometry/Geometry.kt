package io.jim.tesserapp.geometry

import io.jim.tesserapp.math.vector.VectorN
import io.jim.tesserapp.util.LinearList

/**
 * A geometrical structure consisting of vertices.
 *
 * @property onTransformUpdate
 * Called in every render frame to update this geometry's transform.
 * The actual transform owned by this geometry is passed as the receiver to [onTransformUpdate].
 * To trigger an [onTransformUpdate] invocation, call [updateTransform].
 */
class Geometry(
        private val onTransformUpdate: Transform.() -> Unit = {}
) {
    
    companion object {
        
        /**
         * The volume onto which 4 dimensional vectors are projected.
         * This value should be well chosen, as no vector should ever have such a q-value,
         * since that will lead to projection into infinity.
         */
        const val Q_PROJECTION_VOLUME = 1.0
        
    }
    
    /**
     * List containing all positions.
     */
    private val positions = ArrayList<VectorN>()
    
    /**
     * List containing all lines constructed from [positions] using indices.
     */
    val lines = LinearList<Line<VectorN>>()
    
    /**
     * This geometry's name.
     */
    var name = ""
    
    /**
     * Model-transform.
     */
    val transform = Transform()
    
    /**
     * Trigger the [onTransformUpdate] callback.
     * This function is intended to be called in each frame to implement smoothed transform.
     */
    fun updateTransform() {
        onTransformUpdate(transform)
    }

    /**
     * Add a series of vertices.
     * The actual lines are drawn from indices to these vertices.
     */
    private fun addPosition(position: VectorN) {
        positions += position
    }
    
    /**
     * Add a line spanning between two positions.
     * @param a Index to the start position.
     * @param b Index to the end position.
     * @param color Line color.
     */
    private fun addLine(a: Int, b: Int, color: Color = Color.PRIMARY) {
        lines += Line(positions, a, b, color)
    }
    
    /**
     * Add a line from position [a] to position [b].
     * This actually creates to new positions.
     * @param a Starting position.
     * @param b End position.
     * @param color Line color.
     */
    fun addLine(a: VectorN, b: VectorN, color: Color = Color.PRIMARY) {
        addPosition(a)
        addPosition(b)
        addLine(positions.lastIndex - 1, positions.lastIndex, color)
    }
    
    /**
     * Add a quadrilateral with four corner and an optional color.
     * @param a First corner.
     * @param b Second corner.
     * @param c Third corner.
     * @param d Fourth corner.
     * @param color Color of added lines.
     */
    fun addQuadrilateral(
            a: VectorN,
            b: VectorN,
            c: VectorN,
            d: VectorN,
            color: Color = Color.PRIMARY
    ) {
        addPosition(a)
        addPosition(b)
        addPosition(c)
        addPosition(d)
        addLine(0, 1, color)
        addLine(1, 2, color)
        addLine(2, 3, color)
        addLine(3, 0, color)
    }

    /**
     * Extrudes the whole geometry in the given [direction].
     * This works by duplicating the whole geometry and then connecting all point duplicate
     * counterparts.
     * @param keepColors The generated copy will have matching colors to the line set it originated from.
     * @param connectorColor Color of the lines connecting the original and generated lines.
     */
    fun extrude(
            direction: VectorN,
            keepColors: Boolean = false,
            connectorColor: Color = Color.PRIMARY
    ) {
        val size = positions.size
        
        for (i in 0 until size) {
            positions += VectorN(4).apply {
                copy(positions[i])
                this += direction
            }
        }
        
        lines += lines.map {
            Line(
                    positions,
                    it.startIndex + size,
                    it.endIndex + size,
                    if (keepColors) it.color else Color.PRIMARY
            )
        }
        for (i in 0 until size) {
            addLine(i, i + size, connectorColor)
        }
    }
    
    /**
     * Invoke [f] for each position and the color it's associated with.
     */
    inline fun forEachVertex(f: (position: VectorN, color: Color) -> Unit) {
        lines.indexedForEach {
            f(it.start, it.color)
            f(it.end, it.color)
        }
    }
    
    /**
     * Represents this geometry in a string.
     */
    override fun toString() = name
    
    /**
     * Symbolic colors.
     *
     * Geometries are colored indirectly using this palette.
     * The actual color integer is not relevant to the geometry.
     *
     * This is used to implement dynamic coloring when switching themes, without having to
     * rebuild the geometry just to change the color.
     */
    enum class Color {
        PRIMARY,
        ACCENT,
        X,
        Y,
        Z,
        Q
    }
    
}
