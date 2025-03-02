package indi.gromov.ktor.requests

import indi.gromov.models.DurationAsStringSerializer
import io.ks3.java.typealiases.UuidAsString
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
data class FilmCreateRequest(
    val name: String,
    val genreId: UuidAsString,
    @Serializable(with = DurationAsStringSerializer::class) val duration: Duration
)

