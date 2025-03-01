package indi.gromov.models

import io.ks3.java.typealiases.UuidAsString
import kotlinx.serialization.Serializable

@Serializable
data class SeatType(
    val seatTypeId: UuidAsString,
    val name: String
)