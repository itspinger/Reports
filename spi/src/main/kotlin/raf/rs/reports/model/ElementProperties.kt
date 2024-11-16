package raf.rs.reports.model

/**
 * Represents the formatting properties of an element within a report.
 *
 * The `ElementProperties` class contains the color and text styles for an element, such as the title, headers,
 * rows, and columns in the report. It provides methods to modify and retrieve text styles, as well as to manage the
 * properties of a report element dynamically.
 *
 * @property color The hex color of the element. Can be null if no color is specified.
 * @property textStyles Set of text styles (e.g., italic, bold, underline) applied to the element. Defaults to an empty set.
 */
class ElementProperties(var color: String?, private var textStyles: MutableSet<TextStyle> = mutableSetOf()) {

    /**
     * Companion object that provides utility methods for creating `ElementProperties` instances.
     */
    companion object {
        /**
         * Creates an instance of `ElementProperties` with the specified color and text styles.
         *
         * @param color the color to be assigned to the element properties. Can be `null` if no color is desired.
         * @param styles the text styles to be assigned (italic, bold, underline....)
         * @return a new `ElementProperties` instance with the provided color and text styles.
         */
        fun of(color: String?, vararg styles: TextStyle): ElementProperties {
            return ElementProperties(color, styles.toMutableSet())
        }

        /**
         * Creates an empty instance of `ElementProperties`, with no color and no text styles.
         *
         * @return an empty instance of `ElementProperties`.
         */
        fun empty(): ElementProperties {
            return ElementProperties(null, mutableSetOf())
        }
    }

    /**
     * Adds the specified text style to the element properties.
     *
     * If the style is already present, it will not be added again.
     *
     * @param textStyle the `TextStyle` to be added to the element properties.
     */
    fun addTextStyle(textStyle: TextStyle) {
        this.textStyles.add(textStyle)
    }

    /**
     * Removes the specified text styles from the element properties.
     *
     * Any matching `TextStyle` elements will be removed.
     *
     * @param styles text style elements to be removed from the element properties.
     */
    fun removeTextStyle(vararg styles: TextStyle) {
        this.textStyles.removeAll(styles.toSet())
    }

    /**
     * Clears all text styles from the element properties.
     *
     * After this method is called, the element will have no assigned text styles.
     */
    fun clearTextStyles() {
        this.textStyles.clear()
    }

    /**
     * Retrieves the set of text styles assigned to the element properties.
     *
     * @return set of `TextStyle` elements currently assigned to the element properties.
     */
    fun getTextStyles(): Set<TextStyle> {
        return textStyles
    }

    /**
     * Checks if the specified text style is assigned to the element properties.
     *
     * @param style the `TextStyle` to be checked.
     * @return whether the specified style is assigned to the element properties
     */
    fun hasTextStyle(style: TextStyle): Boolean {
        return textStyles.contains(style)
    }

    /**
     * Enum representing the possible text styles for the element properties.
     */
    enum class TextStyle {
        ITALIC, BOLD, UNDERLINE
    }
}