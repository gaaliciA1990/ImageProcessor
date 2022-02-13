package com.img_processor

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.img_processor.plugins.*

/**
 * Server class for managing service calls
 */
fun main() {
    embeddedServer(Netty, port = ConstantsAPI.PORT, host = "0.0.0.0") {
        configureRouting()
    }.start(wait = true)
}
