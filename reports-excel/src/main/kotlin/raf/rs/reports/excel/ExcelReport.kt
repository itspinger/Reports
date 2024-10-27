package raf.rs.reports.excel

import org.apache.poi.ss.usermodel.*
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import raf.rs.reports.IReport
import raf.rs.reports.ReportType
import java.io.FileOutputStream

class ExcelReport : IReport {

    override val getReportType: ReportType = ReportType.EXCEL

    override fun generateReport(data: Map<String, List<String>>, destination: String, header: Boolean, title: String?, summary: String?) {
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
                bold = true
                fontHeightInPoints = 18
            }

            titleStyle.setFont(titleFont)
            titleCell.cellStyle = titleStyle
        }

        // Create header row if necessary
        if (header) {
            val headerRow: Row = sheet.createRow(1)
            data.keys.forEachIndexed { index, columnName ->
                headerRow.createCell(index).setCellValue(columnName)
            }
        }

        // Add data rows
        val numRows = data.values.first().size
        for (i in 0 until numRows) {
            val dataRow: Row = sheet.createRow(if (header) i + 2 else i + 1) // Adjust for header
            data.keys.forEachIndexed { index, columnName ->
                dataRow.createCell(index).setCellValue(data[columnName]?.get(i) ?: "")
            }
        }

        // Add summary if provided
        summary?.let { it: String ->
            val summaryRow: Row = sheet.createRow(numRows + 2) // Place summary after data
            val summaryCell: Cell = summaryRow.createCell(0)
            summaryCell.setCellValue("Summary: $it")
        }

        // Write to the destination file
        FileOutputStream(destination).use { outputStream ->
            workbook.write(outputStream)
        }

        // Close the workbook
        workbook.close()
    }

}