package interfaces

import dataClasses.Technology

interface DataImporter {
    fun importFromCsv(fileName: String): Set<Technology>
    fun importFromJson(json: String): Set<Technology>
}