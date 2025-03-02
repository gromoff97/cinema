package indi.gromov.ktor.requests

import io.ks3.java.math.BigDecimalAsString
import io.ks3.java.typealiases.UuidAsString
import kotlinx.serialization.Serializable

@Serializable
data class SessionSeatCreateRequest(
    val sessionId: UuidAsString,
    val seatId: UuidAsString,
    val price: BigDecimalAsString
)