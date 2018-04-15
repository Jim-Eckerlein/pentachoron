package io.jim.tesserapp.gui

import android.content.Context
import android.opengl.GLSurfaceView
import android.os.Handler
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.MotionEvent.*
import io.jim.tesserapp.R
import io.jim.tesserapp.geometry.Geometry
import io.jim.tesserapp.geometry.Lines
import io.jim.tesserapp.geometry.Quadrilateral
import io.jim.tesserapp.graphics.Color
import io.jim.tesserapp.graphics.Renderer
import io.jim.tesserapp.math.Vector

/**
 * A view capable of rendering 3D geometry.
 */
class CoordinateSystemView(context: Context, attrs: AttributeSet?) : GLSurfaceView(context, attrs) {

    /**
     * Controllable demonstration geometry.
     */
    val cube: Geometry

    private val renderer = Renderer(context)
    private val touchStartPosition = Vector(0.0, 0.0, 0.0, 0.0)
    private val rotation = Vector(0.0, 0.0, 0.0, 0.0)
    private var touchStartTime = 0L
    private val grid: Lines

    companion object {
        private const val CLICK_TIME_MS = 100L
        private const val TOUCH_ROTATION_SENSITIVITY = 0.005
    }

    init {
        setEGLContextClientVersion(2)
        setRenderer(renderer)
        debugFlags = DEBUG_CHECK_GL_ERROR
        renderMode = RENDERMODE_WHEN_DIRTY

        // Create axis:
        val axis = Lines("Axis", Color(context, R.color.colorPrimary))
        axis.addLine(Vector(0.0, 0.0, 0.0, 1.0), Vector(1.0, 0.0, 0.0, 1.0))
        axis.addLine(Vector(0.0, 0.0, 0.0, 1.0), Vector(0.0, 1.0, 0.0, 1.0))
        axis.addLine(Vector(0.0, 0.0, 0.0, 1.0), Vector(0.0, 0.0, 1.0, 1.0))
        axis.addToParentGeometry(renderer.rootGeometry)

        // Create grid:
        grid = Lines("Grid", Color(context, R.color.colorGrid))
        grid.addToParentGeometry(renderer.rootGeometry)
        for (i in -5..5) {
            grid.addLine(Vector(i.toDouble(), 0.0, -5.0, 1.0), Vector(i.toDouble(), 0.0, 5.0, 1.0))
            grid.addLine(Vector(-5.0, 0.0, i.toDouble(), 1.0), Vector(5.0, 0.0, i.toDouble(), 1.0))
        }

        // Create cube:
        cube = Quadrilateral("Cube", Vector(1.0, 1.0, 1.0, 1.0),
                Vector(-1.0, 1.0, 1.0, 1.0),
                Vector(-1.0, -1.0, 1.0, 1.0),
                Vector(1.0, -1.0, 1.0, 1.0),
                Color(context, R.color.colorAccent)
        )
        cube.addToParentGeometry(renderer.rootGeometry)
        Handler().postDelayed({
            cube.extrude(Vector(0.0, 0.0, -2.0, 0.0))
        }, 3000)

        // Render scene upon every graphical change:
        Geometry.onMatrixChangedListeners += {
            println("Matrix data changed")
            requestRender()
        }
        Geometry.onHierarchyChangedListeners += {
            println("Hierarchy changed")
            requestRender()
        }
        Geometry.onGeometryChangedListeners += {
            println("Geometry changed")
            requestRender()
        }

        requestRender()
    }

    /**
     * Handles camera orbit position upon touch events.
     */
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (null == event) return false
        val x = event.x.toDouble()
        val y = event.y.toDouble()

        if (event.action == ACTION_DOWN) {
            touchStartPosition.x = x
            touchStartPosition.y = y
            touchStartTime = System.currentTimeMillis()
            return true
        }

        if (event.action == ACTION_MOVE) {
            val dx = x - touchStartPosition.x
            val dy = y - touchStartPosition.y
            rotation.x += dx
            rotation.y += dy
            renderer.rootGeometry.rotationZX(rotation.x * TOUCH_ROTATION_SENSITIVITY)
            renderer.rootGeometry.rotationYX(rotation.y * TOUCH_ROTATION_SENSITIVITY)

            touchStartPosition.x = x
            touchStartPosition.y = y
            return true
        }

        if (event.action == ACTION_UP && System.currentTimeMillis() -
                touchStartTime < CLICK_TIME_MS) {
            rotation.x = 0.0
            rotation.y = 0.0
            performClick()
        }

        return false
    }

    /**
     * Clicks reset camera position to default.
     */
    override fun performClick(): Boolean {
        super.performClick()

        renderer.rootGeometry.rotationYX(0.0)
        renderer.rootGeometry.rotationZX(0.0)
        requestRender()

        return true
    }

    /**
     * Enable or disable grid rendering.
     */
    fun enableGrid(@Suppress("UNUSED_PARAMETER") enable: Boolean) {
        //grid.visible = enable
        /*
                println("Enable grid: $enable")
        if (enable) {
            for (i in -1..1) {
                println("Add lines #$i")
                grid.addLine(Vector(i.toDouble(), 0.0, -5.0, 1.0), Vector(i.toDouble(), 0.0, 5.0, 1.0))
                grid.addLine(Vector(-5.0, 0.0, i.toDouble(), 1.0), Vector(5.0, 0.0, i.toDouble(), 1.0))
            }
        }
        else {
            grid.clearPoints()
        }
         */
    }

}
