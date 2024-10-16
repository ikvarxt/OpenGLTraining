package me.ikvarxt.opengltraining.render

import android.opengl.GLES32.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.os.SystemClock
import me.ikvarxt.opengltraining.checkGlError
import me.ikvarxt.opengltraining.loadShader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

private const val TAG = "Triangle"

// number of coordinates per vertex in this array
const val COORDS_PER_VERTEX = 3
var triangleCoords = floatArrayOf(     // in counterclockwise order:
    0.0f, 0.6220085f, 0.0f,      // top
    -0.5f, -0.31100425f, 0.0f,    // bottom left
    0.5f, -0.31100425f, 0.0f      // bottom right
)

class TriangleRender : GLSurfaceView.Renderer {

    // Set color with red, green, blue and alpha (opacity) values
    val color = floatArrayOf(0.63671875f, 0.76953125f, 0.22265625f, 1.0f)

    private var vertexBuffer: FloatBuffer =
        // (number of coordinate values * 4 bytes per float)
        ByteBuffer.allocateDirect(triangleCoords.size * 4).run {
            // use the device hardware's native byte order
            order(ByteOrder.nativeOrder())

            // create a floating point buffer from the ByteBuffer
            asFloatBuffer().apply {
                // add the coordinates to the FloatBuffer
                put(triangleCoords)
                // set the buffer to read the first coordinate
                position(0)
            }
        }

    private val vertexShaderCode = """
        uniform mat4 uMVPMatrix;
        attribute vec4 vPosition;
        void main() {
          gl_Position = uMVPMatrix * vPosition;
        }
    """.trimIndent()

    private val fragmentShaderCode = """ 
        precision mediump float;
        uniform vec4 vColor;
        void main(void) {
          if (gl_FragCoord.x < 400.0) {
            gl_FragColor = vec4(0.0, 0.0, 1.0, 1.0);
          } else {
            gl_FragColor = vColor;
          }
        }
    """.trimIndent()

    private var program: Int = 0

    private var vertexShader: Int = 0
    private var fragmentShader: Int = 0

    private var positionHandle: Int = 0
    private var mColorHandle: Int = 0
    private var mMVPMatrixHandle: Int = 0

    private val vertexCount: Int = triangleCoords.size / COORDS_PER_VERTEX
    private val vertexStride: Int = COORDS_PER_VERTEX * 4 // 4 bytes per vertex

    private val rotationMatrix = FloatArray(16)
    private val vPMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)



    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(1f, 0f, 0.5f, 1f)

        vertexShader = loadShader(GL_VERTEX_SHADER, vertexShaderCode)
        fragmentShader = loadShader(GL_FRAGMENT_SHADER, fragmentShaderCode)

        // create empty OpenGL ES Program
        program = glCreateProgram().also {

            // add the vertex shader to program
            glAttachShader(it, vertexShader)

            // add the fragment shader to program
            glAttachShader(it, fragmentShader)

            // creates OpenGL ES program executables
            glLinkProgram(it)
        }
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)

        val ratio = width.toFloat() / height.toFloat()

        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f)
    }

    override fun onDrawFrame(gl: GL10?) {
        val scratch = FloatArray(16)

        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        val time = SystemClock.uptimeMillis() % 4000L
        val angle = 0.090f * time.toInt()

        Matrix.setLookAtM(
            viewMatrix, 0,
            0f, 0f, 3f,
            0f, 0f, 0f,
            0f, 1f, 0f
        )

        Matrix.multiplyMM(
            vPMatrix, 0,
            projectionMatrix, 0,
            viewMatrix, 0
        )
        Matrix.setRotateM(rotationMatrix, 0, angle, 0f, 0f, -1.0f)

        Matrix.multiplyMM(
            scratch, 0,
            vPMatrix, 0,
            rotationMatrix, 0
        )

        draw(scratch)
    }


    private fun draw(mvpMatrix: FloatArray) {
        glUseProgram(program)

        // get handle to vertex shader's vPosition member
        positionHandle = glGetAttribLocation(program, "vPosition")

        // Enable a handle to the triangle vertices
        glEnableVertexAttribArray(positionHandle)

        // Prepare the triangle coordinate data
        glVertexAttribPointer(
            positionHandle, COORDS_PER_VERTEX,
            GL_FLOAT, false,
            vertexStride, vertexBuffer
        )

        // get handle to fragment shader's vColor member
        mColorHandle = glGetUniformLocation(program, "vColor").also { colorHandle ->
            if (checkGlError()) {
                throw RuntimeException("draw: glGetUniformLocation vColor ${glGetShaderInfoLog(fragmentShader)}")
            }

            // Set color for drawing the triangle
            glUniform4fv(colorHandle, 1, color, 0)
            if (checkGlError()) {
                throw RuntimeException("draw: glUniform4fv vColor ${glGetShaderInfoLog(fragmentShader)}")
            }
        }

        mMVPMatrixHandle = glGetUniformLocation(program, "uMVPMatrix").also { handle ->
            glUniformMatrix4fv(handle, 1, false, mvpMatrix, 0)
        }

        // Draw the triangle
        glDrawArrays(GL_TRIANGLES, 0, vertexCount)

        // Disable vertex array
        glDisableVertexAttribArray(positionHandle)
    }
}
