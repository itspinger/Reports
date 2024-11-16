package raf.rs.reports.model

/**
 * Represents the formatting options for various elements in a report, such as the title, header, rows, columns and summary.
 *
 * The `FormattingOptions` class provides flexibility in customizing the visual appearance of different sections of the
 * report. It allows specifying properties like color, text style for the title, header, individual
 * rows, columns, and summary elements. The properties can be modified and applied using various setter
 * methods.
 */
class FormattingOptions {

    /**
     * Specifies the formatting options for the title of the report.
     *
     * The title format allows the customization of the title's appearance, such as color and text style.
     * This property can be set to `null` if no specific formatting is required for the title.
     */
    var titleFormat : ElementProperties? = null

    /**
     * Specifies the formatting options for the report header.
     *
     * This property allows customization of the header's appearance, similar to `titleFormat`.
     */
    var headerFormat : ElementProperties? = null

    /**
     * Specifies the style of the report's border.
     *
     * The border style determines how the borders of the report are displayed (e.g., thin, medium, or none).
     * Defaults to `NONE` if no border is desired.
     */
    var borderStyle : BorderStyle = BorderStyle.NONE

    /**
     * A map holding formatting options for specific columns in the report.
     *
     * The key is the name of the column, and the value is the formatting for that column, which may include color,
     * text style, etc.
     */
    var columnFormat : MutableMap<String, ElementProperties> = mutableMapOf()
        private set

    /**
     * A map holding formatting options for specific rows in the report.
     *
     * The key is the row number, and the value is the formatting for that row.
     */
    var rowFormat : MutableMap<Int, ElementProperties> = mutableMapOf()
        private set

    /**
     * A map holding formatting options for specific summary labels in the report.
     *
     * The key is the summary label, and the value is the formatting for that label.
     */
    var summaryFormat : MutableMap<String, ElementProperties> = mutableMapOf()
        private set

    /**
     * Enum class representing the border style of the report.
     */
    enum class BorderStyle {
        NONE, THIN, MEDIUM
    }

}