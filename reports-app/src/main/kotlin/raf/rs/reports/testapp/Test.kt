@file:Suppress("SpellCheckingInspection", "UNCHECKED_CAST")

package raf.rs.reports.testapp

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import raf.rs.reports.IReport
import raf.rs.reports.ReportType
import raf.rs.reports.calculations.ColumnCalculations
import raf.rs.reports.calculations.SummaryCalculation
import raf.rs.reports.calculations.SummaryCalculationType
import raf.rs.reports.model.ElementProperties
import raf.rs.reports.model.FormattingOptions
import raf.rs.reports.model.Summary
import java.awt.Color
import java.io.File
import java.io.FileReader
import java.sql.Connection
import java.sql.DriverManager
import java.util.*
import kotlin.system.exitProcess

data class MenuOption(val option: () -> String, val action: () -> Unit) {
    constructor(option: String, action: () -> Unit) : this({ option }, action)
    constructor(option: String) : this({ option }, {})
}

data class Database(val databaseUrl: String, val username: String, val password: String, val query: String) {

    fun getConnection() : Connection {
        return DriverManager.getConnection(this.databaseUrl, this.username, this.password)
    }

}

val colorPalette = listOf(
    "#000000", "#993300", "#333300", "#003300", "#003366", "#000080", "#333399", "#333333",
    "#800000", "#FF6600", "#808000", "#008000", "#008080", "#0000FF", "#666699", "#808080",
    "#FF0000", "#FF9900", "#99CC00", "#339966", "#33CCCC", "#3366FF", "#800080", "#969696",
    "#FF00FF", "#FFCC00", "#FFFF00", "#00FF00", "#00FFFF", "#00CCFF", "#993366", "#C0C0C0",
    "#FF99CC", "#FFCC99", "#FFFF99", "#CCFFCC", "#CCFFFF", "#99CCFF", "#CC99FF", "#FFFFFF",
    "#9999FF", "#993366", "#FFFFCC", "#CCFFFF", "#660066", "#FF8080", "#0066CC", "#CCCCFF",
    "#000080", "#FF00FF", "#FFFF00", "#00FFFF", "#800080", "#800000", "#008080", "#0000FF"
)

val colorsColored = colorPalette.map { colorizeText(it, it) }
val defaultColor = colorPalette[0]

fun main() {
    val serviceLoader = ServiceLoader.load(IReport::class.java)
    val exporterServices = mutableMapOf<ReportType, IReport>()

    serviceLoader.forEach { service ->
        exporterServices[service.getReportType] = service
    }

    val implementation: IReport = chooseImplementation(exporterServices)

    /* Run an infinite loop */
    var iteration = 0
    while (true) {
        if (iteration != 0 && !promptContinue()) {
            break
        }

        val inputType = chooseDataInputType("JSON", "SQL", "TXT", "CSV")
        println(inputType)

        // Nakon sto smo uneli tip, unosimo putanju do fajla
        // Da bismo ucitali tip
        val data : Any? = if (inputType == "SQL") {
            retrieveDataFromSql()
        } else {
            retrieveDataFromInputType(inputType)
        }

        if (data == null) {
            println("Nismo mogli da ucitamo podatke iz tog fajla, probajte opet!")
            continue
        }

        val destination = askQuestion("Unesite ime fajla za cuvanje izvestaja")

        // Dovde smo dobili sve informacije koje su nam zapravo potrebe za generisanje izvestaja
        // I opciono dalje dajemo korisniku da konfigurise po njegovoj zelji
        displayReportMenu(implementation, inputType, data, destination)
        iteration++
    }
}

