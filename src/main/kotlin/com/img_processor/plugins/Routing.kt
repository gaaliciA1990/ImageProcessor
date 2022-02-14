package com.img_processor.plugins

import com.img_processor.ImgManipulators.ManipulateImage
import com.sksamuel.scrimage.ImageParseException
import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.JpegWriter
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.http.HttpHeaders.If
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
        route(ConstantAPI.API_PATH) {
            // access call for rotate any degree
            post(ConstantAPI.API_ROTATE) {
                // upload the image to be manipulated
                val uploadedImage = convertToImmutableImage(call)

                // store the passed value for degrees and convert to an integer
                // If unable to convert to int, return null
                val degree = call.request.queryParameters["degrees"]?.toIntOrNull()

                // verify we have a valid image
                if (uploadedImage == null) {
                    call.response.status(HttpStatusCode.BadRequest)
                    return@post
                }

                // rotate the image based on teh degress
                if (degree != null) {
                    // rotate image
                    val rotatedImage = ManipulateImage(uploadedImage)
                    rotatedImage.rotateImage(degree)

                    // convert rotated image to byte array
                    val returnedImage = convertToByteArray(rotatedImage.image)

                    call.respondBytes(returnedImage)

                } else {
                    call.response.status(HttpStatusCode.BadRequest)
                }
            }

            // access call for rotating left or right 90degrees
            post(ConstantAPI.API_ROTATE90) {
                val uploadedImage = convertToImmutableImage(call)

                // parameters for rotation left or right
                val direction = call.request.queryParameters["direction"]

                // verify we have a valid image
                if (uploadedImage == null) {
                    call.response.status(HttpStatusCode.BadRequest)
                    return@post
                }

                // rotate the image left or right
                if (!direction.isNullOrBlank()) {
                    // rotate image
                    val image = ManipulateImage(uploadedImage)
                    val rotatedImage = image.rotate90LeftOrRight(direction)

                    // convert rotated image to byte array
                    val returnedImage = convertToByteArray(rotatedImage)

                    call.respondBytes(returnedImage)
                } else {
                    call.response.status(HttpStatusCode.BadRequest)
                }
            }

            // access call for adding grayscale filter to image
            post(ConstantAPI.API_GRAY) {
                val uploadedImage = convertToImmutableImage(call)

                // verify we have a valid image
                if (uploadedImage == null) {
                    call.response.status(HttpStatusCode.BadRequest)
                    return@post
                }

                // filter the image to grayscale
                val image = ManipulateImage(uploadedImage)
                val filteredImage = image.convertToGrayscale()

                // convert filtered image to byte array
                val returnedImage = convertToByteArray(filteredImage.toImmutableImage())
                call.respondBytes(returnedImage)
            }

            // access call for resize image based on width and height values, optionally
            // based on pixel size
            post(ConstantAPI.API_RESIZE) {
                val uploadedImage = convertToImmutableImage(call)

                // store parameter for width and height as integers
                val width = call.request.queryParameters["width"]?.toIntOrNull()
                val height = call.request.queryParameters["height"]?.toIntOrNull()

                // verify we have a valid image
                if (uploadedImage == null) {
                    call.response.status(HttpStatusCode.BadRequest)
                    return@post
                }

                val image = ManipulateImage(uploadedImage)

                // resize the image based on width and/or height
                if (width == null && height == null) {
                    call.response.status(HttpStatusCode.BadRequest)
                } else if (width != null && height != null) {
                    val resizedImage = image.resizeImage(width, height)

                    // convert resized image to bytes
                    val returnedImage = convertToByteArray(resizedImage)

                    call.respondBytes(returnedImage)
                } else if (height != null) {
                    val resizedImage = image.resizeImageHeight(height)

                    // covert resized image to bytes
                    val returnedImage = convertToByteArray(resizedImage)

                    call.respondBytes(returnedImage)
                } else {
                    val resizedImage = image.resizeImageWidth(requireNotNull(width))

                    // covert resized image to bytes
                    val returnedImage = convertToByteArray(resizedImage)

                    call.respondBytes(returnedImage)
                }
            }

            // access call for converting image to a thumbnail size
            post(ConstantAPI.API_THUMBNAIL) {
                val uploadedImage = convertToImmutableImage(call)

                // verify we have a valid image
                if (uploadedImage == null) {
                    call.response.status(HttpStatusCode.BadRequest)
                    return@post
                }

                // create thumbnail image
                val image = ManipulateImage(uploadedImage)
                val thumbnailImage = image.resizeImageToThumbnail()

                // convert thumbnail image to Bytes
                val returnedImage = convertToByteArray(thumbnailImage)
                call.respondBytes(returnedImage)
            }

            // access call for flipping image
            post(ConstantAPI.API_FLIP) {
                val uploadedImage = convertToImmutableImage(call)

                val direction = call.request.queryParameters["direction"]

                // verify we have a valid image
                if (uploadedImage == null) {
                    call.response.status(HttpStatusCode.BadRequest)
                    return@post
                }

                // flip the image based on direction
                if (!direction.isNullOrBlank()) {
                    val image = ManipulateImage(uploadedImage)

                    // flip the image
                    val flippedImage = image.flipImage(direction)

                    // convert flipped image to byte array
                    val returnedImage = convertToByteArray(flippedImage)

                    call.respondBytes(returnedImage)
                } else {
                    call.response.status(HttpStatusCode.BadRequest)
                }
            }
        }
    }
}

/**
 * Co-routine to upload the image for manipulation
 */
suspend fun convertToImmutableImage(call: ApplicationCall): ImmutableImage? {
    // channel to read the image bytes from
    val image = call.receiveChannel()

    // create an array of byes
    var byteArray = byteArrayOf()

    // populate the byte array with data from the image file
    while (!image.isClosedForRead) {
        byteArray += image.readByte()
    }

    // return an immutable image, only if it's a valid image
    try {
        return ImmutableImage.loader().fromBytes(byteArray)
    } catch (exception: ImageParseException) {
        return null
    }
}

/**
 * Convert [image] to a bytearray for rendering back to download
 *
 * Returns a ByteArray
 */
fun convertToByteArray(image: ImmutableImage): ByteArray {

    return image.bytes(JpegWriter.Default)
}