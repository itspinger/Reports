package raf.rs.reports.model

class ElementProperties(var color: String?, private var textStyles: MutableSet<TextStyle> = mutableSetOf()) {
    /**
     * Companion object that handles the creation of the element properties.
     */
    companion object {
        /**
         * Creates an element properties with the provided color and text styles.
         *
         * @param color Color of the element properties.
         * @param styles Text styles of the element properties.
         * @return Element properties with the provided color and text styles.
         */
        fun of(color: String?, vararg styles: TextStyle): ElementProperties {
            return ElementProperties(color, styles.toMutableSet())
        }
        /**
         * Creates an empty element properties.
         *
         * @return Empty element properties.
         */
        fun empty(): ElementProperties {
            return ElementProperties(null, mutableSetOf())
        }
    }
    /**
     * Adds the provided text style to the element properties.
     *
     * @param textStyle Text style to be added.
     */
    fun addTextStyle(textStyle: TextStyle) {
        this.textStyles.add(textStyle)
    }
    /**
     * Removes the provided text style from the element properties.
     *
     * @param styles Text styles to be removed.
     */
    fun removeTextStyle(vararg styles: TextStyle) {
        this.textStyles.removeAll(styles.toSet())
    }
    /**
     * Clears the text styles from the element properties.
     */
    fun clearTextStyles() {
        this.textStyles.clear()
    }
    /**
     * Returns the color of the element properties.
     *
     * @return Color of the element properties.
     */
    fun getTextStyles(): Set<TextStyle> {
        return textStyles
    }
    /**
     * Checks if the element properties has the provided text style.
     *
     * @param style Text style to be checked.
     * @return Flag indicating whether the element properties has the provided text style.
     */
    fun hasTextStyle(style: TextStyle): Boolean {
        return textStyles.contains(style)
    }
    /**
     * Enum class representing the text style of the element properties.
     */
    enum class TextStyle {
        ITALIC, BOLD, UNDERLINE
    }
}