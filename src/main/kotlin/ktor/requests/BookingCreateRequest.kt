package indi.gromov.ktor.requests

import io.ks3.java.typealiases.UuidAsString
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class BookingCreateRequest(
    val bookingTime: Instant,
    val sessionSeatId: UuidAsString,
    val customerId: UuidAsString
)