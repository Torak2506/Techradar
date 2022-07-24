package ioClasses

import dataClasses.Radar
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class RadarExporterTest
{
    private val test = RadarExporter()
    private val importer = RadarDataImporter()

    @Test
    fun exportPdfTest()
    {
        test.exportToPdf(Radar(importer.importFromJson("src/main/resources/data2.json"),""),"src/main/resources/")
    }
}