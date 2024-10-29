package raf.rs.reports

import raf.rs.reports.model.ColumnCalculations
import raf.rs.reports.model.SummaryCalculation
import java.sql.ResultSet
import java.sql.ResultSetMetaData

interface IReport {

    val getReportType: ReportType

    fun generateReport(data: Map<String, List<String>>, destination: String, header: Boolean, title:String? ,summary: Map<String,String>? = null)

    fun generateReport(data: Map<String, List<String>>, destination: String, header: Boolean, title: String? = null, calculations: List<ColumnCalculations>, summary: Map<String,String>? = null) {
        val toMap = data.toMutableMap();
        prepareData(toMap, calculations)
      //  generateReport(toMap, destination, header, title, summary)
    }
    fun generateReport(data: Map<String, List<String>>, destination: String, header: Boolean, title: String? = null, summary: MutableMap<String,Any>? = null) {
        if (summary != null) {
            summary.forEach { entry ->
                if(entry.value is SummaryCalculation){
                    summary[entry.key] = (entry.value as SummaryCalculation).calculateValues(data)

                }
            }
        }
        val newMap= mutableMapOf<String,String>()
        if (summary != null) {
            summary.mapValuesTo(newMap) { it.value.toString() }
        }
        generateReport(data, destination, header, title, newMap)
    }

    fun generateReport(resultSet: ResultSet, destination: String, header: Boolean, title: String? = null, summary: String? = null) {
        val data = prepareData(resultSet)
      //  generateReport(data, destination, header, title, summary)
    }

    private fun prepareData(data: MutableMap<String, List<String>>, calculations: List<ColumnCalculations>) {
        calculations.forEach {
            data[it.columnName] = it.calculateValues(data)
        }
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