fun displayReportMenu(report: IReport, inputType: String, data: Any, destination: String) {
    var header = false
    var showRowNumbers = false
    var title : String? = null
    val summaryData: MutableMap<String, Any> = mutableMapOf()
    val columnCalculation : MutableList<ColumnCalculations> = mutableListOf()
    val formatOptions = FormattingOptions()

    val menuOptions = mutableListOf(
        MenuOption({ "Dodajte/izbacite header (${if (header) "Yes" else "No"})" }) { header = !header; },
        MenuOption({ "Dodajte/izbacite brojanje redova (${if (showRowNumbers) "Yes" else "No"})" }) { showRowNumbers = !showRowNumbers },
        MenuOption({ "Izmenite naslov (${title ?: "Trenutno nema"})" }) { title = chooseTitle() },
        MenuOption("Izmenite rezime") { configureSummary(summaryData) },
        MenuOption("Dodajte kalkulacije za kolone") { configureColCalc(columnCalculation) },
        MenuOption("Izmenite formatiranje") { configureFormatting(formatOptions) },
        MenuOption("Generisite izvestaj") {
            // Ovde generisemo izvestaj na kraju
            // Moramo samo da proverimo koji je data tip u podatku
            // Da bismo mogli da prosledimo
            val summary: Summary? = if (summaryData.isEmpty()) null else Summary(summaryData)

            if (inputType == "SQL") {
                val database = data as Database
                database.getConnection().use { connection ->
                    connection.prepareStatement(database.query).use { statement ->
                        statement.executeQuery().use { resultSet ->
                            report.generateReport(
                                resultSet,
                                destination,
                                header,
                                title,
                                summary,
                                columnCalculation,
                                showRowNumbers,
                                formatOptions
                            )
                        }
                    }
                }

                return@MenuOption
            }

            // Ako nije SQL, onda znamo da je data Map<String, List<String>>
            val finalData = data as Map<String, List<String>>
            report.generateReport(
                finalData,
                destination,
                header,
                title,
                summary,
                columnCalculation,
                showRowNumbers,
                formatOptions
            )
        }
    )

    buildMenu("Izaberite opcije", MenuOption("Vratite se nazad"), options = menuOptions)
}

fun chooseTitle() : String? {
    return askQuestion("Unesite tekst za naslov ili ostavite prazno ukoliko ne zelite naslov").ifEmpty{ null }
}

fun configureSummary(source: MutableMap<String, Any>) {
    val menuOptions = mutableListOf(
        MenuOption("Dodajte novi rezime") { addSummary(source) },
        MenuOption("Ocistite ceo rezime") { source.clear() },
        MenuOption("Za pregled trenutnog rezime-a") {
            if (source.isEmpty()) {
                println("Summary je prazan!")
                return@MenuOption
            }

            println("Pregled za summary!")
            println("---------------------")
            source.forEach { (key, value) -> println("$key: $value") }
            println("---------------------")
        }
    )

    val returnOption = MenuOption("Vratite se nazad")
    buildMenu("Izaberite opcije", returnOption, menuOptions)
}

fun addSummary(source: MutableMap<String, Any>) {
    val label = askQuestion("Unesite naziv labele, ostavite prazno da se vratite")
    if (label.isEmpty()) {
        return
    }

    val choice = askQuestionWithOptionIndex("Izaberite tip vrednosti za rezime", "Labela", "Kalkulacija")
    if (choice == 0) {
        val value = askQuestion("Unesite vrednost za rezime, ostavite prazno da se vratite")
        if (value.isEmpty()) {
            return
        }

        println("Uspesno ste dodali nov deo za rezime!")
        source[label] = value
        return
    }

    val columnName = askQuestion("Unesite naziv kolone za koju se obracunava kalkulacija")
    if (columnName.isEmpty()) {
        return
    }

    val type = askQuestionsWithOptionIndex("Izaberite tip kalkulacija", SummaryCalculationType::class.java)
    val uslov = promptYesNo("Da li zelite da unesete uslov za kalkulaciju?")

    if (!uslov) {
        println("Uspesno ste dodali nov deo za rezime!")
        source[label] = SummaryCalculation(columnName, type)
        return
    }

    val operator = askQuestionsWithOptionIndex("Izaberite operator", SummaryCalculation.Operator::class.java) { operator -> operator.operator }
    val targetValue = askQuestion("Izaberite vrednost za poredjenje, ostavite prazno da odustanete")
    if (targetValue.isEmpty()) {
        println("Uspesno ste dodali nov deo za rezime!")
        source[label] = SummaryCalculation(columnName, type)
        return
    }

    val targetValueInt = targetValue.toIntOrNull() ?: return
    println("Uspesno ste dodali nov deo za rezime!")
    source[label] = SummaryCalculation(columnName, type, operator, targetValueInt)
}

