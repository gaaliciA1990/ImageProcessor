package com.img_processor.ImgManipulators

import com.sksamuel.scrimage.*
import com.sksamuel.scrimage.angles.Radians
import kotlin.math.PI

/**
 * Author: Alicia Garcia
 * Version: 1.0
 * Date: 2/12/2022 18:28
 *
 * Class for rotating an image by ## degrees. The rotate function uses
 * radians for step rotations, so we have a helper method for converting the degrees
 * to radians.
 */

class DegreeRotate(val image: ImmutableImage, val degree: Int) {

    // convert the degrees to radian to use step rotation
    val radians = ConvertDegreeToRads(degree)

    /**
     * Rotate the image using the given image and degree
     * return the rotated image
     */
    fun RotateImage(): ImmutableImage? {
        //rotate the image based on calculate radian value
        return image.rotate(Radians(radians))
    }

    /**
     * Convert the passed degree value to radians
     */
    private fun ConvertDegreeToRads(degree: Int): Double {
        val rads = (degree * (PI / 180))
        return rads
    }
}