package io.jim.tesserapp.ui.view

import android.content.res.AssetManager
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import io.jim.tesserapp.gl.ATTRIBUTE_FLOATS
import io.jim.tesserapp.gl.Color
import io.jim.tesserapp.gl.Vbo
import io.jim.tesserapp.graphics.LinesShader
import io.jim.tesserapp.ui.model.MainViewModel
import io.jim.tesserapp.util.synchronized
import java.nio.DoubleBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Actually renders to OpenGL.
 */
class Renderer(
        val clearColor: Color,
        val viewModel: MainViewModel,
        val assets: AssetManager,
        val dpi: Double) : GLSurfaceView.Renderer {
    
    private lateinit var linesShader: LinesShader
    private lateinit var vertexVbo: Vbo
    
    private var aspectRatio: Double = 1.0
    
    companion object {
        
        /**
         * Converts inches to millimeters.
         */
        private const val MM_PER_INCH = 25.4
        
        /**
         * Specifies width of lines, in millimeters.
         */
        private const val LINE_WIDTH_MM = 0.15
        
        init {
            System.loadLibrary("native-lib")
        }
        
    }
    
    /**
     * Initialize data.
     */
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(
                clearColor.red,
                clearColor.green,
                clearColor.blue,
                1f
        )
        GLES20.glDisable(GLES20.GL_CULL_FACE)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        
        GLES20.glLineWidth((dpi / MM_PER_INCH * LINE_WIDTH_MM).toFloat())
        
        println("Open GLES version: ${GLES20.glGetString(GLES20.GL_VERSION)}")
        println("GLSL version: ${GLES20.glGetString(GLES20.GL_SHADING_LANGUAGE_VERSION)}")
        println("Renderer: ${GLES20.glGetString(GLES20.GL_RENDERER)}")
        println("Vendor: ${GLES20.glGetString(GLES20.GL_VENDOR)}")
        
        // Construct shader:
        linesShader = LinesShader(assets)
        
        // Construct vertex buffer:
        vertexVbo = Vbo(GLES20.GL_ARRAY_BUFFER)
    }
    
    /**
     * Construct view matrix.
     */
    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        aspectRatio = width.toDouble() / height
    }
    
    /**
     * Instruct vertex attributes
     */
    fun instructVertexAttributePointers(linesShader: LinesShader) {
        // Position attribute:
        GLES20.glEnableVertexAttribArray(linesShader.positionAttributeLocation)
        GLES20.glVertexAttribPointer(
                linesShader.positionAttributeLocation,
                ATTRIBUTE_FLOATS,
                GLES20.GL_FLOAT,
                false,
                LinesShader.VERTEX_STRIDE,
                LinesShader.VERTEX_OFFSET_POSITION
        )
        
        // Color attribute:
        GLES20.glEnableVertexAttribArray(linesShader.colorAttributeLocation)
        GLES20.glVertexAttribPointer(
                linesShader.colorAttributeLocation,
                ATTRIBUTE_FLOATS,
                GLES20.GL_FLOAT,
                false,
                LinesShader.VERTEX_STRIDE,
                LinesShader.VERTEX_OFFSET_COLOR
        )
    }
    
    /**
     * Uploads the view matrix.
     * Requires the GL program to be bound.
     */
    external fun uploadViewMatrix(
            uniformLocation: Int,
            distance: Double,
            aspectRatio: Double,
            horizontalRotation: Double,
            verticalRotation: Double
    )
    
    /**
     * Uploads the projection matrix.
     * Requires the GL program to be bound.
     */
    external fun uploadProjectionMatrix(
            uniformLocation: Int
    )
    
    /**
     * Draws a single geometry.
     * Requires the GL program to be bound.
     * Requires the VBO bound to GL_ARRAY_BUFFER.
     * Vertex attribute pointers must be instructed before drawing anything.
     */
    external fun drawGeometry(
            positions: DoubleBuffer,
            transform: DoubleArray,
            color: Int,
            isFourDimensional: Boolean
    )
    
    /**
     * Draw a single frame.
     */
    override fun onDrawFrame(gl: GL10?) {
        
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        
        viewModel.synchronized {
    
            linesShader.program.bound {
        
                uploadViewMatrix(
                        linesShader.viewMatrixLocation,
                        cameraDistance.smoothed,
                        aspectRatio,
                        horizontalCameraRotation.smoothed,
                        verticalCameraRotation.smoothed
                )
        
                uploadProjectionMatrix(linesShader.projectionMatrixLocation)
                
                geometries.forEach { geometry ->
                    
                    val transform = geometry.onTransformUpdate()
                    val color = symbolicColorMapping[geometry.color]
    
                    vertexVbo.bound {
        
                        instructVertexAttributePointers(linesShader)
                        
                        drawGeometry(
                                geometry.positions,
                                transform.data,
                                color.code,
                                geometry.isFourDimensional
                        )
    
                    }
                    
                }
                
                
            }
            
        }
        
    }
    
}