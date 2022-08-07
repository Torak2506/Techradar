package ioClasses

import assertk.assertThat
import assertk.assertions.isEqualTo
import dataClasses.*
import enums.Stability
import interfaces.DataImporter
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.File
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

internal class RadarDataImporterTest {
    private val testI: DataImporter = RadarDataImport()

    /*
    @Test
    fun exportPdfTest()
    {
        val imported: Set<Technology> = testI.importFromJson("src/main/resources/data2.json")
        val testE = RadarExporter(Radar(imported,""))
        assertEquals(true,testE.exportToPdf("src/main/resources/").exists())
    }*/

    @Test
    fun exportHtmlTest()
    {
        val formater = DateTimeFormatter.ofPattern("HH-mm-ss")
        val imported: Set<Technology> = testI.importFromJson("src/main/resources/data2.json")
        val testE = RadarExporter(Radar(imported,""))
        val htmlString = testE.exportToHtml()
        val htmlFile = File("src/main/resources/Radar " + LocalDate.now() + " " + LocalTime.now().format(formater) + ".html").writeText(htmlString)
    }


    @Test
    fun importJSON()
    {
        val res = testI.importFromJson("src/main/resources/testData.json")
        val exp = mutableSetOf<Technology>()
        exp.add(Technology("Amazon Redshift","", "DATASTORE", "TRIAL",Stability.valueOf("STABLE")))
        exp.add(Technology("Druid","", "DATASTORE","TRIAL",Stability.valueOf("UP")))
        exp.add(Technology("AWS Data Pipeline","", "DATAMANAGEMENT", "HOLD",Stability.valueOf("STABLE")))
        exp.add(Technology("Google Bigtable","","DATASTORE", "ASSESS",Stability.valueOf("DOWN")))
        assertThat(res).isEqualTo(exp)
    }

    @Test
    fun importCSV()
    {
        val res = testI.importFromCsv("src/main/resources/testData.csv")
        val exp = mutableSetOf<Technology>()
        exp.add(Technology("Amazon Redshift","", "DATASTORE", "TRIAL",Stability.valueOf("STABLE")))
        exp.add(Technology("Druid","", "DATASTORE","TRIAL",Stability.valueOf("UP")))
        exp.add(Technology("AWS Data Pipeline","", "DATAMANAGEMENT", "HOLD",Stability.valueOf("STABLE")))
        exp.add(Technology("Google Bigtable","","DATASTORE", "ASSESS",Stability.valueOf("DOWN")))
        assertThat(res).isEqualTo(exp)
    }
}