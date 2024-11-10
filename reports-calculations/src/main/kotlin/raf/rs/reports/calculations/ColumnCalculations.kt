package raf.rs.reports.calculations

data class ColumnCalculations(val columnName: String, val columns: List<String>, val operator: Char) {
    /**
     * Calculates the values based on the provided data.
     *
     * @param data Data to be used for calculating the values.
     * @return List of calculations.
     * @throws IllegalStateException if the operator is not supported.
     */
    fun calculateValues(data: Map<String, List<String>>): List<String> {
        this.checkColumnsNum()
        /**
         * Holds the calculations.
         */
        val calculations: MutableList<String> = ArrayList()
        /**
         * Holds the size of the data.
         */
        val size = data.values.maxOfOrNull { it.size } ?: 0
        for (i in 0 until size) {
            /**
             * Holds the values from the columns.
             */
            val valuesFrom = this.getValuesFrom(data, this.columns, i)
            if (valuesFrom.isEmpty()) {
                calculations.add("")
                continue
            }
            /**
             * Holds the initial value.
             */
            var initialValue: Int = valuesFrom.first()
            for (j in 1 until valuesFrom.size) {

                val currentValue = valuesFrom[j]
                when (this.operator) {
                    '+' -> initialValue += currentValue
                    '-' -> initialValue -= currentValue
                    '*' -> initialValue *= currentValue
                    '/' -> initialValue /= currentValue
                    else -> throw IllegalStateException("Unexpected operator: " + this.operator)
                }
            }

            calculations.add(initialValue.toString())
        }

        return calculations
    }
    /**
     * Returns the values from the data based on the provided columns and index.
     *
     * @param data Data to be used for getting the values.
     * @param columns Columns to be used for getting the values.
     * @param index Index of the values.
     * @return List of values.
     */
    private fun getValuesFrom(data: Map<String, List<String>>, columns: List<String>, index: Int): List<Int> {
        /**
         * Holds the values.
         */
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
     * Checks the number of columns because some calculations are applicable to only 2 columns .
     * @throws RuntimeException if the number of columns is greater than 2.
     */
    private fun checkColumnsNum() {
        if (this.operator == '-' || this.operator == '/' && this.columns.size > 2) {
            throw RuntimeException("Calculation of this type can only be applied to 2 columns")
        }
    }
    /**
     * Returns the string representation of the object.
     *
     * @return String representation of the object.
     */
    override fun toString(): String {
        return "Kalkulacija (Kolona = ${this.columnName}, Kolone = (${this.columns}), Operator = ${this.operator})"
    }

}