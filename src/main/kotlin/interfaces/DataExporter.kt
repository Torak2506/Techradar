package interfaces

import dataClasses.Radar
import java.io.File

interface DataExporter {
    fun exportToHtml(radar: Radar): String
    fun exportToPdf(radar: Radar,path:String): File
}