package contextreceivers

import org.jetbrains.exposed.sql.transactions.transaction

val mangaRepository = mangaRepository()

fun main() {
    // Halo Tibia!
    // Using context receivers to create a transaction in Exposed API

    databaseConnect()
    // this transaction is here only to work around h2 database
    transaction {
        databaseMigration()

        // this code is the main point of this example
        // with method provide scope for context receivers
        with(defaultTransaction()) {
            functionWithContextReceiver()
            mangaRepository.create("Bleach")
            mangaRepository.findAll().forEach {
                println("Found manga: $it")
            }
        }

    }

    // code below won't compile because transaction is not in scope
    // mangaRepository.create("Won't compile :)")
}

context(TX)
fun functionWithContextReceiver() {
    val create = mangaRepository.create("Naruto")
}
