package raf.rs.reports.model

class ElementProperties(var color: String?, private var textStyles: MutableSet<TextStyle> = mutableSetOf()) {

    companion object {
        fun of(color: String?, vararg styles: TextStyle): ElementProperties {
            return ElementProperties(color, styles.toMutableSet())
        }

        fun empty(): ElementProperties {
            return ElementProperties(null, mutableSetOf())
        }
    }

    fun addTextStyle(textStyle: TextStyle) {
        this.textStyles.add(textStyle)
    }

    fun removeTextStyle(vararg styles: TextStyle) {
        this.textStyles.removeAll(styles.toSet())
    }

    fun clearTextStyles() {
        this.textStyles.clear()
    }

    fun getTextStyles(): Set<TextStyle> {
        return textStyles
    }

    fun hasTextStyle(style: TextStyle): Boolean {
        return textStyles.contains(style)
    }

    enum class TextStyle {
        ITALIC, BOLD, UNDERLINE
    }
}