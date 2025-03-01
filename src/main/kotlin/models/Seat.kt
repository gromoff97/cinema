package indi.gromov.models

import io.ks3.java.typealiases.UuidAsString
import kotlinx.serialization.Serializable

@Serializable
data class Seat(
    val seatId: UuidAsString,
    val hallId: Int,
    val rowNumber: Int,
    val seatNumber: Int,
    val seatTypeId: UuidAsString
)