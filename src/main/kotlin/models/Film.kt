package indi.gromov.models

import io.ks3.java.typealiases.UuidAsString
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@Serializable
data class Film(
    val filmId: UuidAsString,
    val name: String,
    val genreId: UuidAsString,
    @Serializable(with = DurationAsStringSerializer::class) val duration: Duration
)

/**
 * Пользовательский сериализатор для Duration.
 * Преобразует Duration в строку формата "HH:mm:ss" и обратно.
 */
object DurationAsStringSerializer : KSerializer<Duration> {
    override val descriptor = PrimitiveSerialDescriptor("Duration", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Duration) {
        val hours = value.inWholeHours
        val minutes = value.inWholeMinutes % 60
        val seconds = value.inWholeSeconds % 60
        val formattedDuration = String.format("%02d:%02d:%02d", hours, minutes, seconds)
        encoder.encodeString(formattedDuration)
    }

    override fun deserialize(decoder: Decoder): Duration {
        val durationString = decoder.decodeString()
        if (!durationString.matches("\\d{2}:\\d{2}:\\d{2}".toRegex())) {
            throw IllegalArgumentException("Invalid duration format: $durationString")
        }

        val parts = durationString.split(":")
        if (parts.size != 3) {
            throw IllegalArgumentException("Invalid duration format: $durationString")
        }
        val hours = parts[0].toLong()
        val minutes = parts[1].toLong()
        val seconds = parts[2].toLong()
        return hours.hours + minutes.minutes + seconds.seconds
    }
}