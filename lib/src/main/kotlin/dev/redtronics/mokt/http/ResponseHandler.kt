package dev.redtronics.mokt.http

import io.ktor.client.statement.*

object ResponseHandler {
    fun <T : HttpResponse> validate(response: T) {
        when (response.status.value) {
            StatusCode.NOT_FOUND.code -> throw IllegalArgumentException("not found")
            StatusCode.TOO_MANY_REQUESTS.code -> throw TooManyRequestsException()
        }
    }
}