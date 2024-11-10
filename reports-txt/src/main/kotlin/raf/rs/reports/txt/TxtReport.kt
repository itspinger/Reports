package raf.rs.reports.txt

import raf.rs.reports.IReport
import raf.rs.reports.ReportType
import raf.rs.reports.model.FormattingOptions
import java.io.File

class TxtReport : IReport {

    override val getReportType: ReportType = ReportType.TXT

    override fun generateReport(
        data: Map<String, List<String>>,
        destination: String,
        header: Boolean,
        title: String?,
        summary: Map<String, String>?,
        format: FormattingOptions
    ) {
        val columns = data.keys.toList()
        val numRows = data.values.first().size

        val columnWidths = columns.map { column ->
            val maxDataWidth = data[column]?.maxOfOrNull { it.length } ?: 0
            maxOf(column.length, maxDataWidth)
        }

        File(this.getReportType.applyExtension(destination)).printWriter().use { writer ->
            // Write title if provided
            title?.let {
                writer.println(it)
                writer.println()
            }

            // Write the header row
            if (header) {
                columns.forEachIndexed { index, column ->
                    writer.print(column.padEnd(columnWidths[index] + 2))
                }
            }

            writer.println()

            // Write the dashes
            columnWidths.forEach { width ->
                writer.print("-".repeat(width + 2))
            }

            writer.println()

            for (i in 0 until numRows) {
                columns.forEachIndexed { index, column ->
                    val value = data[column]?.get(i) ?: ""
                    writer.print(value.padEnd(columnWidths[index] + 2))
                }

                writer.println()
            }

            summary?.let {
                writer.println()
                writer.println("Summary:")
                it.forEach { entry -> writer.println("${entry.key} : ${entry.value}") }
            }
        }
    }

}