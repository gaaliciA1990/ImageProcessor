package com.img_processor.ImgManipulators

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.angles.Degrees
import com.sksamuel.scrimage.filter.GrayscaleFilter
import java.awt.image.BufferedImage


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
     * Todo: update code for handing the rectangle around the image
     * Return the degree rotated [image]
     */
    fun rotateImage(degree: Int): ImmutableImage {
        //Check the value of degree to determine which direction to rotate
        return if (degree != 0) {
            val w: Int = originalImage.getWidth()
            val h: Int = originalImage.getHeight()
            val toRad = Math.toRadians(degree.toDouble())
            val hPrime =
                (w * Math.abs(Math.sin(toRad)) + h * Math.abs(Math.cos(toRad))).toInt()
            val wPrime =
                (h * Math.abs(Math.sin(toRad)) + w * Math.abs(Math.cos(toRad))).toInt()

            val rotatedImage = BufferedImage(wPrime, hPrime, BufferedImage.TYPE_INT_RGB)
            val g = rotatedImage.createGraphics()
            g.color = Color.LIGHT_GRAY
            g.fillRect(0, 0, wPrime, hPrime) // fill entire area

            g.translate(wPrime / 2, hPrime / 2)
            g.rotate(toRad)
            g.translate(-w / 2, -h / 2)
            g.drawImage(originalImage, 0, 0, null)
            g.dispose() // release used resources before g is garbage-collected

            return rotatedImage
            image.rotate(Degrees(degree))
        } else {
            image // return unaltered image if value == zero
        }
    }

    /**
     * Rotate the [image] clockwise or counterclockwise based in
     * user input
     *
     * Return the rotated [image]
     */
    fun rotate90LeftOrRight(direction:String): ImmutableImage {
        // check the direction and rotate, else return the same image unchanged
        return when (direction.lowercase()) {
            "right" -> {
                image.rotateRight()
            }
            "left" -> {
                image.rotateLeft()
            }
            else -> {
                image
            }
        }
    }

    /**
     * Add a grayscale filter to the [image] using the
     * scrimage Grayscale filter model
     *
     * Return the filtered [image]
     */
    fun convertToGrayscale(): ImmutableImage {
        return image.filter(GrayscaleFilter())
    }

    /**
     * Scale the [image] based on the values for width and height passed
     * through. This is resizing as people want, but without cropping
     * it. Using pixel size
     *
     * Return the scaled [image]
     */
    fun resizeImage(width:Int, height:Int): ImmutableImage {
        return image.scaleTo(width, height)
    }

    /**
     * Scale the [image] based on the value for width passed
     * through without cropping the image. Using pixel size
     *
     * Return the scaled [image]
     */
    fun resizeImageWidth(width:Int): ImmutableImage {
        return image.scaleToWidth(width)
    }

    /**
     * Scale the [image] based on the value for height passed
     * through without cropping it. Using pixel size
     *
     * Return the scaled [image]
     */
    fun resizeImageHeight(height:Int): ImmutableImage {
        return image.scaleToHeight(height)
    }

    /**
     * Scale the [image] to a thumbnail ratio of 1.49
     *
     * Return the thumbnail [image]
     */
    fun resizeImageToThumbnail(): ImmutableImage {
        val width = 160
        val height = 108

        return image.scaleTo(width, height)
    }

    /**
     * Flip the [image] either horizontally or vertically
     * based on user input.
     *
     * Return the flipped [image]
     */
    fun flipImage(direction:String): ImmutableImage {
        // return the image flipped, else return same image unchanged
        return when (direction.lowercase()) {
            "horizontal" -> {
                image.flipX()
            }
            "vertical" -> {
                image.flipY()
            }
            else -> {
                image
            }
        }
    }
}