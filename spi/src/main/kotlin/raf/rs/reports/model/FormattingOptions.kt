package raf.rs.reports.model

class FormattingOptions {
    var titleFormat : ElementProperties? = null
    var headerFormat : ElementProperties? = null
    var borderStyle : BorderStyle = BorderStyle.NONE

    var columnFormat : MutableMap<String, ElementProperties> = mutableMapOf()
        private set

    var rowFormat : MutableMap<Int, ElementProperties> = mutableMapOf()
        private set

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

    enum class BorderStyle {
        NONE, THIN, MEDIUM
    }

}