package indi.gromov.models

import io.ks3.java.typealiases.UuidAsString
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Booking(
    val bookingId: UuidAsString,
    val bookingTime: Instant,
    val sessionSeatId: UuidAsString,
    val customerId: UuidAsString
)