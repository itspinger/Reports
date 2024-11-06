import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import raf.rs.reports.IReport
import raf.rs.reports.ReportType
import raf.rs.reports.calculations.SummaryCalculation
import raf.rs.reports.calculations.SummaryCalculationType
import raf.rs.reports.model.ElementProperties
import raf.rs.reports.model.FormattingOptions
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

    val formatOptions = FormattingOptions()
    formatOptions.titleFormat = ElementProperties.of(null, ElementProperties.TextStyle.UNDERLINE, ElementProperties.TextStyle.ITALIC)
    formatOptions.headerFormat = ElementProperties.of("#f72105", ElementProperties.TextStyle.BOLD)
    formatOptions.rowFormat[1] = ElementProperties.of("#22d3d6", ElementProperties.TextStyle.ITALIC)
    formatOptions.columnFormat["day"] = ElementProperties.of(null, ElementProperties.TextStyle.UNDERLINE)
    formatOptions.setSummaryFormat("Avg Years", ElementProperties.of(null, ElementProperties.TextStyle.BOLD))
    formatOptions.borderStyle = FormattingOptions.BorderStyle.THIN

    println(formatOptions.rowFormat[1])
    println(formatOptions.titleFormat)
    println(formatOptions.headerFormat)

    exporterServices[ReportType.EXCEL]?.generateReport(data, destination = "izlaz3.xlsx", header = true, title = ".....", calculations =
<<<<<<< Updated upstream
    calculation, printRowNumbers = true)

    */
    var summaryCalculations = null as SummaryCalculation?
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
        var formatOptions= null as FormattingOptions?
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
        var isPrintRowNumbers = false;
        println("Da li zelite da dodate brojanje kolona? 1. Da 2. Ne")
        if(readLine()=="1"){
            isPrintRowNumbers = true;
        }

            println("Da li zelite da dodate sazetak? 1. Da 2. Ne")
            var summary = false;
            var isCalcSummary = false
            var calcSummary = "";
            var nonCalcSummary = "";

            if (readLine() == "1") {
                summary = true;
            }
            var summaryLabel = "";
            var summaryColumnName = "";
            var summaryType = "";
            //var summaryOperator = "";
            var summaryOperator= null as SummaryCalculation.Operator?
            if (summary) {
                println("unesite naziv labele");
                summaryLabel = readLine().toString();
                println("unesite tip sazetka 1. Izracunjiv 2. Slobadan");
                if (readLine() == "1") {
                    isCalcSummary = true;
                    println("unesite ime kolone za koju zelite da izracunate sazetak");
                    summaryColumnName = readLine().toString()
                    println("unesite tip izracunavanja SUM  AVG  COUNT");
                    summaryType = readLine().toString()
                    calcSummary = summaryType.toString() + " " + summaryColumnName.toString();
                    println("unesite operator za uslov > < >= <= != =, ako ne zelite unesite enter")

                    when(readLine()){
                        ">" -> summaryOperator= SummaryCalculation.Operator.GREATER
                        "<" -> summaryOperator= SummaryCalculation.Operator.LESSER
                        ">=" -> summaryOperator= SummaryCalculation.Operator.GREATER_EQUAL
                        "<=" -> summaryOperator= SummaryCalculation.Operator.LESS_EQUAL
                        "!=" -> summaryOperator= SummaryCalculation.Operator.NOT_EQUAL
                    }
                    println("summaryOperator"+summaryOperator)

                    var value = 0;
                    println("unesite vrednost za uslov")
                    if(summaryOperator!=null) {
                        value = readLine()?.toInt() as Int
                        summaryCalculations = SummaryCalculation(summaryColumnName, SummaryCalculationType.valueOf(summaryType),summaryOperator, value)
                    }
                    else
                        summaryCalculations = SummaryCalculation(summaryColumnName, SummaryCalculationType.valueOf(summaryType))

                    println("value"+value)

                    calcSummary += " " + summaryOperator.toString();
                    calcSummary += " " + value.toString();


                } else {
                    println("unesite sazetak")
                    nonCalcSummary = readLine().toString()
                }



            }
            if(implementation!!.getReportType == ReportType.EXCEL || implementation!!.getReportType == ReportType.PDF) {


                println("Da li zelite da dodate formatiranje? 1. Da 2. Ne")
                if (readLine() == "1") {
                    formatOptions = FormattingOptions()
                    var style = "";
                    var color = "";
                    while (true) {
                        println("Birajte sta zelite da formatirate 1. Naslov 2. Header 3. Red 4. Kolonu 5.Sažetak ako ne zelite da dodate vise formatiranja pretisnite enter")
                        var formatOption = readLine();
                        if (formatOption == "") {
                            break;
                        }
                        println("unesite boju crvena plava zelena");
                        color = readLine().toString();
                        println("unesite stil  BOLD ITALIC  UNDERLINE");
                        style = readLine().toString();
                        if (color == "crvena") {
                            color = "#f72105";
                        } else if (color == "plava") {
                            color = "#22d3d6";
                        } else if (color == "zelena") {
                            color = "#22d6a6";
                        }


                        when (formatOption) {
                            "1" -> {
                                formatOptions.titleFormat =
                                    ElementProperties.of(color, ElementProperties.TextStyle.valueOf(style))
                            }

                            "2" -> {
                                formatOptions.headerFormat =
                                    ElementProperties.of(color, ElementProperties.TextStyle.valueOf(style))
                            }

                            "3" -> {
                                println("Unesite redni broj reda koji zelite da formatirate")
                                var rowNumber = readLine().toString().toInt();
                                formatOptions.rowFormat[rowNumber] =
                                    ElementProperties.of(color, ElementProperties.TextStyle.valueOf(style))
                            }

                            "4" -> {
                                println("Unesite ime kolone koju zelite da formatirate")
                                var columnName = readLine().toString();
                                formatOptions.columnFormat[columnName] =
                                    ElementProperties.of(color, ElementProperties.TextStyle.valueOf(style))
                            }

                            "5" -> {
                                formatOptions.summaryFormat[summaryLabel] =
                                    ElementProperties.of(color, ElementProperties.TextStyle.valueOf(style))
                            }
                        }
                        println("Da li zelite da promenite debljinu linje? 1. Da 2. Ne")
                        if (readLine() == "1") {
                            println("Unesite debljinu linije 1. NONE 2. THIN 3. MEDIUM")
                            var borderStyle = readLine().toString();
                            when (borderStyle) {
                                "1" -> {
                                    formatOptions.borderStyle = FormattingOptions.BorderStyle.NONE
                                }

                                "2" -> {
                                    formatOptions.borderStyle = FormattingOptions.BorderStyle.THIN
                                }

                                "3" -> {
                                    formatOptions.borderStyle = FormattingOptions.BorderStyle.MEDIUM
                                }
                            }
                        }
                    }
                }
            }
            if(summary){
                if (isCalcSummary)
                    exporterServices[implementation!!.getReportType]?.generateReport(
                        data,
                        destination = destinationfile,
                        header = header,
                        title = title,
                        format = formatOptions!!,
                        summary = Summary(mapOf(summaryLabel.toString() to summaryCalculations as Any)),


                    )
                else {
                    exporterServices[implementation!!.getReportType]?.generateReport(
                        data,
                        destination = destinationfile,
                        header = header,
                        title = title,
                        mapOf<String, String>(Pair(summaryLabel.toString(), nonCalcSummary.toString())),
                        format = formatOptions!!,
                        printRowNumbers = isPrintRowNumbers

                    )
                }
            }
            else{
                exporterServices[implementation!!.getReportType]?.generateReport(
                    data,
                    destination=destinationfile,
                    header = header,
                    title = title,
                    summary = null as Summary?,
                    printRowNumbers = isPrintRowNumbers,
                    format = formatOptions!!
                )

            }

        i++
    }
}  