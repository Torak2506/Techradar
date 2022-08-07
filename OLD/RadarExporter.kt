package OLD


import com.itextpdf.text.Document
import com.itextpdf.text.Element
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.pdf.PdfWriter
import enums.Category
import enums.Ring
import enums.Stability
import interfaces.DataExporter

import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

class RadarExporter(val radar: Radar): DataExporter<Radar> {

    override fun exportToHtml(): String {
        val techs = radar.technologies.sorted()
        var dsList = "<div style=\"display:inline-block;width:150px\">"
        var inList = "<div style=\"display:inline-block;width:150px\">"
        var dmList = "<div style=\"display:inline-block;width:150px\">"
        var lgList = "<div style=\"display:inline-block;width:150px\">"
        dsList += "<p><strong>Datastorage</strong></p>"
        dsList += "<p><strong>Adopt</strong></p>"
        inList += "<p><strong>Infrastructure</strong></p>"
        inList += "<p><strong>Adopt</strong></p>"
        dmList += "<p><strong>Data Management</strong></p>"
        dmList += "<p><strong>Adopt</strong></p>"
        lgList += "<p><strong>Languages</strong></p>"
        lgList += "<p><strong>Adopt</strong></p>"
        var dslR = Ring.ADOPT
        var dmlR = Ring.ADOPT
        var inlR = Ring.ADOPT
        var lglR = Ring.ADOPT
        techs.forEachIndexed{index, t ->
            if(t.category == Category.DATASTORE)
            {
                if(t.ring!=dslR)
                {
                    val new = when(t.ring)
                    {
                        Ring.ASSESS -> "Assess"
                        Ring.HOLD -> "Hold"
                        Ring.TRIAL -> "Trial"
                        Ring.ADOPT -> "Adopt"
                    }
                    dsList += "<p><strong>" + new + "</strong></p>"
                    dslR = t.ring
                }
                dsList+= "<p>" + (index+1).toString()+" "+t.name+"</p>"
            }
            if(t.category == Category.DATAMANAGEMENT)
            {
                if(t.ring!=dmlR)
                {
                    val new = when(t.ring)
                    {
                        Ring.ASSESS -> "Assess"
                        Ring.HOLD -> "Hold"
                        Ring.TRIAL -> "Trial"
                        Ring.ADOPT -> "Adopt"
                    }
                    dmList += "<p><strong>" + new + "</strong></p>"
                    dmlR = t.ring
                }
                dmList+= "<p>" + (index+1).toString()+" "+t.name+"</p>"
            }
            if(t.category == Category.INFRASTRUCTURE)
            {
                if(t.ring!=inlR)
                {
                    val new = when(t.ring)
                    {
                        Ring.ASSESS -> "Assess"
                        Ring.HOLD -> "Hold"
                        Ring.TRIAL -> "Trial"
                        Ring.ADOPT -> "Adopt"
                    }
                    inList += "<p><strong>" + new + "</strong></p>"
                    inlR = t.ring
                }
                inList+="<p>" + (index+1).toString()+" "+t.name+"</p>"
            }
            if(t.category == Category.LANGUAGES)
            {
                if(t.ring!=lglR)
                {
                    val new = when(t.ring)
                    {
                        Ring.ASSESS -> "Assess"
                        Ring.HOLD -> "Hold"
                        Ring.TRIAL -> "Trial"
                        Ring.ADOPT -> "Adopt"
                    }
                    lgList += "<p><strong>" + new + "</strong></p>"
                    lglR = t.ring
                }
                lgList+= "<p>" + (index+1).toString()+" "+t.name+"</p>"
            }
        }
        dsList += "</div>"
        inList += "</div>"
        dmList += "</div>"
        lgList += "</div>"

        return "<!DOCTYPE HTML>\n" +
                "<html>\n" +
                "<head>\n" +
                "<meta charset=\"utf-8\">\n" +
                writeJS(techs) +
                "<title>Techradar</title>\n" +
                "</head>\n" +
                "<body onload=\"draw()\">\n" +
                "<h1>${radar.something}</h1>\n" +
                "<canvas id=\"radar\", width=800, height=800 style=\"display:block\">\n" +
                "</canvas>\n" +
                dsList+inList+dmList+lgList+
                "</body>\n" +
                "</html>"
    }

