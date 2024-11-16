package raf.rs.reports

/**
 * Enum class representing the type of the report, which by default supports an operation
 * for handling (generating) extensions based on the type of the report.
 *
 * @property extension The default extension supported by this report type
 */
enum class ReportType(private val extension: String) {
    TXT("txt"),
    CSV("csv"),
    PDF("pdf"),
    EXCEL("xlsx");

    /**
     * Applies the extension to the destination path.
     *
     * If the path already contains an extension, but the extension doesn't
     * match the extension that this report type supports, or the path doesn't
     * contain an extension, the correct one will be appended with this method.
     *
     * @param destination the destination where the report is supposed to be saved
     * @return the full destination with the correct extension
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

}