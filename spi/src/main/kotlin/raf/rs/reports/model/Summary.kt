package raf.rs.reports.model

import raf.rs.reports.calculations.SummaryCalculation

class Summary(private val sourceSummaryMap: Map<String, Any>) {
    /**
     * Holds the summary of the report in hashmap.
     */
    private val map: MutableMap<String, String> = HashMap()
    /**
     * Processes the summary based on the provided data and handles both types of summaries.
     *
     * @param data Data to be used for processing the summary.
     */
    fun processSummary(data: Map<String, List<String>>) {
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
     * Returns the summary of the report.
     *
     * @return Summary of the report.
     */
    val summary: Map<String, String> get() = this.map
}