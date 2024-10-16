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
        void main(void) {
          if (gl_VertexID == 0) gl_Position = vec4(0.25, -0.25, 0.0, 1.0);
          else if (gl_VertexID == 1) gl_Position = vec4(-0.25, -0.25, 0.0, 1.0);
          else gl_Position = vec4(0.25, 0.25, 0.0, 1.0);
        }
    """.trimIndent()

    private val fragmentShaderCode = """
        out vec4 color;
        void main(void) {
          color = vec4(0.0, 0.0, 1.0, 1.0);
        }
    """.trimIndent()

    private var program: Int = 0

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        val vertexShader = loadShader(GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GL_FRAGMENT_SHADER, fragmentShaderCode)

        program = glCreateProgram().also {
            glCompileShader(vertexShader)
            if (checkGlError()) throw RuntimeException("$TAG compile vertex: ${glGetShaderInfoLog(vertexShader)}")
            glCompileShader(fragmentShader)
            if (checkGlError()) throw RuntimeException("$TAG compile frag: ${glGetShaderInfoLog(fragmentShader)}")

            glAttachShader(it, vertexShader)
            glAttachShader(it, fragmentShader)

            glLinkProgram(it)
            if (checkGlError()) throw RuntimeException("$TAG link program: ${glGetProgramInfoLog(it)}")

            val vao = IntBuffer.allocate(1)
            glGenVertexArrays(1, vao)
            glBindVertexArray(vao[0])
        }
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
    }

    override fun onDrawFrame(gl: GL10?) {
        glUseProgram(program)
        glDrawArrays(GL_POINTS, 0, 1)
    }
}