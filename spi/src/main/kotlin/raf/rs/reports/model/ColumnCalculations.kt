package raf.rs.reports.model

data class ColumnCalculations(val columnName: String, val columns: List<String>, val operator: Char) {

    fun calculateValues(data: MutableMap<String, List<String>>): List<String> {
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

    private fun getValuesFrom(data: MutableMap<String, List<String>>, columns: List<String>, index: Int): List<Int> {
        val values: MutableList<Int> = ArrayList()
        for (column in columns) {
            val columnValues = data[column]!!
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

}