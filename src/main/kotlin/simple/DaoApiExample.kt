package simple

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun main() {
    // Halo Tibia!
    // Exposed DAO API example
    Database.connect(url = "jdbc:h2:mem:testdb", driver = "org.h2.Driver", user = "admin", password = "admin")
    transaction {
        SchemaUtils.create(MangaTable, ChapterTable, GenreTable, GenreMangaTable)

        val firstManga = MangaEntity.new { title = "FirstManga" }

        // creating new chapters for firstManga
        ChapterEntity.new {
            manga = firstManga
            chapterNumber = 1
        }
        ChapterEntity.new {
            manga = firstManga
            chapterNumber = 2
        }

        // adding genres
        GenreEntity.new {
            name = "Action"
        }
        GenreEntity.new {
            name = "Adventure"
        }
        val tibiaGenre = GenreEntity.new {
            name = "Tibia"
        }

        // adding genres to firstManga
        // and examples of different ways to find genre
        GenreMangaEntity.new {
            manga = firstManga
            // may throw NoSuchElementException if genre not found
            genre = GenreEntity.find { GenreTable.name eq "Adventure" }.first()
        }
        GenreMangaEntity.new {
            manga = firstManga
            genre = tibiaGenre
        }

        println("Manga title: \n$firstManga.title")
        println("Manga chapters:")
        println(firstManga.chapters.forEach { println("Chapter: ${it.chapterNumber}") })
        println("Manga genres:")
        println(firstManga.genres.forEach { println("Genre: ${it.name}") })
    }

//    transaction {
//        GenreEntity.all().forEach { println("Genre: ${it.name}") }
//    }

}

object MangaTable : IntIdTable(name = "manga") {
    val title = varchar("name", length = 128)
}

class MangaEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<MangaEntity>(MangaTable)

    var title by MangaTable.title

    // example of one-to-many relationship
    val chapters by ChapterEntity referrersOn ChapterTable.manga

    // example of many-to-many relationship
    var genres by GenreEntity via GenreMangaTable
}

// Chapters for many-to-one relationship example
object ChapterTable : IntIdTable(name = "chapter") {
    val manga = reference("manga", MangaTable)
    val chapterNumber = integer("chapter_number")
}

class ChapterEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ChapterEntity>(ChapterTable)

    // example of many-to-one relationship
    var manga by MangaEntity referencedOn ChapterTable.manga
    var chapterNumber by ChapterTable.chapterNumber
}

// Genres for many-to-many relationship example
object GenreTable : IntIdTable(name = "genre") {
    val name = varchar("name", length = 128)
}

class GenreEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<GenreEntity>(GenreTable)

    var name by GenreTable.name
}

object GenreMangaTable : IntIdTable(name = "manga_genre") {
    val manga = reference("manga", MangaTable)
    val genre = reference("genre", GenreTable)
}

class GenreMangaEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<GenreMangaEntity>(GenreMangaTable)

    var manga by MangaEntity referencedOn GenreMangaTable.manga
    var genre by GenreEntity referencedOn GenreMangaTable.genre
}
