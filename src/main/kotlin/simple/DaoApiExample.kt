package simple

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun main() {
    // Halo Tibia!
    // Exposed DAO API example
    Database.connect(url = "jdbc:h2:mem:testdb", driver = "org.h2.Driver", user = "admin", password = "admin")
    simpleExamples()
    transactionRollbacks()
}

fun transactionRollbacks() {
    // checking if rollback are working as expected
    transaction {
        SchemaUtils.create(MangaTable, ChapterTable, GenreTable, GenreMangaTable)
        try {
            transaction {
                transaction {
                    MangaEntity.new { title = "Should be rollbacked" }
                }
                MangaEntity.new { title = "Should be rollbacked2" }
                throw RuntimeException("Rollback")
            }
        } catch (e: Exception) {
            rollback()
            println("Rollbacked")
        }
        MangaEntity.new { title = "Should be added" }
        println("Manga titles:")
        MangaEntity.all().forEach { println(it.title) }
    }
}

fun simpleExamples() {
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
}
