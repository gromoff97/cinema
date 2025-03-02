package indi.gromov.ktor.requests

import kotlinx.serialization.Serializable

@Serializable
data class SeatTypeCreateRequest(
    val name: String
)