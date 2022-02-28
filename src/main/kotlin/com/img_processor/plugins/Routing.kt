package com.img_processor.plugins

import ConstantAPI
import com.sksamuel.scrimage.ImageParseException
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
    val controller = ApiController()

    // Starting point for a Ktor app:
    routing {
        //set default route mapping
        route(ConstantAPI.API_PATH) {
            // call to manipulate an image with any/all options
            post(ConstantAPI.API_COMBO) {
                // upload the image to be manipulated
                val uploadedImage = convertToImmutableImage(call)

                // verify we have a valid image
                if (uploadedImage == null) {
                    call.response.status(HttpStatusCode.BadRequest)
                    return@post
                }

                // parse the string of commands to a list of string, based on commas
                val commands: List<String>? = call.request.queryParameters["combo"]?.split(",")

                // check our commands list is not null.
                if (commands == null) {
                    call.response.status(HttpStatusCode.BadRequest)
                    return@post
                }

                val validRequest = controller.combinationImage(commands, uploadedImage)

                // verify the commands are valid, return bad request if not
                if (validRequest == null) {
                    call.response.status(HttpStatusCode.BadRequest)
                    return@post
                } else {
                    // return manipulated image as a byte
                    val returnedImage = convertToByteArray(validRequest)
                    call.respondBytes(returnedImage)
                }
            }

            // access call for rotate any degree
            post(ConstantAPI.API_ROTATE) {
                // upload the image to be manipulated
                val uploadedImage = convertToImmutableImage(call)

                // store the passed value for degrees and convert to an integer
                // If unable to convert to int, set to null
                val degree = call.request.queryParameters["degrees"]?.toIntOrNull()

                // verify we have a valid image
                if (uploadedImage == null) {
                    call.response.status(HttpStatusCode.BadRequest)
                    return@post
                }

                // call api controller to manipulate the image if applicable
                if (degree != null) {
                    // rotate the image by degrees and convert returned image back to bytes
                    val returnedImage = convertToByteArray(controller.rotateDegreesImage(degree, uploadedImage))
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

                // call api controller to rotate the image left or right
                if (!direction.isNullOrBlank()) {
                    // rotate the image left or right and convert rotated image to byte array
                    val returnedImage = convertToByteArray(controller.rotateImage(direction, uploadedImage))
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
                // call controller to add grayscaled filter and convert filtered image to byte array
                val returnedImage = convertToByteArray(controller.grayscaleImage(uploadedImage))
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

                // resize the image based on width and/or height
                if (width == null && height == null) {
                    call.response.status(HttpStatusCode.BadRequest)
                } else {
                    // call api controller to resize image and convert image to bytes
                    val returnedImage = convertToByteArray(controller.resizeImage(width, height, uploadedImage))
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

                // call controller to convert image to thumbnail and convert image to bytes
                val returnedImage = convertToByteArray(controller.thumbnailImage(uploadedImage))
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
                    // call controller to flip image and convert image to bytes
                    val returnedImage = convertToByteArray(controller.flipImage(direction, uploadedImage))
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
 * We do not store the image since this would raise security issues,
 * and I have no need for their images
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
    return try {
        ImmutableImage.loader().fromBytes(byteArray)
    } catch (exception: ImageParseException) {
        null
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