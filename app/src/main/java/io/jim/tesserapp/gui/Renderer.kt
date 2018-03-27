package io.jim.tesserapp.gui

import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import io.jim.tesserapp.geometry.Geometry
import io.jim.tesserapp.math.Direction
import io.jim.tesserapp.math.Matrix
import io.jim.tesserapp.math.Point
import junit.framework.Assert.assertEquals
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class Renderer(maxLines: Int) : GLSurfaceView.Renderer {

    private val geometries = ArrayList<Geometry>()
    private val maxLineVertices = maxLines * 2
    private lateinit var vertexBuffer: VertexBuffer
    private lateinit var shader: Shader
    private val viewMatrix = Matrix(3)
    private val rotationMatrixXY = Matrix(3)
    private val rotationMatrixYZ = Matrix(3)
    private val projectionMatrix = Matrix(3).perspective(0.1, 10.0)
    private val translationMatrix = Matrix(3).translation(Direction(0.0, 0.0, -4.0))

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(1f, 1f, 1f, 1f)
        glDisable(GL_CULL_FACE)
        glEnable(GL_DEPTH_TEST)
        glLineWidth(4f)

        shader = Shader()
        vertexBuffer = VertexBuffer(maxLineVertices)
    }

    fun addGeometry(geometry: Geometry) {
        geometries.add(geometry)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)
        viewMatrix.scale(Point(1.0, (width).toDouble() / height, 1.0))
    }

    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        for (geometry in geometries) {
            for (point in geometry.toLineList()) {
                assertEquals("All vertices must be 3D", 3, point.dimension)
                vertexBuffer.appendVertex(point * rotationMatrixXY * rotationMatrixYZ * translationMatrix * viewMatrix * projectionMatrix, geometry.color)
            }
        }

        vertexBuffer.draw(shader, GL_LINES)
    }

    fun rotation(yaw: Double, pitch: Double) {
        rotationMatrixXY.rotation(0, 1, pitch)
        rotationMatrixYZ.rotation(1, 2, yaw)
    }

}
