package raf.rs.reports.calculations

import kotlin.jvm.Throws

/**
 * Data class representing calculations performed on columns in the provided dataset and bound
 * to a new column with the specified name.
 *
 * The calculations are based on the operator and column values.
 *
 * @property columnName Name of the column where the result will be stored.
 * @property columns List of columns to be used for the calculations.
 * @property operator Operator to be used for the calculation between columns (e.g., '+', '-', '*', '/').
 */
data class ColumnCalculations(val columnName: String, val columns: List<String>, val operator: Char) {

    /**
     * Calculates the values based on the provided data using the specified operator between column values.
     *
     * This method checks that the number of columns is valid for the calculation and performs the appropriate
     * arithmetic operation between values in the specified columns for each row of the data.
     *
     * @param data data to be used for calculating the values, where keys represent column names and values are lists of strings.
     * @return list of calculated results for each row, represented as strings.
     *
     * @throws ArithmeticException if the operator is not supported of if the number of columns exceeds the limit for the current operation
     */
    @Throws(ArithmeticException::class)
    fun calculateValues(data: Map<String, List<String>>): List<String> {
        this.checkColumnsNum()

        val calculations: MutableList<String> = ArrayList()
        val maxSize = data.values.maxOfOrNull { it.size } ?: 0
        for (i in 0 until maxSize) {
            val valuesFrom = this.getValuesFrom(data, this.columns, i)
            if (valuesFrom.isEmpty()) {
                calculations.add("")
                continue
            }

            var initialValue = valuesFrom.first()
            for (j in 1 until valuesFrom.size) {
                val currentValue = valuesFrom[j]
                when (this.operator) {
                    '+' -> initialValue += currentValue
                    '-' -> initialValue -= currentValue
                    '*' -> initialValue *= currentValue
                    '/' -> initialValue /= currentValue
                    else -> throw ArithmeticException("Unexpected operator: " + this.operator)
                }
            }

            calculations.add(initialValue.toString())
        }

        return calculations
    }

    private fun getValuesFrom(data: Map<String, List<String>>, columns: List<String>, index: Int): List<Int> {
        val values: MutableList<Int> = ArrayList()
        for (column in columns) {
            val columnValues = data[column] ?: continue
            if (index >= columnValues.size) {
                continue
            }

            val value = columnValues[index]
            try {
                value.let { values.add(it.toInt()) }
            } catch (e: NumberFormatException) {
                continue
            }
        }

        return values
    }

    /**
     * Checks the number of columns, ensuring that the calculation is valid for the given operator.
     * Specifically, some calculations can only be applied to exactly two columns.
     *
     * @throws ArithmeticException if the number of columns exceeds the limit for the current operation.
     */
    @Throws(ArithmeticException::class)
    private fun checkColumnsNum() {
        if (this.operator == '-' || this.operator == '/' && this.columns.size > 2) {
            throw ArithmeticException("Calculation of this type can only be applied to 2 columns")
        }
    }

    override fun toString(): String {
        return "Kalkulacija (Kolona = ${this.columnName}, Kolone = (${this.columns}), Operator = ${this.operator})"
    }

}