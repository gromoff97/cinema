package indi.gromov.models

import io.ks3.java.typealiases.UuidAsString
import kotlinx.serialization.Serializable

@Serializable
data class Genre(
    val genreId: UuidAsString,
    val name: String
)