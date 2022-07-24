package ioClasses


import com.itextpdf.text.Document
import com.itextpdf.text.Element
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.pdf.PdfWriter
import dataClasses.Radar
import dataClasses.Technology
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

class RadarExporter: DataExporter {
    override fun exportToHtml(radar: Radar): String {
        TODO("Not yet implemented")
    }

    override fun exportToPdf(radar: Radar, path: String): File {
        val formater = DateTimeFormatter.ofPattern("HH-mm-ss")
        val pdfFile = File("$path/Radar " + LocalDate.now() + " " + LocalTime.now().format(formater) +".pdf")
        val output = FileOutputStream(pdfFile)
        val doc = Document()
        val writer = PdfWriter.getInstance(doc, output)
        doc.open()

        val canvas = writer.directContent
        val font = BaseFont.createFont(BaseFont.TIMES_BOLD, "UTF-8", false)
        canvas.setFontAndSize(font, 20F)
        canvas.setRGBColorFill(0x0, 0x0, 0x0)

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
        canvas.showTextAligned(Element.ALIGN_CENTER,Ring.ADOPT.toString(),300F,515F,0F)
        canvas.showTextAligned(Element.ALIGN_CENTER,Ring.TRIAL.toString(),300F,565F,0F)
        canvas.showTextAligned(Element.ALIGN_CENTER,Ring.ASSESS.toString(),300F,615F,0F)
        canvas.showTextAligned(Element.ALIGN_CENTER,Ring.HOLD.toString(),300F,665F,0F)
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
            println("X size: " + X.size)
            println(tech.name + " x:" + point[0] + " y:" + point[1])
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
                    /*canvas.setLineWidth(0.7)
                    canvas.moveTo(point[0], point[1])
                    canvas.lineTo(point[0] + 4, point[1] + 4)
                    canvas.lineTo(point[0] - 4, point[1] + 4)
                    canvas.lineTo(point[0], point[1])
                    canvas.setRGBColorFill(0, 0, 0)
                    canvas.stroke()*/
                    canvas.setRGBColorFill(0, 0, 0)
                    canvas.beginText()
                    canvas.showTextAligned(Element.ALIGN_CENTER, (index+1).toString(), point[0].toFloat(), (point[1]+1.5).toFloat(),0F)
                    canvas.endText()

                }
                Stability.UP -> {
                    canvas.moveTo(point[0], point[1])
                    canvas.lineTo(point[0] + 4, point[1] - 4)
                    canvas.lineTo(point[0] - 4, point[1] - 4)
                    canvas.lineTo(point[0], point[1])
                    canvas.fill()
                    /*canvas.setLineWidth(0.7)
                    canvas.moveTo(point[0], point[1])
                    canvas.lineTo(point[0] + 4, point[1] - 4)
                    canvas.lineTo(point[0] - 4, point[1] - 4)
                    canvas.lineTo(point[0], point[1])
                    canvas.setRGBColorFill(0, 0, 0)
                    canvas.stroke()*/
                    canvas.setRGBColorFill(0, 0, 0)
                    canvas.beginText()
                    canvas.showTextAligned(Element.ALIGN_CENTER, (index+1).toString(), point[0].toFloat(), (point[1]-3.5).toFloat(),0F)
                    canvas.endText()
                }
                Stability.STABLE -> {
                    canvas.circle(point[0], point[1], 3.0)
                    canvas.fill()
                    /*canvas.circle(point[0], point[1], 3.0)
                    canvas.setLineWidth(0.7)
                    canvas.setRGBColorFill(0, 0, 0)
                    canvas.stroke()*/
                    canvas.setRGBColorFill(0, 0, 0)
                    canvas.beginText()
                    canvas.showTextAligned(Element.ALIGN_CENTER, (index+1).toString(), point[0].toFloat(), (point[1]-1.0).toFloat(),0F)
                    canvas.endText()
                }
            }
            canvas.moveTo(point[0], point[1])
        }
        var i =0
        var baseX = 20F
        var licznik = 1
        var lastRing = "Adopt"
        var rings = 0
        canvas.beginText()
        canvas.setFontAndSize(font, 12F)
        canvas.showTextAligned(Element.ALIGN_LEFT,"Datastore",baseX,275F,0F)
        canvas.setFontAndSize(font, 10F)
        canvas.showTextAligned(Element.ALIGN_LEFT,"Adopt",baseX,250F,0F)
        val font2 = BaseFont.createFont(BaseFont.TIMES_ROMAN, "UTF-8", false)
        canvas.setFontAndSize(font2, 8F)

        while(techs[i].category.equals(Category.DATASTORE))
        {
            if((250-(licznik+rings)*10)<=0)
            {
                baseX += 100F
                licznik = 0-rings+1
            }

            if(techs[i].ring.equals(Ring.TRIAL) && lastRing!="Trial")
            {
                lastRing ="Trial"
                canvas.setFontAndSize(font, 10F)
                canvas.showTextAligned(Element.ALIGN_LEFT,"Trial",baseX,(250-(licznik)*10).toFloat(),0F)
                canvas.setFontAndSize(font2, 8F)
                rings++
            }

            if(techs[i].ring.equals(Ring.ASSESS) && lastRing!="Assess")
            {
                lastRing ="Assess"
                canvas.setFontAndSize(font, 10F)
                canvas.showTextAligned(Element.ALIGN_LEFT,"Assess",baseX,(250-(licznik+rings)*10).toFloat(),0F)
                canvas.setFontAndSize(font2, 8F)
                rings++
            }

            if(techs[i].ring.equals(Ring.HOLD) && lastRing!="Hold")
            {
                lastRing ="Hold"
                canvas.setFontAndSize(font, 10F)
                canvas.showTextAligned(Element.ALIGN_LEFT,"Hold",baseX,(250-(licznik+rings)*10).toFloat(),0F)
                canvas.setFontAndSize(font2, 8F)
                rings++
            }
            canvas.showTextAligned(Element.ALIGN_LEFT,(i+1).toString()+" " + techs[i].name,baseX,(250-(licznik+rings)*10).toFloat(),0F)
            licznik++
            i++
        }

        baseX+=100F
        canvas.setFontAndSize(font, 12F)
        canvas.showTextAligned(Element.ALIGN_LEFT,"Infrastructure",baseX,275F,0F)
        canvas.setFontAndSize(font, 10F)
        canvas.showTextAligned(Element.ALIGN_LEFT,"Adopt",baseX,250F,0F)
        canvas.setFontAndSize(font2, 8F)
        licznik = 1
        lastRing="Adopt"
        rings = 0
        while(techs[i].category.equals(Category.INFRASTRUCTURE))
        {
            if((250-(licznik+rings)*10)<=0)
            {
                baseX += 100F
                licznik = 0-rings+1
            }

            if(techs[i].ring.equals(Ring.TRIAL) && lastRing!="Trial")
            {
                lastRing ="Trial"
                canvas.setFontAndSize(font, 10F)
                canvas.showTextAligned(Element.ALIGN_LEFT,"Trial",baseX,(250-(licznik)*10).toFloat(),0F)
                canvas.setFontAndSize(font2, 8F)
                rings++
            }

            if(techs[i].ring.equals(Ring.ASSESS) && lastRing!="Assess")
            {
                lastRing ="Assess"
                canvas.setFontAndSize(font, 10F)
                canvas.showTextAligned(Element.ALIGN_LEFT,"Assess",baseX,(250-(licznik+rings)*10).toFloat(),0F)
                canvas.setFontAndSize(font2, 8F)
                rings++
            }

            if(techs[i].ring.equals(Ring.HOLD) && lastRing!="Hold")
            {
                lastRing ="Hold"
                canvas.setFontAndSize(font, 10F)
                canvas.showTextAligned(Element.ALIGN_LEFT,"Hold",baseX,(250-(licznik+rings)*10).toFloat(),0F)
                canvas.setFontAndSize(font2, 8F)
                rings++
            }
            canvas.showTextAligned(Element.ALIGN_LEFT,(i+1).toString()+" " + techs[i].name,baseX,(250-(licznik+rings)*10).toFloat(),0F)
            licznik++
            i++
        }


        baseX+=100F
        canvas.setFontAndSize(font, 12F)
        canvas.showTextAligned(Element.ALIGN_LEFT,"Data Management",baseX,275F,0F)
        canvas.setFontAndSize(font, 10F)
        canvas.showTextAligned(Element.ALIGN_LEFT,"Adopt",baseX,250F,0F)
        canvas.setFontAndSize(font2, 8F)
        licznik = 1
        lastRing="Adopt"
        rings = 0
        while(techs[i].category.equals(Category.DATAMANAGEMENT))
        {
            if(250-(licznik+rings)*10<=0)
            {
                baseX += 100F
                licznik = 0-rings+1
            }

            if(techs[i].ring.equals(Ring.TRIAL) && lastRing!="Trial")
            {
                lastRing ="Trial"
                canvas.setFontAndSize(font, 10F)
                canvas.showTextAligned(Element.ALIGN_LEFT,"Trial",baseX,(250-(licznik)*10).toFloat(),0F)
                canvas.setFontAndSize(font2, 8F)
                rings++
            }

            if(techs[i].ring.equals(Ring.ASSESS) && lastRing!="Assess")
            {
                lastRing ="Assess"
                canvas.setFontAndSize(font, 10F)
                canvas.showTextAligned(Element.ALIGN_LEFT,"Assess",baseX,(250-(licznik+rings)*10).toFloat(),0F)
                canvas.setFontAndSize(font2, 8F)
                rings++
            }

            if(techs[i].ring.equals(Ring.HOLD) && lastRing!="Hold")
            {
                lastRing ="Hold"
                canvas.setFontAndSize(font, 10F)
                canvas.showTextAligned(Element.ALIGN_LEFT,"Hold",baseX,(250-(licznik+rings)*10).toFloat(),0F)
                canvas.setFontAndSize(font2, 8F)
                rings++
            }
            canvas.showTextAligned(Element.ALIGN_LEFT,(i+1).toString()+" " + techs[i].name,baseX,(250-(licznik+rings)*10).toFloat(),0F)
            licznik++
            i++
        }

        baseX+=100F
        canvas.setFontAndSize(font, 12F)
        canvas.showTextAligned(Element.ALIGN_LEFT,"Languages",baseX,275F,0F)
        canvas.setFontAndSize(font, 10F)
        canvas.showTextAligned(Element.ALIGN_LEFT,"Adopt",baseX,250F,0F)
        canvas.setFontAndSize(font2, 8F)
        licznik = 1
        lastRing="Adopt"
        rings = 0
        while(techs[i].category.equals(Category.LANGUAGES))
        {
            if((250-(licznik+rings)*10)<=0)
            {
                baseX += 100F
                licznik = 0-rings+1
            }

            if(techs[i].ring.equals(Ring.TRIAL) && lastRing!="Trial")
            {
                lastRing ="Trial"
                canvas.setFontAndSize(font, 10F)
                canvas.showTextAligned(Element.ALIGN_LEFT,"Trial",baseX,(250-(licznik)*10).toFloat(),0F)
                canvas.setFontAndSize(font2, 8F)
                rings++
            }

            if(techs[i].ring.equals(Ring.ASSESS) && lastRing!="Assess")
            {
                lastRing ="Assess"
                canvas.setFontAndSize(font, 10F)
                canvas.showTextAligned(Element.ALIGN_LEFT,"Assess",baseX,(250-(licznik+rings)*10).toFloat(),0F)
                canvas.setFontAndSize(font2, 8F)
                rings++
            }

            if(techs[i].ring.equals(Ring.HOLD) && lastRing!="Hold")
            {
                lastRing ="Hold"
                canvas.setFontAndSize(font, 10F)
                canvas.showTextAligned(Element.ALIGN_LEFT,"Hold",baseX,(250-(licznik+rings)*10).toFloat(),0F)
                canvas.setFontAndSize(font2, 8F)
                rings++
            }
            canvas.showTextAligned(Element.ALIGN_LEFT,(i+1).toString()+" " + techs[i].name,baseX,(250-(licznik+rings)*10).toFloat(),0F)
            licznik++
            i++
            if(i==techs.size)
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

}
