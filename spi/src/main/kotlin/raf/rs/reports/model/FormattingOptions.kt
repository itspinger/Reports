package raf.rs.reports.model

class FormattingOptions {
    /**
     * Holds the title formating options for the report.
     */
    var titleFormat : ElementProperties? = null
    /**
     * Holds the header formating options for the report.
     */
    var headerFormat : ElementProperties? = null
    /**
     * Holds the border formating options for the report.
     */
    var borderStyle : BorderStyle = BorderStyle.NONE
    /**
     * Holds the column formating options for the report.
     */
    var columnFormat : MutableMap<String, ElementProperties> = mutableMapOf()
        private set

    /**
     * Holds the row formating options for the report.
     */
    var rowFormat : MutableMap<Int, ElementProperties> = mutableMapOf()
        private set

    /**
     * Holds the summary formating options for the report.
     */
    var summaryFormat : MutableMap<String, ElementProperties> = mutableMapOf()
        private set

    fun setColumnFormat(columnName: String, format: ElementProperties) {
        this.columnFormat[columnName] = format
    }

    fun setRowFormat(rowNumber: Int, format: ElementProperties) {
        this.rowFormat[rowNumber] = format
    }

    fun setSummaryFormat(summaryLabel: String, format: ElementProperties) {
        this.summaryFormat[summaryLabel] = format
    }
    /**
     * Enum class representing the border style of the report.
     */
    enum class BorderStyle {
        NONE, THIN, MEDIUM
    }

}