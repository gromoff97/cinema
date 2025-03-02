package indi.gromov.ktor.requests

import io.ks3.java.typealiases.UuidAsString
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class SessionCreateRequest(
    val hallId: Int,
    val filmId: UuidAsString,
    val startTime: Instant
)