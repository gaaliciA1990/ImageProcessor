package com.img_processor.plugins

import com.img_processor.ImgManipulators.ManipulateImage
import com.sksamuel.scrimage.ImmutableImage


/**
 * Author: Alicia Garcia
 * Version: 1.0
 * Date: 2/27/2022 21:05
 *
 * API controller class for handling all of the manipulation requests passed
 * through the parameters to a given [img].
 */

class ApiController {
    /**
     * Controller for handling multiple manipulation commands on an [image]. Called from the Routing.kt file
     *
     * Returns an ImmutableImage, or null if invalid command found
     */
    fun combinationImage(commands: List<String>, img: ImmutableImage): ImmutableImage? {
        var image = img

        // iterate through list of commands
        commands?.forEach { command ->
            // delimit the strings by the equal sign and store in new list
            val manipulate = command.split("=")
            // check the first value of the new list and do the correct manipulations
            when (manipulate.first()) {
                "degree" -> {
                    image = rotateDegreesImage(manipulate[1].toInt(), image)
                }
                "grayscale" -> {
                    image = grayscaleImage(image)
                }
                "rotate" -> {
                    image = rotateImage(manipulate[1], image)
                }
                "flip" -> {
                    image = flipImage(manipulate[1], image)
                }
                "resize" -> {
                    val dimensions = manipulate[1].split("*")
                    image = resizeImage (dimensions[0].toInt(), dimensions[1].toInt(), image)
                }
                "thumbnail" -> {
                    image = thumbnailImage(image)
                }
                else -> {
                    return null
                }
            }
        }
        return image
    }

    /**
     * controller for rotating an [image] by degrees. Called from the Routing.kt file
     *
     * Returns an ImmutableImage
     */
    fun rotateDegreesImage(degree: Int, img: ImmutableImage): ImmutableImage {
        val image = ManipulateImage(img)

        // rotate the image based on the degrees
        return image.rotateImage(degree)
    }

    /**
     * Controller for rotating an image left or right 90 degrees. Called from Routing.kt file
     *
     * Returns an ImmutableImage
     */
    fun rotateImage(direction: String, img: ImmutableImage): ImmutableImage {
        // rotate image
        val image = ManipulateImage(img)
        val rotatedImage = image.rotate90LeftOrRight(direction)

        return (rotatedImage)
    }

    /**
     * Controller for adding grayscale to an [image]. Called from Routing.kt.
     *
     * Returns an ImmutableImage
     */
    fun grayscaleImage(img: ImmutableImage): ImmutableImage {
        // filter the image to grayscale
        val image = ManipulateImage(img)
        return image.convertToGrayscale()
    }

    /**
     * Controller for resizing an [image] based on width and height. Called from Routing.kt
     *
     * Returns ImmutableImage
     */
    fun resizeImage(width: Int?, height: Int?, img: ImmutableImage): ImmutableImage {
        val image = ManipulateImage(img)

        if (width != null && height != null) {
            return image.resizeImage(width, height)

        } else if (height != null) {
            return image.resizeImageHeight(height)

        } else {
            return image.resizeImageWidth(requireNotNull(width))
        }
    }

    /**
     * Controller for creating a thumbnail size [image]. Called from Routing.kt
     *
     * Returns ImmutableImage
     */
    fun thumbnailImage(img: ImmutableImage): ImmutableImage {
        val image = ManipulateImage(img)
        return image.resizeImageToThumbnail()
    }

    /**
     * Controller for flipping the [image] horizontally or vertically. Called from Routing.kt
     *
     * Return ImmutableImage
     */
    fun flipImage(direction: String, img: ImmutableImage): ImmutableImage {
        val image = ManipulateImage(img)

        // flip the image
        return image.flipImage(direction)
    }
}