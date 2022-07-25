package ioClasses

import assertk.assertThat
import assertk.assertions.isEqualTo
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
    private val testE = RadarExporter()

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
        assertEquals(true,testE.exportToPdf(imported.generateRadar("Radar "+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss"))),"src/main/resources/").exists())
    }

    @Test
    fun exportHtmlTest()
    {
        val formater = DateTimeFormatter.ofPattern("HH-mm-ss")
        val imported: Set<Technology> = testI.importFromJson("src/main/resources/data2.json")
        val htmlString = testE.exportToHtml(imported.generateRadar("Radar "+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss"))))
        val htmlFile = File("src/main/resources/Radar " + LocalDate.now() + " " + LocalTime.now().format(formater) + ".html").writeText(htmlString)
    }
}