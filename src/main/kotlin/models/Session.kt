package indi.gromov.models

import io.ks3.java.typealiases.UuidAsString
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Session(
    val sessionId: UuidAsString,
    val hallId: Int,
    val filmId: UuidAsString,
    val startTime: Instant
)