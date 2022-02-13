package com.img_processor.ImgManipulators

import com.sksamuel.scrimage.*
import com.sksamuel.scrimage.angles.Degrees
import com.sksamuel.scrimage.color.Grayscale

/**
 * Author: Alicia Garcia
 * Version: 1.0
 * Date: 2/12/2022 18:28
 *
 * Class for rotating an image by ## degrees. The rotate function uses
 * radians for step rotations, so we have a helper method for converting the degrees
 * to radians.
 */

class ManipulateImage(val image: ImmutableImage) {
    /**
     * Rotate the image using the given member variable [image]
     * and [degree]
     *
     * Return the manipulated [image]
     */
    fun rotateImage(degree: Int): ImmutableImage {
        //rotate the image based on calculate radian value
        return image.rotate(Degrees(degree))
    }

    /**
     * Rotate the image clockwise
     *
     * Return the manipulated [image]
     */
    fun rotateClockwise(): ImmutableImage {
        return image.rotateRight()
    }

    /**
     * Rotate the image counterclockwise
     *
     * Return the manipulated [image]
     */
    fun rotateCounterClockwise(): ImmutableImage {
        return image.rotateLeft()
    }

    fun convertToGrayscale(): ImmutableImage {
        val filteredImg = image.filter(Grayscale(5))
    }

}