fun configureColCalc(calculations: MutableList<ColumnCalculations>) {
    val menuOptions = mutableListOf(
        MenuOption("Dodajte novu kalkulaciju") { addCalculation(calculations) },
        MenuOption("Ocistite sve kalkulacije") { calculations.clear() },
        MenuOption("Za pregled trenutnih kalkulacija") {
            if (calculations.isEmpty()) {
                println("Kalkulacije su prazne")
                return@MenuOption
            }

            println("Pregled za kalkulacije!")
            println("---------------------")
            calculations.forEach { calculation -> println("$calculation") }
            println("---------------------")
        }
    )

    val returnOption = MenuOption("Vratite se nazad")
    buildMenu("Izaberite opcije", returnOption, menuOptions)
}

fun addCalculation(source: MutableList<ColumnCalculations>) {
    val columnName = askQuestion("Unesite naziv nove kolone, ostavite prazno da se vratite")
    if (columnName.isEmpty()) {
        return
    }

    val columns =
        askQuestion("Unesite naziv kolona na osnovu kojih se vrsi kalkulacija, odvojene zarezom (ex. year 2018, year2019)")
            .split(",".toRegex())
            .map { it.trim() }
            .filter { it.isNotEmpty() }

    // Necemo da proveravamo ovde nazive kolona
    // Ako smo uneli kolonu koja ne postoji
    // Samo ce da se preskoci, da ne bismo bacali gresku bez razloga
    if (columns.isEmpty()) {
        println("Niste uneli naziv nijedne kolone, vracamo se nazad...")
        return
    }

    // Ovo je karakter koji predstavlja taj operator
    val operator = askQuestionWithOptions("Unesite operator za kalkulaciju", "+", "*", "-", "/")[0]
    source.add(ColumnCalculations(columnName, columns, operator))
    println("Uspesno ste dodali kalkulaciju za kolonu")
}

fun configureFormatting(formatOptions: FormattingOptions) {
    val titleStyle = formatOptions.titleFormat ?: ElementProperties.empty()
    val headerStyle = formatOptions.headerFormat ?: ElementProperties.empty()

    val menuOptions = mutableListOf(
        MenuOption("Formatiraj title") {
            chooseElementProperties(titleStyle)
            formatOptions.titleFormat = titleStyle
        },
        MenuOption("Formatiraj header") {
            chooseElementProperties(headerStyle)
            formatOptions.headerFormat = headerStyle
        },
        MenuOption("Formatiraj kolonu") {
            val columnName = askQuestion("Izaberi naziv kolone za formatiranje, ostavi prazno da odustanes")
            if (columnName.isEmpty()) {
                return@MenuOption
            }

            val columnFormat = formatOptions.columnFormat[columnName] ?: ElementProperties.empty()
            chooseElementProperties(columnFormat)
            formatOptions.columnFormat[columnName] = columnFormat
        },
        MenuOption("Formatiraj red") {
            val rowNum = askQuestion("Unesi broj reda za formatiranje, ostavi prazno da odustanes")
            if (rowNum.isEmpty()) {
                return@MenuOption
            }

            val actualRowNum = rowNum.toIntOrNull() ?: return@MenuOption
            val rowFormat = formatOptions.rowFormat[actualRowNum] ?: ElementProperties.empty()
            chooseElementProperties(rowFormat)
            formatOptions.rowFormat[actualRowNum] = rowFormat
        },
        MenuOption("Formatiraj rezime") {
            val labelName = askQuestion("Izaberi naziv labele za formatiranje, ostavi prazno da odustanes")
            if (labelName.isEmpty()) {
                return@MenuOption
            }

            val labelFormat = formatOptions.summaryFormat[labelName] ?: ElementProperties.empty()
            chooseElementProperties(labelFormat)
            formatOptions.summaryFormat[labelName] = labelFormat
        },
        MenuOption("Formatiraj debljinu linije") {
            val lineWeight = askQuestionsWithOptionIndex("Izaberi debljinu linije", FormattingOptions.BorderStyle::class.java)
            formatOptions.borderStyle = lineWeight
        },
    )

    val returnOption = MenuOption("Vratite se nazad")
    buildMenu("Izaberite opcije", returnOption, menuOptions)
}

