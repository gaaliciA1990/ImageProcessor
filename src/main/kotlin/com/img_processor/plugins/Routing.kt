package com.img_processor.plugins

import com.img_processor.ImgManipulators.DegreeRotate
import com.sksamuel.scrimage.ImmutableImage
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
            // access for rotate any degree
            post(ConstantAPI.API_ROTATE) {
                // upload the image to be manipulated
                val image = ImageUpload(call)

                // store the passed value for degrees and convert to an integer
                // If unable to convert to int, return null
                val degree = call.request.queryParameters["degrees"]?.toIntOrNull()

                if (degree != null) {
                    //Check the value of degree to determine which direction to rotate
                    if (degree != 0) {
                        DegreeRotate(image, degree)
                        call.respondText("Image successfully rotated")
                    }
                    else {
                        call.respondText("No rotation")
                    }
                }
                else {
                    call.response.status(HttpStatusCode.BadRequest)
                }
            }

            // access for rotating left or right 90degrees
            post(ConstantAPI.API_ROTATE90){
                // parameters for rotation left or right
                val direction = call.request.queryParameters["direction"]

                if (direction != null) {
                    // check if the direction is left or right and rotate 90 degrees.
                    // otherwise, return a bad request error
                    when (direction) {
                        "left" -> {
                            call.respondText("Rotating image left")
                        }
                        "right" -> {
                            call.respondText("Rotating image right")
                        }
                        else -> {
                            call.response.status(HttpStatusCode.BadRequest)
                        }
                    }
                }
                call.respondText("Rotating l or r")
            }

            // access for adding grayscale to image
            post(ConstantAPI.API_GRAY) {
                call.respondText("Add grayscale")
            }

            // access for resize image
            post(ConstantAPI.API_RESIZE) {
                call.respondText("Image resize")
            }

            // access for converting image to a thumbnail size
            post(ConstantAPI.API_THUMBNAIL) {
                call.respondText("Image thumbnail")
            }

            // access for flipping image
            post(ConstantAPI.API_FLIP) {
                call.respondText("Flip horizontally or vertically")
            }
        }
    }
}

/**
 * Co-routine to upload the image for manipulation
 */
suspend fun ImageUpload(call: ApplicationCall): ImmutableImage {
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