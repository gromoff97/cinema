package indi.gromov.db

import indi.gromov.ktor.requests.GenreCreateRequest
import indi.gromov.models.Genre
import indi.gromov.utils.transaction.extensions.toModel
import indi.gromov.utils.transaction.suspendTransaction
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.UUID

object GenreTable : UUIDTable("genre", "genre_id") {
    val name = text("name")
}

class GenreDao(id: EntityID<UUID>): UUIDEntity(id) {
    companion object: UUIDEntityClass<GenreDao>(GenreTable)

    var name by GenreTable.name
}

object GenreRepository {
    suspend fun allGenres(): List<Genre> = suspendTransaction {
        GenreDao.all().map { it.toModel() }
    }

    suspend fun insertGenre(genre: GenreCreateRequest) = suspendTransaction {
        GenreDao.new {
            name = genre.name
        }
    }
}