    val DEFAULT_PATH = "D:/"
    override fun exportToPdf(dirPath: String?): File {
        val path = dirPath ?: DEFAULT_PATH
        val formater = DateTimeFormatter.ofPattern("HH-mm-ss")
        val pdfFile = File("$path/Radar " + LocalDate.now() + " " + LocalTime.now().format(formater) + ".pdf")
        val output = FileOutputStream(pdfFile)
        val doc = Document()
        val writer = PdfWriter.getInstance(doc, output)
        doc.open()

        val canvas = writer.directContent
        val font = BaseFont.createFont(BaseFont.TIMES_BOLD, "UTF-8", false)
        canvas.setFontAndSize(font, 20F)
        canvas.setRGBColorFill(0x0, 0x0, 0x0)
        canvas.beginText()
        canvas.showTextAligned(Element.ALIGN_CENTER, radar.something, 300F, 800F, 0F)
        canvas.endText()
        canvas.circle(300.0, 500.0, 50.0)
        canvas.circle(300.0, 500.0, 100.0)
        canvas.circle(300.0, 500.0, 150.0)
        canvas.circle(300.0, 500.0, 200.0)

        canvas.moveTo(300.0, 300.0)
        canvas.lineTo(300.0, 700.0)
        canvas.moveTo(100.0, 500.0)
        canvas.lineTo(500.0, 500.0)
        canvas.stroke()
        canvas.setRGBColorFill(125, 125, 125)
        canvas.beginText()
        canvas.showTextAligned(Element.ALIGN_CENTER, Ring.ADOPT.toString(), 300F, 515F, 0F)
        canvas.showTextAligned(Element.ALIGN_CENTER, Ring.TRIAL.toString(), 300F, 565F, 0F)
        canvas.showTextAligned(Element.ALIGN_CENTER, Ring.ASSESS.toString(), 300F, 615F, 0F)
        canvas.showTextAligned(Element.ALIGN_CENTER, Ring.HOLD.toString(), 300F, 665F, 0F)
        canvas.endText()
        canvas.setFontAndSize(font, 3.5F)
        canvas.setRGBColorFill(0x0, 0x0, 0x0)
        val techs = radar.technologies.sorted()

        var X = mutableListOf<Double>()
        var Y = mutableListOf<Double>()
        techs.forEachIndexed { index, tech ->
            var point: List<Double>
            var test: Boolean
            do {
                point = this.generatePoint(tech)
                test = false;
                for (i in 0 until X.size) {
                    var d = (X[i] - point[0]).pow(2.0) + (Y[i] - point[1]).pow(2.0)
                    d = sqrt(d)
                    if (d < 8) {
                        test = true;
                    }
                }
            } while (test)
            X.add(point[0])
            Y.add(point[1])
            when (tech.category) {
                Category.DATASTORE -> {
                    canvas.setRGBColorFill(0x0, 0x0, 255)
                }
                Category.LANGUAGES -> {
                    canvas.setRGBColorFill(255, 0x0, 0x0)
                }
                Category.DATAMANAGEMENT -> {
                    canvas.setRGBColorFill(0x0, 255, 0x0)
                }
                Category.INFRASTRUCTURE -> {
                    canvas.setRGBColorFill(50, 100, 120)
                }
            }

            when (tech.stability) {
                Stability.DOWN -> {
                    canvas.moveTo(point[0], point[1])
                    canvas.lineTo(point[0] + 4, point[1] + 4)
                    canvas.lineTo(point[0] - 4, point[1] + 4)
                    canvas.lineTo(point[0], point[1])
                    canvas.fill()
                    canvas.setRGBColorFill(0, 0, 0)
                    canvas.beginText()
                    canvas.showTextAligned(
                        Element.ALIGN_CENTER,
                        (index + 1).toString(),
                        point[0].toFloat(),
                        (point[1] + 1.5).toFloat(),
                        0F
                    )
                    canvas.endText()

                }
                Stability.UP -> {
                    canvas.moveTo(point[0], point[1])
                    canvas.lineTo(point[0] + 4, point[1] - 4)
                    canvas.lineTo(point[0] - 4, point[1] - 4)
                    canvas.lineTo(point[0], point[1])
                    canvas.fill()
                    canvas.setRGBColorFill(0, 0, 0)
                    canvas.beginText()
                    canvas.showTextAligned(
                        Element.ALIGN_CENTER,
                        (index + 1).toString(),
                        point[0].toFloat(),
                        (point[1] - 3.5).toFloat(),
                        0F
                    )
                    canvas.endText()
                }
                Stability.STABLE -> {
                    canvas.circle(point[0], point[1], 3.0)
                    canvas.fill()
                    canvas.setRGBColorFill(0, 0, 0)
                    canvas.beginText()
                    canvas.showTextAligned(
                        Element.ALIGN_CENTER,
                        (index + 1).toString(),
                        point[0].toFloat(),
                        (point[1] - 1.0).toFloat(),
                        0F
                    )
                    canvas.endText()
                }
            }
            canvas.moveTo(point[0], point[1])
        }
        var i = 0
        var baseX = 20F
        var licznik = 1
        var lastRing = "Adopt"
        var rings = 0
        canvas.beginText()
        canvas.setFontAndSize(font, 12F)
        canvas.showTextAligned(Element.ALIGN_LEFT, "Datastore", baseX, 275F, 0F)
        canvas.setFontAndSize(font, 10F)
        canvas.showTextAligned(Element.ALIGN_LEFT, "Adopt", baseX, 250F, 0F)
        val font2 = BaseFont.createFont(BaseFont.TIMES_ROMAN, "UTF-8", false)
        canvas.setFontAndSize(font2, 8F)

        while (techs[i].category.equals(Category.DATASTORE)) {
            if ((250 - (licznik + rings) * 10) <= 0) {
                baseX += 100F
                licznik = 0 - rings + 1
            }

            if (techs[i].ring.equals(Ring.TRIAL) && lastRing != "Trial") {
                lastRing = "Trial"
                canvas.setFontAndSize(font, 10F)
                canvas.showTextAligned(Element.ALIGN_LEFT, "Trial", baseX, (250 - (licznik) * 10).toFloat(), 0F)
                canvas.setFontAndSize(font2, 8F)
                rings++
            }

            if (techs[i].ring.equals(Ring.ASSESS) && lastRing != "Assess") {
                lastRing = "Assess"
                canvas.setFontAndSize(font, 10F)
                canvas.showTextAligned(
                    Element.ALIGN_LEFT,
                    "Assess",
                    baseX,
                    (250 - (licznik + rings) * 10).toFloat(),
                    0F
                )
                canvas.setFontAndSize(font2, 8F)
                rings++
            }

            if (techs[i].ring.equals(Ring.HOLD) && lastRing != "Hold") {
                lastRing = "Hold"
                canvas.setFontAndSize(font, 10F)
                canvas.showTextAligned(Element.ALIGN_LEFT, "Hold", baseX, (250 - (licznik + rings) * 10).toFloat(), 0F)
                canvas.setFontAndSize(font2, 8F)
                rings++
            }
            canvas.showTextAligned(
                Element.ALIGN_LEFT,
                (i + 1).toString() + " " + techs[i].name,
                baseX,
                (250 - (licznik + rings) * 10).toFloat(),
                0F
            )
            licznik++
            i++
        }

        baseX += 100F
        canvas.setFontAndSize(font, 12F)
        canvas.showTextAligned(Element.ALIGN_LEFT, "Infrastructure", baseX, 275F, 0F)
        canvas.setFontAndSize(font, 10F)
        canvas.showTextAligned(Element.ALIGN_LEFT, "Adopt", baseX, 250F, 0F)
        canvas.setFontAndSize(font2, 8F)
        licznik = 1
        lastRing = "Adopt"
        rings = 0
        while (techs[i].category.equals(Category.INFRASTRUCTURE)) {
            if ((250 - (licznik + rings) * 10) <= 0) {
                baseX += 100F
                licznik = 0 - rings + 1
            }

            if (techs[i].ring.equals(Ring.TRIAL) && lastRing != "Trial") {
                lastRing = "Trial"
                canvas.setFontAndSize(font, 10F)
                canvas.showTextAligned(Element.ALIGN_LEFT, "Trial", baseX, (250 - (licznik) * 10).toFloat(), 0F)
                canvas.setFontAndSize(font2, 8F)
                rings++
            }

            if (techs[i].ring.equals(Ring.ASSESS) && lastRing != "Assess") {
                lastRing = "Assess"
                canvas.setFontAndSize(font, 10F)
                canvas.showTextAligned(
                    Element.ALIGN_LEFT,
                    "Assess",
                    baseX,
                    (250 - (licznik + rings) * 10).toFloat(),
                    0F
                )
                canvas.setFontAndSize(font2, 8F)
                rings++
            }

            if (techs[i].ring.equals(Ring.HOLD) && lastRing != "Hold") {
                lastRing = "Hold"
                canvas.setFontAndSize(font, 10F)
                canvas.showTextAligned(Element.ALIGN_LEFT, "Hold", baseX, (250 - (licznik + rings) * 10).toFloat(), 0F)
                canvas.setFontAndSize(font2, 8F)
                rings++
            }
            canvas.showTextAligned(
                Element.ALIGN_LEFT,
                (i + 1).toString() + " " + techs[i].name,
                baseX,
                (250 - (licznik + rings) * 10).toFloat(),
                0F
            )
            licznik++
            i++
        }


        baseX += 100F
        canvas.setFontAndSize(font, 12F)
        canvas.showTextAligned(Element.ALIGN_LEFT, "Data Management", baseX, 275F, 0F)
        canvas.setFontAndSize(font, 10F)
        canvas.showTextAligned(Element.ALIGN_LEFT, "Adopt", baseX, 250F, 0F)
        canvas.setFontAndSize(font2, 8F)
        licznik = 1
        lastRing = "Adopt"
        rings = 0
        while (techs[i].category.equals(Category.DATAMANAGEMENT)) {
            if (250 - (licznik + rings) * 10 <= 0) {
                baseX += 100F
                licznik = 0 - rings + 1
            }

            if (techs[i].ring.equals(Ring.TRIAL) && lastRing != "Trial") {
                lastRing = "Trial"
                canvas.setFontAndSize(font, 10F)
                canvas.showTextAligned(Element.ALIGN_LEFT, "Trial", baseX, (250 - (licznik) * 10).toFloat(), 0F)
                canvas.setFontAndSize(font2, 8F)
                rings++
            }

            if (techs[i].ring.equals(Ring.ASSESS) && lastRing != "Assess") {
                lastRing = "Assess"
                canvas.setFontAndSize(font, 10F)
                canvas.showTextAligned(
                    Element.ALIGN_LEFT,
                    "Assess",
                    baseX,
                    (250 - (licznik + rings) * 10).toFloat(),
                    0F
                )
                canvas.setFontAndSize(font2, 8F)
                rings++
            }

            if (techs[i].ring.equals(Ring.HOLD) && lastRing != "Hold") {
                lastRing = "Hold"
                canvas.setFontAndSize(font, 10F)
                canvas.showTextAligned(Element.ALIGN_LEFT, "Hold", baseX, (250 - (licznik + rings) * 10).toFloat(), 0F)
                canvas.setFontAndSize(font2, 8F)
                rings++
            }
            canvas.showTextAligned(
                Element.ALIGN_LEFT,
                (i + 1).toString() + " " + techs[i].name,
                baseX,
                (250 - (licznik + rings) * 10).toFloat(),
                0F
            )
            licznik++
            i++
        }

        baseX += 100F
        canvas.setFontAndSize(font, 12F)
        canvas.showTextAligned(Element.ALIGN_LEFT, "Languages", baseX, 275F, 0F)
        canvas.setFontAndSize(font, 10F)
        canvas.showTextAligned(Element.ALIGN_LEFT, "Adopt", baseX, 250F, 0F)
        canvas.setFontAndSize(font2, 8F)
        licznik = 1
        lastRing = "Adopt"
        rings = 0
        while (techs[i].category.equals(Category.LANGUAGES)) {
            if ((250 - (licznik + rings) * 10) <= 0) {
                baseX += 100F
                licznik = 0 - rings + 1
            }

            if (techs[i].ring.equals(Ring.TRIAL) && lastRing != "Trial") {
                lastRing = "Trial"
                canvas.setFontAndSize(font, 10F)
                canvas.showTextAligned(Element.ALIGN_LEFT, "Trial", baseX, (250 - (licznik) * 10).toFloat(), 0F)
                canvas.setFontAndSize(font2, 8F)
                rings++
            }

            if (techs[i].ring.equals(Ring.ASSESS) && lastRing != "Assess") {
                lastRing = "Assess"
                canvas.setFontAndSize(font, 10F)
                canvas.showTextAligned(
                    Element.ALIGN_LEFT,
                    "Assess",
                    baseX,
                    (250 - (licznik + rings) * 10).toFloat(),
                    0F
                )
                canvas.setFontAndSize(font2, 8F)
                rings++
            }

            if (techs[i].ring.equals(Ring.HOLD) && lastRing != "Hold") {
                lastRing = "Hold"
                canvas.setFontAndSize(font, 10F)
                canvas.showTextAligned(Element.ALIGN_LEFT, "Hold", baseX, (250 - (licznik + rings) * 10).toFloat(), 0F)
                canvas.setFontAndSize(font2, 8F)
                rings++
            }
            canvas.showTextAligned(
                Element.ALIGN_LEFT,
                (i + 1).toString() + " " + techs[i].name,
                baseX,
                (250 - (licznik + rings) * 10).toFloat(),
                0F
            )
            licznik++
            i++
            if (i == techs.size)
                break
        }



        canvas.endText()
        doc.close()
        return pdfFile
    }

