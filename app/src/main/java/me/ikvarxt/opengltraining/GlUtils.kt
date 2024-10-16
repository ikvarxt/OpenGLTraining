package me.ikvarxt.opengltraining

import android.opengl.GLES32.GL_NO_ERROR
import android.opengl.GLES32.glCompileShader
import android.opengl.GLES32.glCreateShader
import android.opengl.GLES32.glGetError
import android.opengl.GLES32.glShaderSource


fun loadShader(type: Int, shaderCode: String): Int {

    // create a vertex shader type (GL_VERTEX_SHADER)
    // or a fragment shader type (GL_FRAGMENT_SHADER)
    return glCreateShader(type).also { shader ->

        // add the source code to the shader and compile it
        glShaderSource(shader, shaderCode)
        glCompileShader(shader)
    }
}

/**
 * Utility method for debugging OpenGL calls. Provide the name of the call
 * just after making it:
 *
 * <pre>
 * mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
 * MyGLRenderer.checkGlError("glGetUniformLocation");</pre>
 *
 * If the operation is not successful, the check throws an error.
 *
 * @param glOperation - Name of the OpenGL call to check.
 */
fun checkGlError(): Boolean = glGetError() != GL_NO_ERROR