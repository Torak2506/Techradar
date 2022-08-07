package OLD

interface DataImporter {
    fun importFromCsv(fileName: String): Set<Technology>
    fun importFromJson(json: String): Set<Technology>
}