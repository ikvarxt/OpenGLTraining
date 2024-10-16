package me.ikvarxt.opengltraining.render

import android.opengl.GLES32.*
import android.opengl.GLSurfaceView
import me.ikvarxt.opengltraining.BaseGLRender
import me.ikvarxt.opengltraining.checkGlError
import me.ikvarxt.opengltraining.loadShader
import me.ikvarxt.opengltraining.tryThrowError
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

private const val TAG = "SimpleTriangle"

class SimpleTriangleRender : BaseGLRender(TAG) {

    override val vertexShaderCode = """
        #version 320 es
        
        void main() {
          if (gl_VertexID == 0) 
            gl_Position = vec4(0.25, -0.25, 0.0, 1.0);
          else if (gl_VertexID == 1) 
            gl_Position = vec4(-0.25, -0.25, 0.0, 1.0);
          else 
            gl_Position = vec4(0.25, 0.25, 0.0, 1.0);
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

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        super.onSurfaceCreated(gl, config)
        glClearColor(1f, 0f, 0f, 1f)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        super.onSurfaceChanged(gl, width, height)
        glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT)
        glUseProgram(program)

        glDrawArrays(GL_TRIANGLES, 0, 3)
        tryThrowError(TAG, "draw arrays") {
            "program" + glGetProgramInfoLog(program) +
                    ", vertex:" + glGetShaderInfoLog(vertexShader) +
                    ", fragment: " + glGetShaderInfoLog(fragmentShader)
        }

    }

}