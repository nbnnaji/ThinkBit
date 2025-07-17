package com.nkechinnaji.thinkbit

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class StarShape(private val numPoints: Int = 5, private val innerRadiusRatio: Float = 0.4f) : Shape {

    init {
        require(numPoints >= 3) { "Star must have at least 3 points." }
        require(innerRadiusRatio > 0f && innerRadiusRatio < 1f) { "Inner radius ratio must be between 0 and 1." }
    }

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path()
        val outerRadius = min(size.width, size.height) / 2f
        val innerRadius = outerRadius * innerRadiusRatio
        val angleIncrement = (2 * Math.PI / numPoints).toFloat()
        val phaseOffset = (-Math.PI / 2).toFloat() // Start first point at the top

        for (i in 0 until numPoints) {
            // Outer point
            val outerAngle = i * angleIncrement + phaseOffset
            val outerX = size.width / 2 + cos(outerAngle) * outerRadius
            val outerY = size.height / 2 + sin(outerAngle) * outerRadius

            if (i == 0) {
                path.moveTo(outerX, outerY)
            } else {
                path.lineTo(outerX, outerY)
            }

            // Inner point
            val innerAngle = outerAngle + angleIncrement / 2f
            val innerX = size.width / 2 + cos(innerAngle) * innerRadius
            val innerY = size.height / 2 + sin(innerAngle) * innerRadius
            path.lineTo(innerX, innerY)
        }
        path.close()
        return Outline.Generic(path)
    }
}
