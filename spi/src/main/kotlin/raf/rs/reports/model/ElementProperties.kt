package raf.rs.reports.model

class ElementProperties(var color: String?, private var textStyles: MutableSet<TextStyle> = mutableSetOf()) {

    companion object {
        fun of(color: String?, vararg styles: TextStyle): ElementProperties {
            return ElementProperties(color, styles.toMutableSet())
        }
    }

    fun addTextStyle(textStyle: TextStyle) {
        this.textStyles.add(textStyle)
    }

    fun hasTextStyle(style: TextStyle): Boolean {
        return textStyles.contains(style)
    }

    enum class TextStyle {
        ITALIC, BOLD, UNDERLINE
    }
}