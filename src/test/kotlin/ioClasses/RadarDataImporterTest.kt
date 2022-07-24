package ioClasses

import interfaces.DataImporter
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class RadarDataImporterTest {
    private val test: DataImporter = RadarDataImporter()

    @Test
    fun importTest()
    {
        test.importFromCsv("src/main/resources/data.csv")
    }

    @Test
    fun importJsonTest()
    {
        test.importFromJson("src/main/resources/data2.json")
    }
}