    private fun generatePoint(technology: Technology): MutableList<Double> {
        val xULimit: Double
        val xLLimit: Double
        val yULimit: Double
        val yLLimit: Double
        val dULimit: Double
        val dLLimit: Double
        when (technology.category) {
            Category.DATASTORE -> {
                xULimit = 295.0
                xLLimit = 105.0
                yULimit = 695.0
                yLLimit = 505.0
            }
            Category.INFRASTRUCTURE -> {
                xULimit = 495.0
                xLLimit = 305.0
                yULimit = 695.0
                yLLimit = 505.0
            }
            Category.DATAMANAGEMENT -> {
                xULimit = 295.0
                xLLimit = 105.0
                yULimit = 495.0
                yLLimit = 305.0
            }
            Category.LANGUAGES -> {
                xULimit = 495.0
                xLLimit = 305.0
                yULimit = 495.0
                yLLimit = 305.0
            }
        }
        when (technology.ring) {
            Ring.ADOPT -> {
                dULimit = 45.0
                dLLimit = 0.0
            }
            Ring.ASSESS -> {
                dULimit = 95.0
                dLLimit = 55.0
            }
            Ring.TRIAL -> {
                dULimit = 145.0
                dLLimit = 105.0
            }
            Ring.HOLD -> {
                dULimit = 195.0
                dLLimit = 155.0
            }
        }

        var x: Double
        var y: Double
        var d: Double
        do {
            x = Random.nextDouble() * (xULimit - xLLimit) + xLLimit
            y = Random.nextDouble() * (yULimit - yLLimit) + yLLimit
            d = (x - 300.0).pow(2.0) + (y - 500.0).pow(2.0)
            d = sqrt(d)
        } while (d < dLLimit || d > dULimit)
        val point = mutableListOf<Double>()
        point.add(x)
        point.add(y)
        return point
    }

