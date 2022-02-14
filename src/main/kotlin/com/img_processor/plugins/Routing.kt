package com.img_processor.plugins

import com.img_processor.ImgManipulators.ManipulateImage
import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.JpegWriter
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*


/**
 * Function for mapping the routing for the API
 */
fun Application.configureRouting() {

    // Starting point for a Ktor app:
    routing {
        //set default route mapping
        route(ConstantAPI.API_PATH){
            // access call for rotate any degree
            post(ConstantAPI.API_ROTATE) {
                // upload the image to be manipulated
                val image = convertToImmutableImage(call)

                // store the passed value for degrees and convert to an integer
                // If unable to convert to int, return null
                val degree = call.request.queryParameters["degrees"]?.toIntOrNull()

                if (degree != null) {
                    //Check the value of degree to determine which direction to rotate
                    if (degree != 0) {
                        // rotate image
                        val rotatedImg = ManipulateImage(image)
                        rotatedImg.rotateImage(degree)

                        // convert rotated image to byte array
                        val returnedImg = convertToByteArray(rotatedImg.image)

                        call.respondBytes(returnedImg)
                    }
                    else {
                        call.respondBytes(convertToByteArray(image))
                    }
                }
                else {
                    call.response.status(HttpStatusCode.BadRequest)
                }
            }

            // access call for rotating left or right 90degrees
            post(ConstantAPI.API_ROTATE90){
                val image = convertToImmutableImage(call)

                // parameters for rotation left or right
                val direction = call.request.queryParameters["direction"]

                if (direction != null) {
                    // check if the direction is left or right and rotate 90 degrees.
                    // otherwise, return a bad request error
                    when (direction) {
                        "left" -> {
                            // rotate image
                            val img = ManipulateImage(image)
                            val rotatedImg = img.rotateCounterClockwise()

                            // convert rotated image to byte array
                            val returnedImg = convertToByteArray(rotatedImg)

                            call.respondBytes(returnedImg)
                        }
                        "right" -> {
                            // rotate image
                            val img = ManipulateImage(image)
                            val rotatedImg = img.rotateClockwise()

                            // convert rotated image to byte array
                            val returnedImg = convertToByteArray(rotatedImg)

                            call.respondBytes(returnedImg)
                        }
                        else -> {
                            call.response.status(HttpStatusCode.BadRequest)
                        }
                    }
                }
            }

            // access call for adding grayscale filter to image
            post(ConstantAPI.API_GRAY) {
                val image = convertToImmutableImage(call)

                if (image != null) {
                    val img = ManipulateImage(image)
                    val filterImg = img.convertToGrayscale()

                    // convert filtered image to byte array
                    val returnedImg = convertToByteArray(filterImg.toImmutableImage())
                    call.respondBytes(returnedImg)
                }
                else {
                    call.response.status(HttpStatusCode.BadRequest)
                }
            }

            // access call for resize image based on width and height values, optionally
            post(ConstantAPI.API_RESIZE) {
                val image = convertToImmutableImage(call)

                // store parameter for width and height as integers
                val width = call.request.queryParameters["width"]?.toIntOrNull()
                val height = call.request.queryParameters["height"]?.toIntOrNull()

                val img = ManipulateImage(image)

                if (width == null && height == null) {
                    call.response.status(HttpStatusCode.BadRequest)
                }
                else if (width != null && height != null) {
                    val resizedImg = img.resizeImage(width, height)

                    // convert resized image to bytes
                    val returnedImg = convertToByteArray(resizedImg)

                    call.respondBytes(returnedImg)
                }
                else if (height !=null) {
                    val resizedImg = img.resizeImageHeight(height)

                    // covert resized image to bytes
                    val returnedImg = convertToByteArray(resizedImg)

                    call.respondBytes(returnedImg)
                }
                else {
                    val resizedImg = img.resizeImageWidth(requireNotNull(width))

                    // covert resized image to bytes
                    val returnedImg = convertToByteArray(resizedImg)

                    call.respondBytes(returnedImg)
                }
            }

            // access call for converting image to a thumbnail size
            post(ConstantAPI.API_THUMBNAIL) {
                call.respondText("Image thumbnail")
            }

            // access call for flipping image
            post(ConstantAPI.API_FLIP) {
                call.respondText("Flip horizontally or vertically")
            }
        }
    }
}

/**
 * Co-routine to upload the image for manipulation
 */
suspend fun convertToImmutableImage(call: ApplicationCall): ImmutableImage {
    // channel to read the image bytes from
    val image = call.receiveChannel()

    // create an array of byes
    var byteArray = byteArrayOf()

    // populate the byte array with data from the image file
    while (!image.isClosedForRead) {
        byteArray += image.readByte()
    }

    // Save file to hard disk
    return ImmutableImage.loader().fromBytes(byteArray)
}

/**
 * Convert [image] to a bytearray for rendering
 *
 * Returns a ByteArray
 */
fun convertToByteArray(image: ImmutableImage): ByteArray {

    return image.bytes(JpegWriter.Default)
}