package me.ikvarxt.opengltraining

import android.content.Context
import android.opengl.GLSurfaceView
import me.ikvarxt.opengltraining.render.SimpleTriangleRender
import me.ikvarxt.opengltraining.render.TriangleRender

class Spacial4dView(context: Context) : GLSurfaceView(context) {

    private val render = SimpleTriangleRender()

    init {
        setEGLContextClientVersion(3)
        setRenderer(render)
    }
}
