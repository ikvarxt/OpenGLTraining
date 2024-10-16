package me.ikvarxt.opengltraining

import android.opengl.GLES32.GL_FRAGMENT_SHADER
import android.opengl.GLES32.GL_VERTEX_SHADER
import android.opengl.GLES32.glAttachShader
import android.opengl.GLES32.glCreateProgram
import android.opengl.GLES32.glGetProgramInfoLog
import android.opengl.GLES32.glLinkProgram
import android.opengl.GLES32.glViewport
import android.opengl.GLSurfaceView
import androidx.annotation.CallSuper
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

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
            glAttachShader(it, vertexShader)
            glAttachShader(it, fragmentShader)
            glLinkProgram(it)
            tryThrowError(tag, "link program") { glGetProgramInfoLog(it) }
        }

    }

    @CallSuper
    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)
    }

}