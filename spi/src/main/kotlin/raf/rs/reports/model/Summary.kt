package raf.rs.reports.model

import raf.rs.reports.calculations.SummaryCalculation

/**
 * The `Summary` class is responsible for managing/calculating the summary data that is being
 * provided to a report.
 *
 * Since summary is basically a set of `label:value` pairs, any type of value can be provided
 * to this model, as long as it can be converted to a string.
 *
 * @param sourceSummaryMap A map containing the source summary data, where the key is the summary label
 *                         and the value can either be a static value (anything other than a `SummaryCalculation`)
 *                         or a `SummaryCalculation` object that defines the calculations that need to be processed.
 */
class Summary(private val sourceSummaryMap: Map<String, Any>) {
    private val map: MutableMap<String, String> = HashMap()

    /**
     * Processes and updates the summary based on the provided data.
     *
     * This method omits any previously stored summary results and recalculates the summary values.
     * For each entry in the source summary map, it either performs the associated calculation
     * (if the value is a `SummaryCalculation`) or directly converts the value to a string
     * (for static values), and stores the result in the summary map.
     *
     * @param data A map containing column data where each key represents a column name,
     *             and the value is a list of data points for that column. This data is used
     *             to perform calculations for summary values when applicable.
     */
    fun processSummary(data: Map<String, List<String>>) {
        this.map.clear()
        for ((key, value) in this.sourceSummaryMap) {
            // If it's calculation we need to process, it otherwise just call toString()
            if (value is SummaryCalculation) {
                this.map[key] = value.calculateValues(data).toString()
            } else {
                this.map[key] = value.toString()
            }
        }
    }

    /**
     * Returns the processed summary of the report.
     *
     * The map contains the summary data, where each entry has a label as the key (e.g., "Avg Price")
     * and the value is a string representing the calculated or static summary value (e.g., "20" or "Unknown").
     *
     * @return A map of summary labels and their corresponding string values.
     */
    val summary: Map<String, String> get() = this.map
}