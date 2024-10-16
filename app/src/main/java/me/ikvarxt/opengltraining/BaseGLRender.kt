package me.ikvarxt.opengltraining

import android.opengl.GLSurfaceView
import android.opengl.GLES32.*
import androidx.annotation.CallSuper
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.egl.EGLConfig

abstract class BaseGLRender(
    protected open val tag: String = "BaseGLRender",
) : GLSurfaceView.Renderer {

    abstract val vertexShaderCode: String
    abstract val fragmentShaderCode: String

    protected var program: Int = 0
    protected var vertexShader: Int = 0
    protected var fragmentShader: Int = 0

    @CallSuper
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {

        vertexShader = loadShader(GL_VERTEX_SHADER, vertexShaderCode)
        fragmentShader = loadShader(GL_FRAGMENT_SHADER, fragmentShaderCode)

        program = glCreateProgram().also {

            glCompileShader(vertexShader)
            tryThrowError(tag, "compile vertex") { glGetShaderInfoLog(vertexShader) }

            glCompileShader(fragmentShader)
            tryThrowError(tag, "compile fragment") { glGetShaderInfoLog(fragmentShader) }

            glAttachShader(it, vertexShader)
            glAttachShader(it, fragmentShader)
            glLinkProgram(it)
        }

    }

    @CallSuper
    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)
    }

}