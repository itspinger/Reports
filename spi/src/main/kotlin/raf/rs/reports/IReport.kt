package raf.rs.reports

import raf.rs.reports.calculations.ColumnCalculations
import raf.rs.reports.model.Summary
import java.sql.ResultSet
import java.sql.ResultSetMetaData

interface IReport {

    val getReportType: ReportType

    fun generateReport(data: Map<String, List<String>>, destination: String, header: Boolean, title: String?, summary: Map<String, String>? = null)

    fun generateReport(data: Map<String, List<String>>, destination: String, header: Boolean, title: String?, summary: Summary? = null) {
        summary?.processSummary(data)
        generateReport(data, destination, header, title, summary?.summary)
    }

    fun generateReport(data: Map<String, List<String>>, destination: String, header: Boolean, title: String? = null, summary: Summary? = null, calculations: List<ColumnCalculations>) {
        val toMap = data.toMutableMap();
        prepareData(toMap, calculations)
        generateReport(toMap, destination, header, title, summary)
    }

    fun generateReport(resultSet: ResultSet, destination: String, header: Boolean, title: String? = null, summary: Summary? = null) {
        val data = prepareData(resultSet)
        generateReport(data, destination, header, title, summary)
    }

    private fun prepareData(data: MutableMap<String, List<String>>, calculations: List<ColumnCalculations>) {
        calculations.forEach { data[it.columnName] = it.calculateValues(data) }
    }

    private fun prepareData(resultSet: ResultSet): MutableMap<String, List<String>> {
        val reportData = mutableMapOf<String, MutableList<String>>()

        val metaData: ResultSetMetaData = resultSet.metaData
        val columnCount = metaData.columnCount

        for (i in 1..columnCount) {
            val columnName = metaData.getColumnName(i)
            reportData[columnName] = mutableListOf()
        }

        while (resultSet.next()) {
            for (i in 1..columnCount) {
                val columnName = metaData.getColumnName(i)
                reportData[columnName]!!.add(resultSet.getString(i))
            }
        }

        return reportData.toMutableMap()
    }
}