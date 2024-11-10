package raf.rs.reports

enum class ReportType(private val extension: String) {

    TXT("txt"),
    CSV("csv"),
    PDF("pdf"),
    EXCEL("xlsx");

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

    companion object {
        fun of(name: String) : ReportType? {
            for (type in entries) {
                if (type.name.equals(name, ignoreCase = true)) {
                    return type
                }
            }
            return null
        }

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