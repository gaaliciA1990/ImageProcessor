package com.img_processor

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.img_processor.plugins.*

/**
 * Server class for managing service calls
 */
fun main() {
    embeddedServer(Netty, port = ConstantAPI.PORT, host = ConstantAPI.HOST) {
        configureRouting()
    }.start(wait = true)
}
