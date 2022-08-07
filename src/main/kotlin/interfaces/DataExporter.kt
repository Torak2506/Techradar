package interfaces

import java.io.File

interface DataExporter<R> {
    fun exportToHtml(): String
    fun exportToPdf(path: String?): File}