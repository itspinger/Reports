import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import raf.rs.reports.IReport
import raf.rs.reports.ReportType
import java.io.InputStreamReader
import java.util.*

data class Schedule(
    val subject: String,
    val classroom: String,
    val year: Int,
    val group: String,
    val day: String,
    val time_from: String,
    val time_to: String
)

fun prepareData(jsonData: InputStreamReader): Map<String, List<String>> {
    val gson = Gson()
    val scheduleType = object : TypeToken<List<Schedule>>() {}.type
    val schedules: List<Schedule> = gson.fromJson(jsonData, scheduleType)

    // Convert the list into a Map<String, List<String>> where key is column name and value is list of values  
    return mapOf(
        "subject" to schedules.map { it.subject },
        "classroom" to schedules.map { it.classroom },
        "year" to schedules.map { it.year.toString() },
        "group" to schedules.map { it.group },
        "day" to schedules.map { it.day },
        "time_from" to schedules.map { it.time_from },
        "time_to" to schedules.map { it.time_to }
    )
}

fun main() {
    val serviceLoader = ServiceLoader.load(IReport::class.java)
    val exporterServices = mutableMapOf<ReportType, IReport>()

    serviceLoader.forEach { service ->
        exporterServices[service.getReportType] = service
    }

    println(exporterServices.keys)
    println(exporterServices[ReportType.TXT])

    val inputStream = object {}.javaClass.getResourceAsStream("/data.json")
    val reader = InputStreamReader(inputStream)
    val data = prepareData(reader)
    reader.close()

    println(data)

    exporterServices[ReportType.PDF]?.generateReport(data, destination = "izlaz3.pdf", header = true, title = ".....")
}  