    private fun writeJS(techs: List<Technology>): String {
        var js = "<script>\nfunction draw(){" +
                "\nvar c = document.getElementById(\"radar\");\n" +
                "var ctx = c.getContext(\"2d\");\n" +
                "ctx.fillStyle = \"#000000\"\n" +
                "ctx.beginPath();\n" +
                "ctx.arc(400, 400, 100, 0, 2 * Math.PI);\n" +
                "ctx.stroke();\n" +
                "ctx.beginPath();\n" +
                "ctx.arc(400, 400, 200, 0, 2 * Math.PI);\n" +
                "ctx.stroke();\n" +
                "ctx.beginPath();\n" +
                "ctx.arc(400, 400, 300, 0, 2 * Math.PI);\n" +
                "ctx.stroke();\n" +
                "ctx.beginPath();\n" +
                "ctx.arc(400, 400, 400, 0, 2 * Math.PI);\n" +
                "ctx.stroke();\n" +
                "ctx.beginPath();\n" +
                "ctx.moveTo(400,0);\n" +
                "ctx.lineTo(400,800);\n" +
                "ctx.stroke();\n" +
                "ctx.beginPath();\n" +
                "ctx.moveTo(0,400);\n" +
                "ctx.lineTo(800,400);\n" +
                "ctx.stroke();\n" +
                "var ctx = c.getContext(\"2d\");\n" +
                "ctx.font = \"11px Times\";"
        var X = mutableListOf<Double>()
        var Y = mutableListOf<Double>()
        techs.forEachIndexed { index, tech ->
            var point: List<Double>
            var test: Boolean
            do {
                point = this.generatePointforJS(tech)
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
            when (tech.category) {
                Category.DATASTORE -> {
                    js += "\nctx.fillStyle = \"#0000FF\""
                }
                Category.LANGUAGES -> {
                    js += "\nctx.fillStyle = \"#FF0000\""
                }
                Category.DATAMANAGEMENT -> {
                    js += "\nctx.fillStyle = \"#00FF00\""
                }
                Category.INFRASTRUCTURE -> {
                    js += "\nctx.fillStyle = \"#326478\""
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
                    js += "\nctx.fillText(" + (index + 1) + "," + (point[0]-2) + "," + (point[1]-4) + ");"
                }
                Stability.UP -> {

                    js += "\nctx.moveTo(" + point[0] + "," + point[1] + ");"
                    js += "\nctx.beginPath();"
                    js += "\nctx.lineTo(" + (point[0] + 12) + "," + (point[1] + 15) + ");"
                    js += "\nctx.lineTo(" + (point[0] - 12) + "," + (point[1] + 15) + ");"
                    js += "\nctx.lineTo(" + point[0] + "," + point[1] + ");"
                    js += "\nctx.fill();"
                    js += "\nctx.fillStyle = \"#000000\""
                    js += "\nctx.fillText(" + (index + 1) + "," + (point[0]-4) + "," + (point[1]+15)+");"
                }
                Stability.STABLE -> {
                    js += "\nctx.moveTo("+point[0]+","+point[1]+");"
                    js += "\nctx.beginPath();"
                    js += "\nctx.arc(" + point[0] + "," + point[1] + ", 8, 0, 2 * Math.PI)"
                    js += "\nctx.fill();"
                    js += "\nctx.fillStyle = \"#000000\""
                    js += "\nctx.fillText(" + (index + 1) + "," + (point[0]-4) + "," + (point[1]+3) + ");"
                }
            }
        }
        js += "}</script>"
        return js
    }

    private fun generatePointforJS(technology: Technology): MutableList<Double> {
        val xULimit: Double
        val xLLimit: Double
        val yULimit: Double
        val yLLimit: Double
        val dULimit: Double
        val dLLimit: Double

        when (technology.category) {
            Category.DATASTORE -> {
                xULimit = 380.0
                xLLimit = 20.0
                yULimit = 380.0
                yLLimit = 20.0
            }
            Category.INFRASTRUCTURE -> {
                xULimit = 780.0
                xLLimit = 420.0
                yULimit = 380.0
                yLLimit = 20.0
            }
            Category.DATAMANAGEMENT -> {
                xULimit = 380.0
                xLLimit = 20.0
                yULimit = 780.0
                yLLimit = 420.0
            }
            Category.LANGUAGES -> {
                xULimit = 780.0
                xLLimit = 420.0
                yULimit = 780.0
                yLLimit = 420.0
            }
        }
        when (technology.ring) {
            Ring.ADOPT -> {
                dULimit = 90.0
                dLLimit = 10.0
            }
            Ring.ASSESS -> {
                dULimit = 190.0
                dLLimit = 110.0
            }
            Ring.TRIAL -> {
                dULimit = 290.0
                dLLimit = 210.0
            }
            Ring.HOLD -> {
                dULimit = 390.0
                dLLimit = 310.0
            }
        }

        var x: Double
        var y: Double
        var d: Double
        do {
            x = Random.nextDouble() * (xULimit - xLLimit) + xLLimit
            y = Random.nextDouble() * (yULimit - yLLimit) + yLLimit
            d = (x - 400).pow(2.0) + (y - 400).pow(2.0)
            d = sqrt(d)
        } while (d < dLLimit || d > dULimit)
        val point = mutableListOf<Double>()
        point.add(x)
        point.add(y)

        return point
    }

}
