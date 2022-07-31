package interfaces

import dataClasses.Radar
import java.io.File

interface DataExporter<R> {
    fun exportToHtml(): String
    fun exportToPdf(path: String?): File}