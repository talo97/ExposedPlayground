package contextreceivers

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

interface TX {
    fun <T> dbQuery(block: TX.() -> T): T
}

fun defaultTransaction() = object : TX {
    override fun <T> dbQuery(block: TX.() -> T): T {
        return transaction { block() }
    }
}

fun databaseConnect() {
    Database.connect(url = "jdbc:h2:mem:testdb", driver = "org.h2.Driver", user = "admin", password = "admin")
}

fun databaseMigration() {
    SchemaUtils.create(MangaTable)
}
