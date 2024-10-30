package raf.rs.reports.model

class Summary(private val sourceSummaryMap: Map<String, Any>) {
    private val map: MutableMap<String, String> = HashMap()

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

    val summary: Map<String, String> get() = this.map
}