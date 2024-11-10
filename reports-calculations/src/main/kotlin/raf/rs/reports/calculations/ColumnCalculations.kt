package raf.rs.reports.calculations

data class ColumnCalculations(val columnName: String, val columns: List<String>, val operator: Char) {

    fun calculateValues(data: Map<String, List<String>>): List<String> {
        this.checkColumnsNum()

        val calculations: MutableList<String> = ArrayList()
        val size = data.values.maxOfOrNull { it.size } ?: 0
        for (i in 0 until size) {
            val valuesFrom = this.getValuesFrom(data, this.columns, i)
            if (valuesFrom.isEmpty()) {
                calculations.add("")
                continue
            }

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

    private fun checkColumnsNum() {
        if (this.operator == '-' || this.operator == '/' && this.columns.size > 2) {
            throw RuntimeException("Calculation of this type can only be applied to 2 columns")
        }
    }

    override fun toString(): String {
        return "Kalkulacija (Kolona = ${this.columnName}, Kolone = (${this.columns}), Operator = ${this.operator})"
    }

}