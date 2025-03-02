package indi.gromov.ktor.requests

import kotlinx.serialization.Serializable

@Serializable
data class HallCreateRequest(
    val alternativeName: String
)