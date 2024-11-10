package raf.rs.reports.calculations

data class SummaryCalculation(val columnName: String, val type: SummaryCalculationType, val operator: Operator? = null, val targetValue: Int? = null) {
    /**
     * Constructor for the SummaryCalculation class.
     *
     * @param columnName Name of the column.
     * @param type Type of the summary calculation.
     * @param condition Condition for the summary calculation.
     */
    constructor(columnName: String, type: SummaryCalculationType, condition: Pair<Operator, Int>?) : this(
        columnName,
        type,
        condition?.first,
        condition?.second
    )
    /**
     * Constructor for the SummaryCalculation class.
     *
     * @param columnName Name of the column.
     * @param type Type of the summary calculation.
     * @param condition Condition for the summary calculation.
     */
    constructor(columnName: String, type: SummaryCalculationType, condition: String) : this(
        columnName,
        type,
        Operator.fromCondition(condition)
    )
    /**
     * Calculates the values based on the provided data.
     *
     * @param data Data to be used for calculating the values.
     * @return Calculated value.
     */
    fun calculateValues(data: Map<String, List<String>>) : Number {
        var result = 0;

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
    /**
     * Maps the list of strings to the list of integers.
     *
     * @return List of integers.
     */
    private fun Iterable<String>.mapToInt() : List<Int> {
        return mapNotNull { it.toIntOrNull() }
    }
    /**
     * Returns the string representation of the SummaryCalculation object.
     *
     * @return String representation of the SummaryCalculation object.
     */
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
         * Companion object that handles operators
         */
        companion object {
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
             * @param condition Condition to be used for getting the operator and the value.
             * @return Operator and the value based on the provided condition.
             * @throws RuntimeException If the operator is not found.
             */
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

                throw RuntimeException("Failed to find operator: $condition")
            }
        }

    }

}