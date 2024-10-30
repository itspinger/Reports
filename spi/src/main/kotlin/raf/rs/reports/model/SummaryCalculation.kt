package raf.rs.reports.model

data class SummaryCalculation(val columnName: String, val type: SummaryCalculationType, val operator: Operator? = null, val targetValue: Int? = null) {

    constructor(columnName: String, type: SummaryCalculationType, condition: Pair<Operator, Int>?) : this(
        columnName,
        type,
        condition?.first,
        condition?.second
    )

    constructor(columnName: String, type: SummaryCalculationType, condition: String) : this(
        columnName,
        type,
        Operator.fromCondition(condition)
    )

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

    private fun Iterable<String>.mapToInt() : List<Int> {
        return mapNotNull { it.toIntOrNull() }
    }

    enum class Operator(val operator: String, val predicate: (Int, Int) -> Boolean) {

        GREATER(">", { a, b -> a > b }),
        GREATER_EQUAL(">=", { a, b -> a >= b }),
        LESSER("<", { a, b -> a < b }),
        LESS_EQUAL("<=", { a, b -> a <= b }),
        EQUAL("==", { a, b -> a == b }),
        NOT_EQUAL("!=", { a, b -> a != b });

        companion object {
            fun fromString(type: String): Operator? {
                for (operator in entries) {
                    if (operator.operator.equals(type, true)) {
                        return operator
                    }
                }

                return null
            }

            fun fromCondition(condition: String): Pair<Operator, Int>? {
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

                return null
            }
        }

    }

}