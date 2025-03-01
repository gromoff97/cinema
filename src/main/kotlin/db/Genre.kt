package indi.gromov.db

import indi.gromov.models.Genre
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

class GenreRepository {
    suspend fun allGenres(): List<Genre> = suspendTransaction {
        GenreDao.all().map(::genreDaoToModel)
    }

    suspend fun insertGenre(genre: Genre) = suspendTransaction {
        GenreDao.new {
            name = genre.name
        }
    }
}

fun genreDaoToModel(dao: GenreDao): Genre {
    return Genre(
        genreId = dao.id.value,
        name = dao.name
    )
}