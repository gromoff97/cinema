package indi.gromov.db

import indi.gromov.ktor.requests.FilmCreateRequest
import indi.gromov.models.Film
import indi.gromov.utils.transaction.extensions.toModel
import indi.gromov.utils.transaction.suspendTransaction
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table
import org.postgresql.util.PGInterval
import java.util.UUID
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

object FilmTable : UUIDTable("film", "film_id") {
    val name = text("name")
    val genreId = uuid("genre_id")
    val duration = durationAsPgInterval("duration")
}

class FilmDao(id: EntityID<UUID>): UUIDEntity(id) {
    companion object: UUIDEntityClass<FilmDao>(FilmTable)

    var name by FilmTable.name
    var genreId by FilmTable.genreId
    var duration by FilmTable.duration
}

object FilmRepository {
    suspend fun allFilms(): List<Film> = suspendTransaction {
        FilmDao.all().map { it.toModel() }
    }

    suspend fun insertFilm(film: FilmCreateRequest) = suspendTransaction {
        FilmDao.new {
            name = film.name
            genreId = film.genreId
            duration = film.duration
        }
    }
}

class DurationColumnType : ColumnType<Duration>() {
    override fun sqlType(): String = "INTERVAL"

    override fun valueFromDB(value: Any): Duration {
        return when (value) {
            is PGInterval -> with(value) {
                if (years != 0 || months != 0 || days != 0 || microSeconds != 0) {
                    throw IllegalStateException("Можно хранить только часы, минуты и секунды")
                }
                hours.hours + minutes.minutes + seconds.seconds
            }
            is Duration -> value
            else -> throw IllegalArgumentException("Unsupported value type: ${value::class}")
        }
    }

    override fun notNullValueToDB(value: Duration): Any {
        return PGInterval(0, 0, 0,
            value.inWholeHours.toInt(),
            value.inWholeMinutes.mod(60),
            value.inWholeSeconds.mod(60).toDouble()
        )
    }
}

fun Table.durationAsPgInterval(name: String): Column<Duration> = registerColumn(name, DurationColumnType())