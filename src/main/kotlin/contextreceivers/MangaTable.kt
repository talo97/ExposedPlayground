package contextreceivers

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object MangaTable : IntIdTable(name = "manga") {
    val title = varchar("name", length = 128)
}

class MangaEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<MangaEntity>(MangaTable)

    var title by MangaTable.title
}
