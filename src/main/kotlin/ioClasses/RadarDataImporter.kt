package ioClasses

import dataClasses.Technology
import enums.Category
import enums.Ring
import enums.Stability
import interfaces.DataImporter
import java.io.File
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RadarDataImporter: DataImporter {

    override fun importFromCsv(csvPath: String): Set<Technology> {
        val technologies = mutableSetOf<Technology>();
        var file = File(csvPath)
        var lines = file.readLines()
        lines=lines.subList(1,lines.size)
        lines.forEach { l ->
            val line = l.split(';')

            technologies.add(Technology(line[0],line[1], Category.valueOf(line[2].uppercase()), Ring.valueOf(line[3].uppercase()),
                Stability.valueOf(line[4].uppercase())))
        }
        technologies.forEach{i ->println(i) }
        return technologies;
    }

    override fun importFromJson(json: String): Set<Technology> {
        val fileContent = File(json).bufferedReader().use { it.readText() }
        val gson=Gson()
        val typeT = object :TypeToken<Set<Technology>>(){}.type
        val technologies: Set<Technology>  = gson.fromJson(fileContent, typeT)
        technologies.forEach{i ->println(i) }
        return technologies
    }

}