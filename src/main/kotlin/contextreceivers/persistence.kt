package contextreceivers

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

interface TX

fun defaultTransaction() = object : TX {}

fun <T> TX.dbQuery(block: TX.() -> T): T = transaction { block() }

fun databaseConnect() {
    Database.connect(url = "jdbc:h2:mem:testdb", driver = "org.h2.Driver", user = "admin", password = "admin")
}

fun databaseMigration() {
    SchemaUtils.create(MangaTable)
}
