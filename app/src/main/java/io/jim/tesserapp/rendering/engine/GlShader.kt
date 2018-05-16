package io.jim.tesserapp.rendering.engine

import android.opengl.GLES20
import android.opengl.GLES30

/**
 * GL shader.
 *
 * @property type Type of shader, i.e. [GLES30.GL_VERTEX_SHADER].
 */
class GlShader(val type: Int, source: String) {

    /**
     * Actual handle retrieved from GL.
     */
    val shaderHandle = GLES20.glCreateShader(type)

    init {
        GLES30.glShaderSource(shaderHandle, source)
        GLES30.glCompileShader(shaderHandle)

        GLES30.glGetShaderiv(shaderHandle, GLES30.GL_COMPILE_STATUS, resultCode)
        if (GLES30.GL_TRUE != resultCode()) {
            throw GlException("Cannot compile vertex shader: ${GLES30.glGetShaderInfoLog(shaderHandle)}")
        }

        GlException.check("Shader initialization")
    }

}
