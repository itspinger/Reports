import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import raf.rs.reports.IReport
import raf.rs.reports.ReportType
import raf.rs.reports.calculations.ColumnCalculations
import raf.rs.reports.calculations.SummaryCalculation
import raf.rs.reports.calculations.SummaryCalculationType
import raf.rs.reports.model.Summary
import java.io.File
import java.io.FileReader
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
fun prepareJSONDataNew(jsonData: InputStreamReader): Map<String, List<String>> {
        val gson = Gson()
        val itemType = object : TypeToken<List<Map<String, String>>>() {}.type
        val dataList: List<Map<String, String>> = gson.fromJson(jsonData, itemType)


        return dataList
            .flatMap { it.entries } // Flatten all entries across all maps
            .groupBy({ it.key }, { it.value }) // Group by key, collecting values into lists


}

fun main() {
    val serviceLoader = ServiceLoader.load(IReport::class.java)
    val exporterServices = mutableMapOf<ReportType, IReport>()

    serviceLoader.forEach { service ->
        exporterServices[service.getReportType] = service
    }

   /* println(exporterServices.keys)
    println(exporterServices[ReportType.TXT])

    val inputStream = object {}.javaClass.getResourceAsStream("/data.json")
    val reader = InputStreamReader(inputStream)
    val data = prepareData(reader).toMutableMap()
    reader.close()

    val calculation = listOf(ColumnCalculations("years+", listOf("year", "group"), '/'))

    val summaryCalculations = SummaryCalculation("year", SummaryCalculationType.SUM, SummaryCalculation.Operator.GREATER_EQUAL, 3)
    val summary = Summary(mapOf("Avg Years" to summaryCalculations))

    exporterServices[ReportType.PDF]?.generateReport(data, destination = "izlaz5.pdf", header = true, title = ".....", summary = summary)
    println(data)

    exporterServices[ReportType.EXCEL]?.generateReport(data, destination = "izlaz3.xlsx", header = true, title = ".....", calculations =
    calculation, printRowNumbers = true)

    */
    var data: Map<String, List<String>> = emptyMap()
   var  implementation: IReport? = null;
    println("izaberite implementaciju za izvoz podataka : 1. TXT 2. EXCEL 3. PDF 4. CSV");
    when(readLine()) {
        "1" -> {
            if (exporterServices[ReportType.TXT] != null) {
                implementation = exporterServices[ReportType.TXT]
            }
        }

        "2" -> {
            implementation = exporterServices[ReportType.EXCEL]
        }

        "3" -> {
            implementation = exporterServices[ReportType.PDF]
        }

        "4" -> {
            implementation = exporterServices[ReportType.CSV]
        }
        else -> {
            println("Niste uneli validan broj");
        }
    }
    var i=0
    while(true){
        if(i!=0){
            println("Da li zelite da dodate jos jedan izvestaj 1. Da 2. Ne")
            if(readLine()=="2"){
                break;
            }
        }
        var inputType = "";
        println("izaberite nacin za unos podataka : 1. JSON 2. SQL 3. TXT, 4. CSV")
        when(readLine()) {
            "1" -> {
                inputType = "JSON";
            }

            "2" -> {
                inputType = "SQL";
            }

            "3" -> {
                inputType = "TXT";
            }

            "4" -> {
                inputType = "CSV";
            }
        }
        var path = "";
        if(inputType != "" && inputType != "SQL"){
            println("unesite putanju do fajla sa podacima")
             path= readLine().toString();

        }
        else{
            println("unesite korisnicko ime");
            var username = readLine();
            println("unesite lozinku");
            var password = readLine();
            println("ako ne zelite da unosite upit pretisnite enter");
            if(readLine() != ""){
                val query = readLine();
            }

        }
        if(path != ""){
           //val inputFromPath= File(path).readLines()
            when(inputType){
                "JSON" -> {
                    data =prepareJSONDataNew(FileReader(path))
                    /*val inputStream = File(path).inputStream()
                    val reader = InputStreamReader(inputStream)
                    val data = prepareData(reader).toMutableMap()
                    reader.close()

                     */
                }
                "TXT" -> {
                    val data = mutableMapOf<String, List<String>>()
                    val lines = File(path).readLines()
                    val headerInput = lines[0].split(",")
                    for (i in 1 until lines.size) {
                        val values = lines[i].split(",")

                    }
                }
                "CSV" -> {
                    val data = mutableMapOf<String, List<String>>()
                    val lines = File(path).readLines()
                    val headerInput = lines[0].split(",")
                    for (i in 1 until lines.size) {
                        val values = lines[i].split(",")

                    }
                }
           }

        }
        var destinationfile = "";
        println("Napsiste ime fajla u koji zelite da sacuvate izvestaj");
        destinationfile = readLine().toString();
        println("Da li zelite da dodate header? 1. Da 2. Ne")
        var header = false;
        if(readLine()=="1"){
            header = true;

        }
        var isTitle=false
        println("Da li zelite naslov? 1. Da 2. Ne");
        if(readLine()=="1"){
            isTitle = true;
        }
        var title = "";
        if(isTitle){
            println("unesite naslov");
            title = readLine().toString();
        }
        println("Da li zelite da dodate sazetak? 1. Da 2. Ne")
        var summary = false;
        if(readLine()=="1"){
            summary = true;
        }
        if(summary){
            while (true){
                println("unesite naziv labele");
                var summaryName = readLine();
                println("unesite ime kolone za koju zelite da izracunate sazetak");
                var summaryColumnName = readLine();
                println("unesite tip sazetka 1. Izracunjiv 2. Slobadan");
                if(readLine()=="1"){
                    println("unesite tip izracuna 1. SUM 2. AVG 3. COUNT");
                    var summaryType = readLine();
                    println("unesite uslov ako zelite, ako ne zelite unesite enter");
                    var summaryOperator = readLine();

                }
                else{
                    println("unesite sazetak");
                    var summaryType = readLine();
                }
                exporterServices[implementation!!.getReportType]?.generateReport( //ToDo treba da se doda summary
                    data,
                    destination=path,
                    header = header,
                    title = title,
                )

            }

        }
        else{
            for((key, value) in data){
                println(key)
                println(value)
            }
            exporterServices[implementation!!.getReportType]?.generateReport(
                data,
                destination=destinationfile,
                header = header,
                title = title,
            )

        }
        i++
    }
}  