fun colorizeText(text: String?, colorHex: String?) : String {
    if (colorHex == null || text == null) {
        return colorizeText(defaultColor, defaultColor)
    }

    try {
        val color = Color.decode(colorHex)
        return "\u001B[38;2;${color.red};${color.green};${color.blue}m$text\u001B[0m"
    } catch (e: Exception) {
        return text
    }
}

fun chooseElementProperties(source: ElementProperties) {
    val menuOptions = mutableListOf(
        MenuOption({ "Postavi boju (${colorizeText(source.color, source.color)})" }) {
            val index = askQuestionWithOptionIndex("Izaberi boju", *colorsColored.toTypedArray())
            source.color = colorPalette[index]
        },
        MenuOption({ "Dodaj stil (${source.getTextStyles().joinToString(",") { it.name }})" }) {
            val style = askQuestionsWithOptionIndex("Izaberi stil", ElementProperties.TextStyle::class.java)
            source.addTextStyle(style)
        },
        MenuOption("Resetuj boju") { source.color = null },
        MenuOption("Ocisti sve stilove") { source.clearTextStyles() }
    )

    val returnOption = MenuOption("Vratite se nazad")
    buildMenu("Izaberite opcije", returnOption, menuOptions)
}

fun buildMenu(question: String, escapeOption: MenuOption, options: MutableList<MenuOption>) {
    options.add(escapeOption)

    val lastIndex = options.indexOf(escapeOption)
    var index = -1
    while (index != lastIndex) {
        val questionOptions = options.map { it.option.invoke() }
        index = askQuestionWithOptionIndex(question, *questionOptions.toTypedArray())
        if (index == lastIndex) {
            return
        }

        options[index].action.invoke()
    }
}

fun chooseImplementation(reports: Map<ReportType, IReport>) : IReport {
    val reportType = askQuestionsWithOptionIndex("Izaberite implementaciju za izvoz podataka", ReportType::class.java)
    val report = reports[reportType] ?: exit("Trenutno ne podrzavamo ovaj tip izvestaja")
    return report
}

fun retrieveDataFromInputType(inputType: String) : Map<String, List<String>>? {
    println("Unesite putanju do zeljenog fajla:")

    val destination = prompt()
    val file = File(destination)
    if (!file.exists()) {
        println("Ovaj fajl ne postoji!")
        return null
    }

    if (file.extension != inputType.lowercase()) {
        println("Nije dozvoljen fajl sa ovom ekstenzijom za ovaj input tip")
        return null
    }

    return when (inputType) {
        "JSON" -> prepareJSONData(file)
        else -> prepareTextData(file)
    }
}

