package com.img_processor.plugins

import com.sksamuel.scrimage.ImmutableImage
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Author: Alicia Garcia
 * Version: 1.0
 * Date: 2/27/2022 22:23
 */

internal class ApiControllerTest {
    @Test
    fun `test combo resize parameters` () {
        // Arrange
        val controller = ApiController()
        val image = ImmutableImage.create(10,10)
        val commands: List<String> = listOf("rotate=left","grayscale","resize=25*25")

        // Act
        val processedImage = controller.combinationImage(commands,image)

        // Assert
        assertEquals(25, processedImage?.width)
        assertEquals(25, processedImage?.height)
    }
}