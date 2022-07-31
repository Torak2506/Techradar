package ioClasses

import assertk.assertThat
import assertk.assertions.isEqualTo
import dataClasses.Radar
import dataClasses.Technology
import dataClasses.generateRadar
import enums.Category
import enums.Ring
import enums.Stability
import interfaces.DataImporter
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

internal class RadarDataImporterTest {
    private val testI: DataImporter = RadarDataImporter()

    @Test
    fun importTest() {
        val res = testI.importFromCsv("src/main/resources/testData.csv")
        val exp = mutableSetOf<Technology>()
        exp.add(Technology("Amazon Redshift","", Category.valueOf("DATASTORE"), Ring.valueOf("TRIAL"),Stability.valueOf("STABLE")))
        exp.add(Technology("Druid","", Category.valueOf("DATASTORE"), Ring.valueOf("TRIAL"),Stability.valueOf("UP")))
        exp.add(Technology("AWS Data Pipeline","", Category.valueOf("DATAMANAGEMENT"), Ring.valueOf("HOLD"),Stability.valueOf("STABLE")))
        exp.add(Technology("Google Bigtable","", Category.valueOf("DATASTORE"), Ring.valueOf("ASSESS"),Stability.valueOf("DOWN")))
        assertThat(res).isEqualTo(exp)
    }

    @Test
    fun importJsonTest()
    {
        val res = testI.importFromJson("src/main/resources/testData.json")
        val exp = mutableSetOf<Technology>()
        exp.add(Technology("Amazon Redshift","", Category.valueOf("DATASTORE"), Ring.valueOf("TRIAL"),Stability.valueOf("STABLE")))
        exp.add(Technology("Druid","", Category.valueOf("DATASTORE"), Ring.valueOf("TRIAL"),Stability.valueOf("UP")))
        exp.add(Technology("AWS Data Pipeline","", Category.valueOf("DATAMANAGEMENT"), Ring.valueOf("HOLD"),Stability.valueOf("STABLE")))
        exp.add(Technology("Google Bigtable","", Category.valueOf("DATASTORE"), Ring.valueOf("ASSESS"),Stability.valueOf("DOWN")))
        assertThat(res).isEqualTo(exp)
    }



    @Test
    fun exportPdfTest()
    {
        val imported: Set<Technology> = testI.importFromJson("src/main/resources/data2.json")
        val testE = RadarExporter(Radar(imported,""))
        assertEquals(true,testE.exportToPdf("src/main/resources/").exists())
    }

    @Test
    fun exportHtmlTest()
    {
        val formater = DateTimeFormatter.ofPattern("HH-mm-ss")
        val imported: Set<Technology> = testI.importFromJson("src/main/resources/data2.json")
        val testE = RadarExporter(Radar(imported,""))
        val htmlString = testE.exportToHtml()
        val htmlFile = File("src/main/resources/Radar " + LocalDate.now() + " " + LocalTime.now().format(formater) + ".html").writeText(htmlString)
    }
}