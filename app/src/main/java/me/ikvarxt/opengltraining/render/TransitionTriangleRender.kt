package me.ikvarxt.opengltraining.render

import android.opengl.GLES32.GL_COLOR_BUFFER_BIT
import android.opengl.GLES32.GL_DEPTH_BUFFER_BIT
import android.opengl.GLES32.GL_TRIANGLE_FAN
import android.opengl.GLES32.glClear
import android.opengl.GLES32.glClearColor
import android.opengl.GLES32.glDrawArrays
import android.opengl.GLES32.glGetProgramInfoLog
import android.opengl.GLES32.glGetShaderInfoLog
import android.opengl.GLES32.glGetUniformLocation
import android.opengl.GLES32.glProgramUniform1f
import android.opengl.GLES32.glUseProgram
import android.opengl.GLES32.glViewport
import me.ikvarxt.opengltraining.BaseGLRender
import me.ikvarxt.opengltraining.tryThrowError
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

private const val TAG = "TransitionTriangleRender"

@Suppress("unused")
class TransitionTriangleRender : BaseGLRender(TAG) {

    override val vertexShaderCode = """
        #version 320 es
        uniform float offset;
        void main() {
          if (gl_VertexID == 0) 
            gl_Position = vec4(0.25 + offset, -0.25, 0.0, 1.0);
          else if (gl_VertexID == 1) 
            gl_Position = vec4(-0.25 + offset, -0.25, 0.0, 1.0);
          else if (gl_VertexID == 2) 
            gl_Position = vec4(-0.25 + offset, 0.25, 0.0, 1.0);
          else 
            gl_Position = vec4(0.25 + offset, 0.25, 0.0, 1.0);
        }
    """.trimIndent()

    override val fragmentShaderCode = """
        #version 320 es
        precision mediump float;
        out vec4 color;
        void main() {
          color = vec4(0.0, 0.0, 1.0, 1.0);
        }
    """.trimIndent()

    private var x = 0f
    private var inc = 0.01f

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        super.onSurfaceCreated(gl, config)
        glClearColor(1f, 0f, 0f, 1f)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        super.onSurfaceChanged(gl, width, height)
        glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        glUseProgram(program)

        x += inc

        if (x > 1f || x < -1f) inc = -inc

        val offsetHandle = glGetUniformLocation(program, "offset")
        glProgramUniform1f(program, offsetHandle, x)

        glDrawArrays(GL_TRIANGLE_FAN, 0, 4)
        tryThrowError(TAG, "draw arrays") {
            "program" + glGetProgramInfoLog(program) +
                    ", vertex:" + glGetShaderInfoLog(vertexShader) +
                    ", fragment: " + glGetShaderInfoLog(fragmentShader)
        }

    }
}