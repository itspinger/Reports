package raf.rs.reports.pdf

import com.lowagie.text.*
import com.lowagie.text.pdf.PdfPCell
import com.lowagie.text.pdf.PdfPTable
import com.lowagie.text.pdf.PdfWriter
import raf.rs.reports.IReport
import raf.rs.reports.ReportType
import java.io.FileOutputStream

class PdfReport : IReport {

    override val getReportType: ReportType = ReportType.PDF

    override fun generateReport(data: Map<String, List<String>>, destination: String, header: Boolean, title: String?, summary: String?) {
        // Create an empty document
        val document = Document()
        val columns = data.keys.toList()
        val numColumns = columns.size

        try {
            PdfWriter.getInstance(document, FileOutputStream(destination))

            // Make sure we can write to the document
            document.open()

            // Add title
            title?.let { it: String ->
                val titleParagraph = Paragraph(it, FontFactory.getFont(FontFactory.TIMES, 18f))
                titleParagraph.alignment = Element.ALIGN_CENTER
                document.add(titleParagraph)
            }

            document.add(Chunk.NEWLINE)

            // Create a table
            val table = PdfPTable(numColumns)

            // Add header row if necessary
            if (header) {
                columns.forEach { column ->
                    val cell = PdfPCell(Paragraph(column, FontFactory.getFont(FontFactory.HELVETICA, 12f)))
                    cell.horizontalAlignment = Element.ALIGN_CENTER
                    table.addCell(cell)
                }
            }

            // Add data rows
            val numRows = data.values.first().size
            for (i in 0 until numRows) {
                columns.forEach { column ->
                    val cellData = data[column]?.get(i) ?: ""
                    table.addCell(cellData)
                }
            }

            document.add(table)

            summary?.let { it: String ->
                document.add(Chunk.NEWLINE)
                val summaryParagraph = Paragraph("Summary: $it", FontFactory.getFont(FontFactory.HELVETICA, 12f))
                document.add(summaryParagraph)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            document.close()
        }
    }
}