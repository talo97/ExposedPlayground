package contextreceivers

data class MangaInfo(val id: Int, val title: String)

interface MangaRepository {
    context(TX) fun create(title: String): Int
    context(TX) fun findAll(): List<MangaInfo>
}

fun mangaRepository() = object : MangaRepository {

    context(TX)
    override fun create(title: String): Int {
        // dbQuery is a context receiver function
        return dbQuery {
            MangaEntity.new {
                this.title = title
            }.id.value
        }
    }

    context(TX)
    override fun findAll(): List<MangaInfo> {
        // dbQuery is a context receiver function
        return dbQuery {
            MangaEntity.all()
                .map { MangaInfo(it.id.value, it.title) }
        }
    }

}
