package me.ikvarxt.opengltraining.render

import android.opengl.GLES32.*
import android.opengl.GLSurfaceView
import me.ikvarxt.opengltraining.checkGlError
import me.ikvarxt.opengltraining.loadShader
import java.nio.IntBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

private const val TAG = "SimpleTriangle"

class SimpleTriangleRender : GLSurfaceView.Renderer {

    private val vertexShaderCode = """
        #version 320 es
        layout(location = 0) in vec4 vPosition;
        
        void main() {
          gl_Position = vPosition;
          if (gl_VertexID == 0) gl_Position = vec4(0.25, -0.25, 0.0, 1.0);
          else if (gl_VertexID == 1) gl_Position = vec4(-0.25, -0.25, 0.0, 1.0);
          else gl_Position = vec4(0.25, 0.25, 0.0, 1.0);
        }
    """.trimIndent()

    private val fragmentShaderCode = """
        #version 320 es
        precision mediump float;
        out vec4 color;
        void main() {
          color = vec4(0.0, 0.0, 1.0, 1.0);
        }
    """.trimIndent()

    private var program = 0
    private var vertexShader = 0
    private var fragmentShader = 0

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(1f, 0f, 0f, 1f)

        vertexShader = loadShader(GL_VERTEX_SHADER, vertexShaderCode)
        fragmentShader = loadShader(GL_FRAGMENT_SHADER, fragmentShaderCode)

        program = glCreateProgram().also {
            glCompileShader(vertexShader)
            tryThrowError("compile vertex") { glGetShaderInfoLog(vertexShader) }

            glCompileShader(fragmentShader)
            tryThrowError("compile frag") { glGetShaderInfoLog(fragmentShader) }

            glAttachShader(it, vertexShader)
            glAttachShader(it, fragmentShader)

            glLinkProgram(it)
            tryThrowError("link program") { glGetProgramInfoLog(it) }

        }
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT)
        glUseProgram(program)

        glDrawArrays(GL_TRIANGLES, 0, 3)
        tryThrowError("draw arrays") {
            glGetProgramInfoLog(program) +
                    glGetShaderInfoLog(vertexShader) +
                    glGetShaderInfoLog(fragmentShader)
        }

    }

    private fun tryThrowError(msg: String, lazyMessage: () -> String) {

        fun throwError(msg: String, log: String): Nothing =
            throw RuntimeException("$TAG $msg $log")

        if (checkGlError()) throwError(msg, lazyMessage.invoke())
    }
}