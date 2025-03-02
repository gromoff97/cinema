package indi.gromov.ktor.requests

import kotlinx.serialization.Serializable

@Serializable
data class GenreCreateRequest(
    val name: String
)