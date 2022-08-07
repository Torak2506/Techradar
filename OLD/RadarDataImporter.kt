package OLD

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import enums.Category
import enums.Ring
import enums.Stability
import java.io.File
import java.nio.charset.StandardCharsets.UTF_8

class RadarDataImporter: DataImporter {

    override fun importFromCsv(csvPath: String): Set<Technology> {
        val technologies = mutableListOf<Technology>()
        var file = File(csvPath).readLines(UTF_8).drop(1).forEach() {
            val line = it.split(';')
            technologies.add(
                Technology(
                    name = line[0],
                    description = line[1],
                    category = Category.valueOf(line[2].uppercase()),
                    ring = Ring.valueOf(line[3].uppercase()),
                    stability = Stability.valueOf(line[4].uppercase())
                )
            )
        }
        return technologies.toSet();
    }

    override fun importFromJson(json: String): Set<Technology> {
        val fileContent = File(json).bufferedReader().use { it.readText() }
        val mapper = jacksonObjectMapper();
        val technologies:List<Technology> = mapper.readValue(fileContent)
        //technologies.forEach{i ->println(i) }
        return technologies.toSet()
    }

}