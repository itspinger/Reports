package raf.rs.reports.csv

import raf.rs.reports.IReport
import raf.rs.reports.ReportType
import raf.rs.reports.model.FormattingOptions
import java.io.File
import java.text.Format

class CsvReport : IReport {

    override val getReportType: ReportType = ReportType.CSV

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

        File(destination).printWriter().use { writer ->
            if (header) {
                writer.println(columns.joinToString(separator = ","))
            }

            for (i in 0 until numRows) {
                val row = columns.map { column -> data[column]?.get(i) ?: "" }
                writer.println(row.joinToString(separator = ","))
            }
        }
    }
}