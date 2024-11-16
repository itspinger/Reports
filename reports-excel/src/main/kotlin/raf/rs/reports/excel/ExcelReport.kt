package raf.rs.reports.excel

import org.apache.poi.ss.usermodel.*
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.ss.util.RegionUtil
import org.apache.poi.xssf.usermodel.XSSFColor
import org.apache.poi.xssf.usermodel.XSSFFont
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import raf.rs.reports.IReport
import raf.rs.reports.ReportType
import raf.rs.reports.model.ElementProperties
import raf.rs.reports.model.FormattingOptions
import java.io.FileOutputStream

class ExcelReport : IReport {

    override val reportType: ReportType = ReportType.EXCEL

    override fun generateReport(
        data: Map<String, List<String>>,
        destination: String,
        header: Boolean,
        title: String?,
        summary: Map<String, String>?,
        format: FormattingOptions
    ) {
        val workbook: Workbook = XSSFWorkbook()
        val sheet: Sheet = workbook.createSheet("Report")

        // Add title if provided
        title?.let { it: String ->
            val titleRow: Row = sheet.createRow(0)
            val titleCell: Cell = titleRow.createCell(0)
            titleCell.setCellValue(it)

            // Merge title cells into one big cell
            sheet.addMergedRegion(CellRangeAddress(0, 0, 0, data.keys.size - 1))

            // Create and set title style
            val titleStyle = workbook.createCellStyle().apply {
                alignment = HorizontalAlignment.CENTER
            }

            val titleFont: Font = workbook.createFont().apply {
                applyStyleToFont(format.titleFormat, this)
                fontHeightInPoints = 18
            }

            titleStyle.setFont(titleFont)
            titleCell.cellStyle = titleStyle
        }

        // Create header row if necessary
        if (header) {
            val headerRow: Row = sheet.createRow(1)
            data.keys.forEachIndexed { index, columnName ->
                val headerCell : Cell = headerRow.createCell(index)
                headerCell.setCellValue(columnName)

                val headerStyle = workbook.createCellStyle()
                val headerFont = workbook.createFont().apply { applyStyleToFont(format.headerFormat, this) }

                headerStyle.setFont(headerFont)
                headerCell.cellStyle = headerStyle
            }
        }

        // Add data rows
        val numRows = data.values.first().size
        for (i in 0 until numRows) {
            val dataRow: Row = sheet.createRow(if (header) i + 2 else i + 1) // Adjust for header

            data.keys.forEachIndexed { index, columnName ->
                val cell = dataRow.createCell(index)
                cell.setCellValue(data[columnName]?.get(i) ?: "")

                val cellStyle = workbook.createCellStyle()
                val cellFont = workbook.createFont().apply {
                    applyStyleToFont(format.rowFormat[i], this)
                    applyStyleToFont(format.columnFormat[columnName], this)
                }

                cellStyle.setFont(cellFont)
                cell.cellStyle = cellStyle
            }
        }

        // Add summary if provided
        summary?.let { it:  Map<String, String> ->
            val summaryRow: Row = sheet.createRow(numRows + 3) // Place summary after data
            val summaryCell: Cell = summaryRow.createCell(0)
            summaryCell.setCellValue("Summary: ")

            var startIndex = numRows + 4;
            for ((key, value) in it) {
                val keyRow: Row = sheet.createRow(startIndex++)
                val keyCell: Cell = keyRow.createCell(0)
                val valueCell: Cell = keyRow.createCell(1)

                keyCell.setCellValue(key)
                valueCell.setCellValue(value)

                val summaryStyle = workbook.createCellStyle()
                val summaryFont = workbook.createFont().apply { applyStyleToFont(format.summaryFormat[key], this) }

                summaryStyle.setFont(summaryFont)
                keyCell.cellStyle = summaryStyle
            }
        }

        // Line style
        val range = CellRangeAddress(1, numRows + (if (header) 1 else 0), 0, data.keys.size - 1)
        val borderStyle : BorderStyle = fromFormatToBorderStyle(format)
        RegionUtil.setBorderTop(borderStyle, range, sheet)
        RegionUtil.setBorderLeft(borderStyle, range, sheet)
        RegionUtil.setBorderRight(borderStyle, range, sheet)
        RegionUtil.setBorderBottom(borderStyle, range, sheet)

        // Write to the destination file
        FileOutputStream(this.reportType.applyExtension(destination)).use { outputStream ->
            workbook.write(outputStream)
        }

        // Close the workbook
        workbook.close()
    }

    private fun fromFormatToBorderStyle(format: FormattingOptions): BorderStyle {
        return when (format.borderStyle) {
            FormattingOptions.BorderStyle.THIN -> BorderStyle.THIN
            FormattingOptions.BorderStyle.MEDIUM -> BorderStyle.MEDIUM
            else -> BorderStyle.NONE
        }
    }

    private fun applyStyleToFont(properties: ElementProperties?, font: Font) {
        properties?.let { format ->
            font.bold = format.hasTextStyle(ElementProperties.TextStyle.BOLD)
            font.italic = format.hasTextStyle(ElementProperties.TextStyle.ITALIC)
            font.underline = if (format.hasTextStyle(ElementProperties.TextStyle.UNDERLINE)) Font.U_SINGLE else Font.U_NONE

            format.color?.let {
                if (font is XSSFFont) {
                    val rgb = hexToRgb(it)
                    font.setColor(XSSFColor(rgb))
                }
            }
        }
    }

    // Helper function to convert hex string to RGB bytes
    private fun hexToRgb(hex: String): ByteArray {
        val colorInt = hex.removePrefix("#").toInt(16)
        return byteArrayOf(
            ((colorInt shr 16) and 0xFF).toByte(),  // Red
            ((colorInt shr 8) and 0xFF).toByte(),   // Green
            (colorInt and 0xFF).toByte()            // Blue
        )
    }

}