package raf.rs.reports

enum class ReportType(private val extension: String) {
    /**
     * Enum class representing the type of the report also handles extension of destination.
     */
    TXT("txt"),
    CSV("csv"),
    PDF("pdf"),
    EXCEL("xlsx");
    /**
     * Applies the extension to the destination.
     *
     * @param destination Destination where the report will be saved.
     * @return Destination with the extension applied.
     */
    fun applyExtension(destination: String) : String {
        val extension = destination.substringAfterLast(".", "")
        if (extension.isEmpty()) {
            return "${destination}.${this.extension}"
        }

        if (extension == this.extension) {
            return destination
        }

        val pathWithoutExt = destination.dropLast(extension.length)
        return "${pathWithoutExt}${this.extension}"
    }
    /**
     * Companion object that provides utility methods for the enum class.
     */
    companion object {
        fun of(name: String) : ReportType? {
            for (type in entries) {
                if (type.name.equals(name, ignoreCase = true)) {
                    return type
                }
            }
            return null
        }
        /**
         * Returns the enum value based on the provided ordinal.
         *
         * @param ordinal Ordinal of the enum value.
         * @return Enum value based on the provided ordinal.
         */
        fun fromOrdinal(ordinal: Int) : ReportType? {
            for (type in entries) {
                if (type.ordinal == ordinal) {
                    return type
                }
            }
            return null
        }
    }

}