package indi.gromov.ktor.requests

import io.ks3.java.typealiases.UuidAsString
import kotlinx.serialization.Serializable

@Serializable
data class SeatCreateRequest(
    val hallId: Int,
    val rowNumber: Int,
    val seatNumber: Int,
    val seatTypeId: UuidAsString
)