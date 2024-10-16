package me.ikvarxt.opengltraining.render

import android.opengl.GLSurfaceView
import me.ikvarxt.opengltraining.loadShader
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

import android.opengl.GLES32.*
import me.ikvarxt.opengltraining.tryThrowError

private const val TAG = "TransitionTriangleRender"

class TransitionTriangleRender : GLSurfaceView.Renderer {

    private val vertexShaderCode = """
        #version 320 es
        uniform float offset;
        void main() {
          if (gl_VertexID == 0) 
            gl_Position = vec4(0.25 + offset, -0.25, 0.0, 1.0);
          else if (gl_VertexID == 1) 
            gl_Position = vec4(-0.25 + offset, -0.25, 0.0, 1.0);
          else 
            gl_Position = vec4(0.25 + offset, 0.25, 0.0, 1.0);
        }
    """.trimIndent()

    private val fragShaderCode = """
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

    private var x = 0f
    private var inc = 0.01f

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        vertexShader = loadShader(GL_VERTEX_SHADER, vertexShaderCode)
        fragmentShader = loadShader(GL_FRAGMENT_SHADER, fragShaderCode)

        program = glCreateProgram().also {
            glCompileShader(vertexShader)
            tryThrowError(TAG, "compile vertex") { glGetShaderInfoLog(vertexShader) }

            glCompileShader(fragmentShader)
            tryThrowError(TAG, "compile frag") { glGetShaderInfoLog(fragmentShader) }

            glAttachShader(it, vertexShader)
            glAttachShader(it, fragmentShader)
            glLinkProgram(it)
        }
        glClearColor(1f, 0f, 0f, 1f)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        glUseProgram(program)

        x += inc

        if (x > 1f || x < -1f) inc = -inc

        val offsetHandle = glGetUniformLocation(program, "offset")
        glProgramUniform1f(program, offsetHandle, x)

        glDrawArrays(GL_TRIANGLES, 0, 3)
        tryThrowError(TAG, "draw arrays") {
            "program" + glGetProgramInfoLog(program) +
                    ", vertex:" + glGetShaderInfoLog(vertexShader) +
                    ", fragment: " + glGetShaderInfoLog(fragmentShader)
        }

    }
}