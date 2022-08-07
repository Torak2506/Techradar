package ioClasses


import dataClasses.Radar
import dataClasses.Technology
import enums.Stability
import interfaces.DataExporter
import java.io.File
import java.lang.Math.*
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.cos
import kotlin.math.sqrt
import kotlin.random.Random

class RadarExporter(val radar:Radar):DataExporter<Radar> {

    override fun exportToHtml(): String {
        val techs = radar.technologies
        val categories = techs.map { it.category }.toSet()
        val rings = techs.map { it.ring }.toSet()
        val techsSorted = techs.toList()
        val lists = mutableListOf<String>()
        categories.forEachIndexed() { index, i ->
            var l = "<div style=\"display:inline-block;width:150px\">"
            l += "<p><strong>" + i + "</strong></p>"
            lists.add(l)
        }
        techs.forEachIndexed { techCount, it ->
            categories.forEachIndexed { index, category ->
                if (it.category == category) {
                    lists.set(
                        index,
                        lists.elementAt(index).plus("<p>" + (techCount + 1).toString() + " " + it.name + "</p>")
                    )
                }
            }
        }
        for (i in 0 until lists.size) {
            lists.set(i, lists.elementAt(i).plus("</div>"))
        }
        //lists.forEach{ println(it) }
        var html = "<!DOCTYPE HTML>\n" +
                "<html>\n" +
                "<head>\n" +
                "<meta charset=\"utf-8\">\n" +
                writeJS(techsSorted, rings, categories) +
                "<title>Techradar</title>\n" +
                "</head>\n" +
                "<body onload=\"draw()\">\n" +
                "<h1>${radar.something}</h1>\n" +
                "<canvas id=\"radar\", width=800, height=800 style=\"display:block\">\n" +
                "</canvas>\n"

        lists.forEach {
            html = html.plus(it)
        }

        html += "</body>\n" +
                "</html>"

        return html
    }

    override fun exportToPdf(path: String?): File {
        TODO("Not yet implemented")
    }


    private fun writeJS(techs: List<Technology>, rings: Set<String>, categories: Set<String>): String {
        var js = "<script>\nfunction draw(){" +
                "\nvar c = document.getElementById(\"radar\");\n" +
                "var ctx = c.getContext(\"2d\");\n" +
                "ctx.fillStyle = \"#000000\"\n"

        val radius = rings.size * 100
        rings.forEachIndexed { index, it ->
            js += "ctx.beginPath();\n" +
                    "ctx.arc(" + radius + "," + radius + "," + (index + 1) * 100 + ", 0, 2 * Math.PI);\n" +
                    "ctx.stroke();\n"
        }
        val categoriesSize = categories.size
        val ringsSize = rings.size
        for (i in 0 until categoriesSize) {
            val angle = 360 / categoriesSize * i
            val x = radius + radius * cos(-angle * PI / 180)
            val y = radius + radius * Math.sin(-angle * PI / 180)
            js += "ctx.beginPath();"
            js += "ctx.moveTo(" + radius + "," + radius + ");\n"
            js += "ctx.lineTo(" + x + "," + y + ");\n"
            js += "ctx.stroke();\n"
        }
        var X = mutableListOf<Double>()
        var Y = mutableListOf<Double>()
        techs.forEachIndexed { index, tech ->
            var point: List<Double>
            var test: Boolean

            do {
                point = this.generatePointforJS(tech, rings, categories, radius)
                test = false;
                for (i in 0 until X.size) {
                    var d = (X[i] - point[0]).pow(2.0) + (Y[i] - point[1]).pow(2.0)
                    d = sqrt(d)
                    if (d < 18) {
                        test = true;
                    }
                }
            } while (test)
            X.add(point[0])
            Y.add(point[1])
            //println(tech.name + " " + point[0] + ";" + point[1])
            categories.forEachIndexed { index, it ->
                if (tech.category == it) {
                    var r = index + 1 * 50
                    var g = index + 1 * 30
                    var b = index + 1 * 40
                    if (r > 255) {
                        r = 255
                    }
                    if (g > 255) {
                        g = 255
                    }
                    if (b > 255) {
                        b = 255
                    }
                    js += "\nctx.fillStyle = \"#FF45F0\";"
                }
            }

            when (tech.stability) {
                Stability.DOWN -> {
                    js += "\nctx.moveTo(" + point[0] + "," + point[1] + ");"
                    js += "\nctx.beginPath();"
                    js += "\nctx.lineTo(" + (point[0] + 12) + "," + (point[1] - 15) + ");"
                    js += "\nctx.lineTo(" + (point[0] - 12) + "," + (point[1] - 15) + ");"
                    js += "\nctx.lineTo(" + point[0] + "," + point[1] + ");"
                    js += "\nctx.fill();"
                    js += "\nctx.fillStyle = \"#000000\""
                    js += "\nctx.fillText(" + (index + 1) + "," + (point[0] - 2) + "," + (point[1] - 4) + ");"
                }
                Stability.UP -> {
                    js += "\nctx.moveTo(" + point[0] + "," + point[1] + ");"
                    js += "\nctx.beginPath();"
                    js += "\nctx.lineTo(" + (point[0] + 12) + "," + (point[1] + 15) + ");"
                    js += "\nctx.lineTo(" + (point[0] - 12) + "," + (point[1] + 15) + ");"
                    js += "\nctx.lineTo(" + point[0] + "," + point[1] + ");"
                    js += "\nctx.fill();"
                    js += "\nctx.fillStyle = \"#000000\""
                    js += "\nctx.fillText(" + (index + 1) + "," + (point[0] - 4) + "," + (point[1] + 15) + ");"
                }
                Stability.STABLE -> {
                    js += "\nctx.moveTo(" + point[0] + "," + point[1] + ");"
                    js += "\nctx.beginPath();"
                    js += "\nctx.arc(" + point[0] + "," + point[1] + ", 8, 0, 2 * Math.PI)"
                    js += "\nctx.fill();"
                    js += "\nctx.fillStyle = \"#000000\""
                    js += "\nctx.fillText(" + (index + 1) + "," + (point[0] - 4) + "," + (point[1] + 3) + ");"
                }
            }
        }
        js += "}</script>"
        return js
    }

    private fun generatePointforJS(
        technology: Technology,
        rings: Set<String>,
        categories: Set<String>,
        radius: Int
    ): MutableList<Double> {
        var angleLowLimit = 0;
        var angleUpLimit = 0;
        val distanceLowLimit = 10;
        val distanceUpLimit = 90;
        val angle = 360 / categories.size
        categories.forEachIndexed { index, i ->
            if (technology.category == i) {
                angleLowLimit = 0 + angle * index + 5
                angleUpLimit = angle + angle * index - 5
            }
        }
        var x: Double
        var y: Double
        val point = mutableListOf<Double>()
        val rAngle = Random.nextDouble() * (angleUpLimit - angleLowLimit) + angleLowLimit

        val generate = (distanceLowLimit..distanceUpLimit).random().toDouble()
        val distanceFromCenter = generate + rings.indexOf(technology.ring) * 100
        x = radius + distanceFromCenter * cos(-rAngle * PI / 180)
        y = radius + distanceFromCenter * sin(-rAngle * PI / 180)
        point.add(x)
        point.add(y)
        return point
    }
}