package raf.rs.reports.pdf

import com.lowagie.text.*
import com.lowagie.text.pdf.PdfPCell
import com.lowagie.text.pdf.PdfPTable
import com.lowagie.text.pdf.PdfWriter
import raf.rs.reports.IReport
import raf.rs.reports.ReportType
import raf.rs.reports.model.ElementProperties
import raf.rs.reports.model.FormattingOptions
import java.awt.Color
import java.io.FileOutputStream

class PdfReport : IReport {

    override val getReportType: ReportType = ReportType.PDF

    override fun generateReport(
        data: Map<String, List<String>>,
        destination: String,
        header: Boolean,
        title: String?,
        summary: Map<String, String>?,
        format: FormattingOptions
    ) {
        // Create an empty document
        val document = Document()
        val columns = data.keys.toList()
        val numColumns = columns.size

        try {
            PdfWriter.getInstance(document, FileOutputStream(this.getReportType.applyExtension(destination)))

            // Make sure we can write to the document
            document.open()

            // Add title
            title?.let {
                val titleParagraph = Paragraph(it, FontFactory.getFont(FontFactory.TIMES, 18f))
                applyStyleToFont(format.titleFormat, titleParagraph.font)
                titleParagraph.alignment = Element.ALIGN_CENTER
                document.add(titleParagraph)
            }

            document.add(Chunk.NEWLINE)

            // Create a table
            val table = PdfPTable(numColumns)

            // Add header row if necessary
            if (header) {
                columns.forEach { column ->
                    val cellParagraph = Paragraph(column, FontFactory.getFont(FontFactory.HELVETICA, 12f))
                    applyStyleToFont(format.headerFormat, cellParagraph.font)

                    val cell = PdfPCell(cellParagraph)
                    applyBorderStyle(cell, format)
                    cell.horizontalAlignment = Element.ALIGN_CENTER
                    table.addCell(cell)
                }
            }

            // Add data rows
            val numRows = data.values.first().size
            for (i in 0 until numRows) {
                columns.forEach { column ->
                    val cellData = data[column]?.get(i) ?: ""
                    val cellParagraph = Paragraph(cellData, FontFactory.getFont(FontFactory.HELVETICA, 12f))
                    applyStyleToFont(format.rowFormat[i], cellParagraph.font)
                    applyStyleToFont(format.columnFormat[column], cellParagraph.font)

                    val cell = PdfPCell(cellParagraph)
                    applyBorderStyle(cell, format)
                    table.addCell(cell)
                }
            }

            // Add the table to the document
            document.add(table)

            summary?.let { sum ->
                document.add(Chunk.NEWLINE)

                val summaryParagraph = Paragraph("Summary:", FontFactory.getFont(FontFactory.HELVETICA, 12f))
                document.add(summaryParagraph)

                for ((key, value) in sum) {
                    val cellParagraph = Paragraph("$key: $value", FontFactory.getFont(FontFactory.HELVETICA, 12f))
                    applyStyleToFont(format.summaryFormat[key], cellParagraph.font)
                    document.add(cellParagraph)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            document.close()
        }
    }

    private fun applyBorderStyle(cell: PdfPCell, format: FormattingOptions) {
        cell.borderWidth = this.fromFormatToBorderStyle(format)
        cell.border = PdfPCell.TOP or PdfPCell.BOTTOM or PdfPCell.LEFT or PdfPCell.RIGHT
    }

    private fun fromFormatToBorderStyle(format: FormattingOptions) : Float {
        return when (format.borderStyle) {
            FormattingOptions.BorderStyle.THIN -> 0.7F
            FormattingOptions.BorderStyle.MEDIUM -> 2.0F
            else -> 1.0F
        }
    }

    private fun applyStyleToFont(properties: ElementProperties?, font: Font) {
        properties?.let { format ->
            if (format.hasTextStyle(ElementProperties.TextStyle.BOLD))
                font.style = Font.BOLD

            if (format.hasTextStyle(ElementProperties.TextStyle.ITALIC))
                font.style = Font.ITALIC

            if (format.hasTextStyle(ElementProperties.TextStyle.UNDERLINE))
                font.style = Font.UNDERLINE

            format.color?.let {
                font.color = Color.decode(it)
            }
        }
    }
}