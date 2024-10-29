package raf.rs.reports.model

data class SummaryCalculation(val columnName: String, val type: SummaryCalculationType, val condition: String?) {
    fun calculateValues(data: Map<String, List<String>>): Number {
        var result = 0;
        if (condition != null) {
            val operator = condition.split("""[><=!]=?""".toRegex())[0]
            val value = condition.split("""[><=!]=?""".toRegex())[1]
            println(operator + value);
            for (i in data.get(columnName)!!) {
                if(type == SummaryCalculationType.COUNT)
                {
                    when (operator) {
                        ">" -> if (i.toInt() > value.toInt()) result++
                        "<" -> if (i.toInt() < value.toInt()) result++
                        ">=" -> if (i.toInt() >= value.toInt()) result++
                        "<=" -> if (i.toInt() <= value.toInt()) result++
                        "==" -> if (i.toInt() == value.toInt()) result++
                        "!=" -> if (i.toInt() != value.toInt()) result++
                    }
                }
                else if(type==SummaryCalculationType.SUM){
                    when (operator) {
                        ">" -> if (i.toInt() > value.toInt()) result+=i.toInt()
                        "<" -> if (i.toInt() < value.toInt()) result+=i.toInt()
                        ">=" -> if (i.toInt() >= value.toInt()) result+=i.toInt()
                        "<=" -> if (i.toInt() <= value.toInt()) result+=i.toInt()
                        "==" -> if (i.toInt() == value.toInt()) result+=i.toInt()
                        "!=" -> if (i.toInt() != value.toInt()) result+=i.toInt()
                    }
                }
                else if(type==SummaryCalculationType.AVG){
                    when (operator) {
                        ">" -> if (i.toInt() > value.toInt()) data.get(columnName)!!.sumOf { it.toInt() } / data.get(columnName)!!.size
                        "<" -> if (i.toInt() < value.toInt()) data.get(columnName)!!.sumOf { it.toInt() } / data.get(columnName)!!.size
                        ">=" -> if (i.toInt() >= value.toInt()) data.get(columnName)!!.sumOf { it.toInt() } / data.get(columnName)!!.size
                        "<=" -> if (i.toInt() <= value.toInt()) data.get(columnName)!!.sumOf { it.toInt() } / data.get(columnName)!!.size
                        "==" -> if (i.toInt() == value.toInt()) data.get(columnName)!!.sumOf { it.toInt() } / data.get(columnName)!!.size
                        "!=" -> if (i.toInt() != value.toInt()) data.get(columnName)!!.sumOf { it.toInt() } / data.get(columnName)!!.size
                    }
                }

            }
        }
        else
        {
            for (i in data.get(columnName)!!) {
                when (type) {
                    SummaryCalculationType.SUM -> result += i.toInt()
                    SummaryCalculationType.AVG -> result++;
                    SummaryCalculationType.COUNT -> result = data.get(columnName)!!.sumOf { it.toInt() } / data.get(columnName)!!.size
                }
            }
        }
        return result
    }
}


