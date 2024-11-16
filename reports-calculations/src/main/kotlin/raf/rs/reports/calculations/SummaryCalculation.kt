package raf.rs.reports.calculations

import kotlin.jvm.Throws

/**
 * Represents a summary calculation for a specific column in a report.
 * This class supports different types of summary calculations such as sum, average, and count,
 * and allows filtering data based on a condition and an operator.
 *
 * The calculation can be performed on a column's values with an optional condition and operator
 * to filter the data before calculating the summary.
 *
 * @property columnName The name of the column to calculate the summary for.
 * @property type The type of the summary calculation (e.g., SUM, AVG, COUNT).
 * @property operator The operator used to filter the data (e.g., greater than, equal to).
 * @property targetValue The target value for filtering the data based on the operator.
 */
data class SummaryCalculation(val columnName: String, val type: SummaryCalculationType, val operator: Operator? = null, val targetValue: Int? = null) {

    /**
     * This constructor creates a `SummaryCalculation` based on the condition, targetValue pair provided,
     * although it can be null by default.
     *
     * @param columnName the name of the column to calculate the summary for.
     * @param type the type of the summary calculation (e.g., SUM, AVG, COUNT).
     * @param condition the condition to filter the data, represented as a pair of an operator and an integer value.
     */
    constructor(columnName: String, type: SummaryCalculationType, condition: Pair<Operator, Int>?) : this(
        columnName,
        type,
        condition?.first,
        condition?.second
    )

    /**
     * This constructor creates a `SummaryCalculation` based on the specified condition literal.
     *
     * The literal must be of valid syntax (ex. "operator target") where operator is an operator,
     * contained in the Operator enum, and the target is a number (ex. >= 5)
     *
     * @param columnName the name of the column to calculate the summary for.
     * @param type the type of the summary calculation (e.g., SUM, AVG, COUNT).
     * @param condition the condition string to filter the data (e.g., "> 10" or "<= 5").
     */
    constructor(columnName: String, type: SummaryCalculationType, condition: String) : this(
        columnName,
        type,
        Operator.fromCondition(condition)
    )

    /**
     * Calculates the values based on the provided data, function type (SUM, AVG, COUNT) and the specified condition.
     *
     * The calculation is performed on the column data, and if an operator and target value are provided,
     * the data is filtered before performing the calculation.
     *
     * If the column name provided doesn't exist in the data map, 0 will be returned.
     *
     * @param data the data map containing the column names as keys and the values as lists of strings.
     * @return the calculated value based on the type of summary calculation.
     */
    fun calculateValues(data: Map<String, List<String>>) : Number {
        val targetCells = data[this.columnName] ?: return 0;
        val filteredCells = if (this.operator != null && this.targetValue != null) {
            targetCells.filter {
                val number = it.toIntOrNull()
                if (number != null) {
                    this.operator.predicate(number, this.targetValue)
                } else {
                    false
                }
            }
        } else {
            targetCells
        }

        return when (this.type) {
            SummaryCalculationType.SUM -> filteredCells.mapToInt().sum()
            SummaryCalculationType.AVG -> filteredCells.mapToInt().average()
            SummaryCalculationType.COUNT -> filteredCells.count()
        }
    }

    private fun Iterable<String>.mapToInt() : List<Int> {
        return mapNotNull { it.toIntOrNull() }
    }

    override fun toString(): String {
        val condition = (if (this.operator != null) ", Uslov: ${this.operator.operator} ${this.targetValue}" else "")
        return "Kalkulacija (Kolona ${this.columnName}, Tip ${this.type}" + condition + ")"
    }

    /**
     * Enum class representing the type of the summary calculation.
     */
    enum class Operator(val operator: String, val predicate: (Int, Int) -> Boolean) {

        GREATER(">", { a, b -> a > b }),
        GREATER_EQUAL(">=", { a, b -> a >= b }),
        LESSER("<", { a, b -> a < b }),
        LESS_EQUAL("<=", { a, b -> a <= b }),
        EQUAL("==", { a, b -> a == b }),
        NOT_EQUAL("!=", { a, b -> a != b });

        /**
         * Companion object for utility functions related to the `Operator` enum.
         */
        companion object {

            /**
             * Returns the operator corresponding to the provided string representation.
             *
             * @param type the string representing the operator (e.g., ">", "<=").
             * @return The corresponding `Operator` enum value or `null` if no match is found.
             */
            fun fromString(type: String): Operator? {
                for (operator in entries) {
                    if (operator.operator.equals(type, true)) {
                        return operator
                    }
                }

                return null
            }

            /**
             * Returns the operator and the value based on the provided condition.
             *
             * @param condition condition to be used for getting the operator and the value.
             * @return the filtering operator and the value based on the provided condition.
             *
             * @throws RuntimeException If the operator is not found or target value is not an int
             */
            @Throws(RuntimeException::class)
            fun fromCondition(condition: String): Pair<Operator, Int> {
                val trimmed = condition.trim()
                entries.forEach { operator ->
                    if (trimmed.startsWith(operator.operator)) {
                        val valuePart = trimmed.removePrefix(operator.operator).trim()
                        val value = valuePart.toIntOrNull()
                        if (value != null) {
                            return operator to value
                        }
                    }
                }

                throw RuntimeException("Failed to find operator or convert target to int: $condition")
            }
        }

    }

}