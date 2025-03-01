package indi.gromov.models

import io.ks3.java.math.BigDecimalAsString
import io.ks3.java.typealiases.UuidAsString
import kotlinx.serialization.Serializable

@Serializable
data class SessionSeat(
    val seatSessionId: UuidAsString,
    val sessionId: UuidAsString,
    val seatId: UuidAsString,
    val price: BigDecimalAsString
)