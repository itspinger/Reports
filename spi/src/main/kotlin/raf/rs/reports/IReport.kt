package raf.rs.reports

import raf.rs.reports.calculations.ColumnCalculations
import raf.rs.reports.model.FormattingOptions
import raf.rs.reports.model.Summary
import java.sql.ResultSet
import java.sql.ResultSetMetaData

/**
 * Represents a contract (agreement) for generating various types of reports.
 *
 * The IReport interface provides a consistent API for generating reports from various data sources,
 * with support for features like row numbering, summaries, column calculations, and customizable formatting options.
 */
interface IReport {
    /**
     * This property indicates which report format or category (such as PDF, Excel, or CSV) the implementing class supports.
     */
    val reportType: ReportType

    /**
     * This method generates (creates) a report based on the specified data and saves it in the
     * specified path.
     *
     * Allows customization with options such as including a header, title, summary and
     * formatting options.
     *
     * @param data data used for generating the report.
     * @param destination destination where the report will be saved.
     * @param header flag indicating whether the report should contain a header.
     * @param title title of the report.
     * @param summary summary of the report.
     * @param format formatting options for the report.
     */
    fun generateReport(
        data: Map<String, List<String>>,
        destination: String,
        header: Boolean,
        title: String?,
        summary: Map<String, String>? = null,
        format: FormattingOptions = FormattingOptions()
    )

    /**
     * This method generates (creates) a report based on the specified data and saves it in the
     * specified path.
     *
     * Allows customization with options such as including a header, title, summary,
     * formatting options, as well as optional printing of row numbers, which is by default
     * disabled.
     *
     * @param data data used for generating the report.
     * @param destination destination where the report will be saved.
     * @param header flag indicating whether the report should contain a header.
     * @param title title of the report.
     * @param summary summary of the report.
     * @param printRowNumbers flag indicating whether the report should contain row numbers, by default false.
     * @param format Formatting options for the report.
     */
    fun generateReport(
        data: Map<String, List<String>>,
        destination: String,
        header: Boolean,
        title: String?,
        summary: Map<String, String>? = null,
        printRowNumbers: Boolean = false,
        format: FormattingOptions = FormattingOptions()
    ) {
        if (!printRowNumbers) {
            generateReport(data, destination, header, title, summary, format)
            return
        }

        val newMap: MutableMap<String, List<String>> = LinkedHashMap()
        newMap["Row Number"] = addRowNumbers(data.values.first().size)
        newMap.putAll(data)
        generateReport(newMap, destination, header, title, summary, format)
    }

