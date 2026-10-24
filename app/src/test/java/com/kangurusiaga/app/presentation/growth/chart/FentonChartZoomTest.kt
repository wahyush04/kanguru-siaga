package com.kangurusiaga.app.presentation.growth.chart

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FentonChartZoomTest {

    @Test
    fun `clampOffset returns Zero when scale is 1 or less`() {
        val size = Size(1000f, 800f)
        val offset = Offset(100f, 100f)

        assertEquals(Offset.Zero, clampOffset(offset, 1.0f, size))
        assertEquals(Offset.Zero, clampOffset(offset, 0.8f, size))
    }

    @Test
    fun `clampOffset returns Zero when container size is empty or zero`() {
        assertEquals(Offset.Zero, clampOffset(Offset(100f, 100f), 2.0f, Size.Zero))
        assertEquals(Offset.Zero, clampOffset(Offset(100f, 100f), 2.0f, Size(0f, 500f)))
        assertEquals(Offset.Zero, clampOffset(Offset(100f, 100f), 2.0f, Size(500f, 0f)))
    }

    @Test
    fun `clampOffset correctly clamps pan within container bounds at scale 2x`() {
        val size = Size(1000f, 800f)
        val scale = 2.0f
        // maxPanX = (2 - 1) * 1000 / 2 = 500
        // maxPanY = (2 - 1) * 800 / 2 = 400

        // In bounds: should remain unchanged
        val inBounds = clampOffset(Offset(250f, -300f), scale, size)
        assertEquals(250f, inBounds.x, 0.01f)
        assertEquals(-300f, inBounds.y, 0.01f)

        // Exceeding positive bounds
        val overPositive = clampOffset(Offset(800f, 600f), scale, size)
        assertEquals(500f, overPositive.x, 0.01f)
        assertEquals(400f, overPositive.y, 0.01f)

        // Exceeding negative bounds
        val overNegative = clampOffset(Offset(-999f, -888f), scale, size)
        assertEquals(-500f, overNegative.x, 0.01f)
        assertEquals(-400f, overNegative.y, 0.01f)
    }

    @Test
    fun `clampOffset correctly scales bounds proportionally with higher zoom levels`() {
        val size = Size(600f, 900f)
        val scale = 4.0f
        // maxPanX = (4 - 1) * 600 / 2 = 900
        // maxPanY = (4 - 1) * 900 / 2 = 1350

        val clamped = clampOffset(Offset(1500f, -2000f), scale, size)
        assertEquals(900f, clamped.x, 0.01f)
        assertEquals(-1350f, clamped.y, 0.01f)
    }

    @Test
    fun `double tap target offset brings tapped point toward center`() {
        val chartSize = Size(1000f, 1000f)
        val center = Offset(chartSize.width / 2f, chartSize.height / 2f)
        val targetScale = 2.5f

        // When tapping center, offset should be Zero
        val tapCenter = clampOffset((center - center) * targetScale, targetScale, chartSize)
        assertEquals(Offset.Zero, tapCenter)

        // When tapping to the left of center, content moves right (positive offset)
        val tapLeft = Offset(250f, 500f)
        val offsetLeft = clampOffset((center - tapLeft) * targetScale, targetScale, chartSize)
        assertTrue("Offset should be positive when tapping left to shift content right", offsetLeft.x > 0)
        // Max pan X for 2.5x on 1000px = (2.5 - 1) * 1000 / 2 = 750
        assertEquals(625f, offsetLeft.x, 0.01f)
    }
}
