package raf.rs.reports

import raf.rs.reports.calculations.ColumnCalculations
import raf.rs.reports.model.FormattingOptions
import raf.rs.reports.model.Summary
import java.sql.ResultSet
import java.sql.ResultSetMetaData

interface IReport {

    val getReportType: ReportType
    /**
     * Generates a report based on the provided data and save it on specified path .
     *
     * @param data Data to be used for generating the report.
     * @param destination Destination where the report will be saved.
     * @param header Flag indicating whether the report should contain a header.
     * @param title Title of the report.
     * @param summary Summary of the report.
     * @param format Formatting options for the report.
     */
    fun generateReport(data: Map<String, List<String>>, destination: String, header: Boolean, title: String?, summary: Map<String, String>? = null, format: FormattingOptions = FormattingOptions())

    /**
     * Generates a report based on the provided data, adding rownumber in first column and save it on specified path .
     *
     * @param data Data to be used for generating the report.
     * @param destination Destination where the report will be saved.
     * @param header Flag indicating whether the report should contain a header.
     * @param title Title of the report.
     * @param summary Summary of the report.
     * @param printRowNumbers Flag indicating whether the report should contain row numbers.
     * @param format Formatting options for the report.
     */

    fun generateReport(
        data: Map<String, List<String>>,
        destination: String,
        header: Boolean,
        title: String?,
        summary: Map<String, String>? = null,
        printRowNumbers: Boolean = false,
        format: FormattingOptions = FormattingOptions()) {

        val newMap: MutableMap<String, List<String>> = LinkedHashMap()
        if (printRowNumbers) {
            newMap["Row Number"] = addRowNumbers(data.values.first().size)
        }

        newMap.putAll(data)
        generateReport(newMap, destination, header, title, summary, format)
    }
    /**
     * Generates a report based on the provided data, adding rownumber, gets summary as summary object  adding. rownumber in first column and save it on specified path .
     *
     * @param data Data to be used for generating the report.
     * @param destination Destination where the report will be saved.
     * @param header Flag indicating whether the report should contain a header.
     * @param title Title of the report.
     * @param summary Summary of the report.
     * @param calculations List of calculations to be performed on the data.
     * @param printRowNumbers Flag indicating whether the report should contain row numbers.
     * @param format Formatting options for the report.
     */
    fun generateReport(
        data: Map<String, List<String>>,
        destination: String,
        header: Boolean,
        title: String?,
        summary: Summary? = null,
        printRowNumbers: Boolean = false,
        format: FormattingOptions = FormattingOptions()
    ) {
        summary?.processSummary(data)
        generateReport(data, destination, header, title, summary?.summary, printRowNumbers, format)
    }
    /**
     * Generates a report based on the provided data,adding calculations as column, gets summary as summary object, adding rownumber in first column and save it on specified path .
     *
     * @param data Data to be used for generating the report.
     * @param destination Destination where the report will be saved.
     * @param header Flag indicating whether the report should contain a header.
     * @param title Title of the report.
     * @param summary Summary of the report.
     * @param calculations List of calculations to be performed on the data.
     * @param printRowNumbers Flag indicating whether the report should contain row numbers.
     * @param format Formatting options for the report.
     */
    fun generateReport(
        data: Map<String, List<String>>,
        destination: String,
        header: Boolean,
        title: String? = null,
        summary: Summary? = null,
        calculations: List<ColumnCalculations>,
        printRowNumbers: Boolean = false,
        format: FormattingOptions = FormattingOptions()
    ) {
        val toMap = data.toMutableMap()
        prepareData(toMap, calculations)
        generateReport(toMap, destination, header, title, summary, printRowNumbers, format)
    }
    /**
     * Generates a report based on the provided data as result set,adding calculations as column, gets summary as summary object, adding rownumber in first column and save it on specified path .
     *
     * @param resultSet Data to be used for generating the report.
     * @param destination Destination where the report will be saved.
     * @param header Flag indicating whether the report should contain a header.
     * @param title Title of the report.
     * @param summary Summary of the report.
     * @param printRowNumbers Flag indicating whether the report should contain row numbers.
     * @param format Formatting options for the report.
     */
    fun generateReport(resultSet: ResultSet, destination: String, header: Boolean, title: String? = null, summary: Summary? = null,
                       calculations: List<ColumnCalculations> = emptyList(),
                       printRowNumbers: Boolean, format: FormattingOptions = FormattingOptions()) {
        val data = prepareData(resultSet)
        prepareData(data, calculations)
        generateReport(data, destination, header, title, summary, printRowNumbers, format)
    }

    fun generateReport(resultSet: ResultSet, destination: String, header: Boolean, title: String? = null, summary: Summary? = null,
                       printRowNumbers: Boolean, format: FormattingOptions = FormattingOptions()) {
        generateReport(resultSet, destination, header, title, summary, emptyList(), printRowNumbers, format)
    }

    private fun addRowNumbers(size: Int) : List<String> {
        val result = mutableListOf<String>()
        for (i in 1..size) {
            result.add(i.toString())
        }
        return result
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