fun retrieveDataFromSql() : Database? {
    val username = askQuestion("Unesite username")
    val password = askQuestion("Unesite password")
    val address = askQuestion("Unesite adresu i port za SQL server u obliku: \"address:port\"")
    if (address.isEmpty()) {
        println("Adresa ne moze da bude prazna...")
        return null
    }

    val schema = askQuestion("Unesite naziv baze podataka")
    if (schema.isEmpty()) {
        println("Ime baze podataka ne moze da bude prazna...")
        return null
    }

    try {
        Class.forName("com.mysql.cj.jdbc.Driver")
    } catch (e: Exception) {
        e.printStackTrace()
        exitProcess(-1)
    }

    val query = askQuestion("Unesite SQL query za ucitavanje podataka")
    if (query.isEmpty()) {
        println("Query ne moze da bude prazan...")
        return null
    }

    val connectionUrl = "jdbc:mysql://${address}/${schema}"
    return Database(connectionUrl, username, password, query)
}

fun prepareTextData(file: File): Map<String, List<String>>? {
    val data = linkedMapOf<String, MutableList<String>>()
    val delimiter = ','
    try {
        val lines = file.readLines()
        if (lines.isEmpty()) {
            return null
        }

        val headers = lines[0].split(delimiter).map { it.trim() }
        headers.forEach { header -> data[header] = mutableListOf() }

        for (line in lines.drop(1)) {
            val row = line.split(delimiter).map { it.trim() }
            if (row.size != headers.size) { // Not the same size
                return null
            }

            headers.forEachIndexed { index, header ->
                data[header]?.add(row[index])
            }
        }

        return data
    } catch (e: Exception) {
        println("Desila se greska pri pokusavanju citanja iz fajla")
        return null
    }
}

fun prepareJSONData(file: File): Map<String, List<String>>? {
    try {
        FileReader(file).use { reader ->
            val gson = Gson()
            val itemType = object : TypeToken<List<Map<String, String>>>() {}.type
            val dataList: List<Map<String, String>> = gson.fromJson(reader, itemType)
            return dataList.flatMap { it.entries }.groupBy({ it.key }, { it.value })
        }
    } catch (e: Exception) {
        println("Desila se greska pri pokusaju citanja iz fajla")
        return null
    }
}

fun promptContinue() : Boolean {
    return promptYesNo("Da li zelite da izgenerisete jos jedan izvestaj?")
}

fun chooseDataInputType(vararg option: String): String {
    return askQuestionWithOptions("Izaberite nacin za unos podataka", *option)
}

fun prompt(text: String, newLine: Boolean = false) : String {
    if (newLine) println(text) else print(text)
    return readlnOrNull().orEmpty()
}

fun prompt(newLine: Boolean = false) : String {
    return prompt("> ", newLine)
}

fun askQuestion(question: String) : String {
    println(question)
    return prompt()
}

fun askQuestionWithOptionIndex(question: String, vararg options: String): Int {
    while (true) {
        println("$question: ")
        options.forEachIndexed { index, option ->
            println("${index + 1}. $option")
        }

        val input = prompt()
        val selectedChoice = input.toIntOrNull()?.minus(1)
        if (selectedChoice != null && selectedChoice in options.indices) {
            return selectedChoice
        } else {
            println("Niste uneli validan broj, pokusajte ponovo")
        }
    }
}

fun <T : Enum<T>> askQuestionsWithOptionIndex(question: String, enum: Class<T>, map: (T) -> String) : T {
    val options = enum.enumConstants
    val optionsMapped = options.map { map.invoke(it) }.toTypedArray()
    return options[askQuestionWithOptionIndex(question, *optionsMapped)]
}

fun <T : Enum<T>> askQuestionsWithOptionIndex(question: String, enum: Class<T>) : T {
    return askQuestionsWithOptionIndex(question, enum) { value -> value.name }
}

fun askQuestionWithOptions(question: String, vararg options: String) : String {
    return options[askQuestionWithOptionIndex(question, *options)]
}

fun promptYesNo(question: String): Boolean {
    return askQuestionWithOptions(question, "Da", "Ne") == "Da"
}

fun exit(message: String, code: Int = -1) : Nothing {
    println(message)
    exitProcess(code)
}