    /**
     * This method generates (creates) a report based on the specified data and saves it in the
     * specified path.
     *
     * Allows customization with options such as header, title, and formatting options,
     * as well as optional printing of row numbers, which is by default disabled.
     *
     * If a `summary` parameter is provided, it will calculate summary values (like sum,
     * average, or count) for the specified columns. The `summary` is ultimately a map
     * of labels to string values (e.g., "Avg Years: 20"), where the labels represent the
     * summary calculation descriptions, and the values are the resulting calculations.
     *
     * @param data data used for generating the report.
     * @param destination destination where the report will be saved.
     * @param header flag indicating whether the report should contain a header.
     * @param title title of the report.
     * @param summary summary of the report.
     * @param printRowNumbers flag indicating whether the report should contain row numbers, by default false.
     * @param format formatting options for the report.
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
     * This method generates (creates) a report based on the specified data and saves it in the
     * specified path.
     *
     * Allows customization with options such as header, title, and formatting options,
     * as well as optional printing of row numbers, which is by default disabled.
     *
     * Additionally, column calculations can be applied to perform operations like sum,
     * multiplication, division or subtraction on one or more existing columns, with the result placed
     * in new columns specified by the `ColumnCalculations`. Each `ColumnCalculations`
     * object defines which columns to operate on and the operator to use, and the resulting
     * calculated values are stored in new columns.
     *
     * If a `summary` parameter is provided, it will calculate summary values (like sum,
     * average, or count) for the specified columns. The `summary` is ultimately a map
     * of labels to string values (e.g., "Avg Years: 20"), where the labels represent the
     * summary calculation descriptions, and the values are the resulting calculations.
     *
     * Both column calculations and summary calculations can be used together to adjust
     * the data accordingly before generating the final report.
     *
     * @param data data to be used for generating the report.
     * @param destination destination where the report will be saved.
     * @param header flag indicating whether the report should contain a header.
     * @param title title of the report.
     * @param summary summary of the report.
     * @param calculations list of calculations to be performed on the data.
     * @param printRowNumbers flag indicating whether the report should contain row numbers, by default false.
     * @param format formatting options for the report.
     *
     * @throws ArithmeticException if for any column calculation the operator is unknown or the columns to calculate from
     *                             exceed the maximum value for that operator
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
        val processedMap = data.toMutableMap()
        prepareData(processedMap, calculations)
        generateReport(processedMap, destination, header, title, summary, printRowNumbers, format)
    }

    /**
     * This method generates (creates) a report based on the provided data from the result set and saves it to the
     * specified destination.
     *
     * Allows customization with options such as header, title, and formatting options,
     * as well as optional printing of row numbers, which is by default disabled.
     *
     * Additionally, column calculations can be applied to perform operations like sum,
     * multiplication, division, or subtraction on one or more existing columns, with the result placed
     * in new columns specified by the `ColumnCalculations`. Each `ColumnCalculations`
     * object defines which columns to operate on and the operator to use, and the resulting
     * calculated values are stored in new columns.
     *
     * If a `summary` parameter is provided, it will calculate summary values (like sum,
     * average, or count) for the specified columns. The `summary` is ultimately a map
     * of labels to string values (e.g., "Avg Years: 20"), where the labels represent the
     * summary calculation descriptions, and the values are the resulting calculations.
     *
     * Both column calculations and summary calculations can be used together to adjust
     * the data accordingly before generating the final report.
     *
     * @param resultSet data to be used for generating the report.
     * @param destination destination where the report will be saved.
     * @param header flag indicating whether the report should contain a header.
     * @param title title of the report.
     * @param summary summary of the report.
     * @param calculations list of calculations to be performed on the data.
     * @param printRowNumbers flag indicating whether the report should contain row numbers, by default false.
     * @param format formatting options for the report.
     *
     * @throws ArithmeticException if for any column calculation the operator is unknown or the columns to calculate from
     *                             exceed the maximum value for that operator
     */
    fun generateReport(
        resultSet: ResultSet,
        destination: String,
        header: Boolean,
        title: String? = null,
        summary: Summary? = null,
        calculations: List<ColumnCalculations>,
        printRowNumbers: Boolean,
        format: FormattingOptions = FormattingOptions()
    ) {
        val data = prepareData(resultSet)
        prepareData(data, calculations)
        generateReport(data, destination, header, title, summary, printRowNumbers, format)
    }

    /**
     * This method generates (creates) a report based on the provided data from the result set and saves it to the
     * specified destination.
     *
     * Allows customization with options such as header, title, and formatting options,
     * as well as optional printing of row numbers, which is by default disabled.
     *
     * If a `summary` parameter is provided, it will calculate summary values (like sum,
     * average, or count) for the specified columns. The `summary` is ultimately a map
     * of labels to string values (e.g., "Avg Years: 20"), where the labels represent the
     * summary calculation descriptions, and the values are the resulting calculations.
     *
     * This method does not include explicit column calculations (`calculations` is empty by default),
     * but still allows for summary calculations.
     *
     * @param resultSet data to be used for generating the report.
     * @param destination destination where the report will be saved.
     * @param header flag indicating whether the report should contain a header.
     * @param title title of the report.
     * @param summary summary of the report.
     * @param printRowNumbers flag indicating whether the report should contain row numbers, by default false.
     * @param format formatting options for the report.
     */
    fun generateReport(
        resultSet: ResultSet,
        destination: String,
        header: Boolean,
        title: String? = null,
        summary: Summary? = null,
        printRowNumbers: Boolean,
        format: FormattingOptions = FormattingOptions()
    ) {
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