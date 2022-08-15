package io.github.kineks.composecalculator.data

import io.github.kineks.composecalculator.data.NumberValue.Operator.*

const val TAG = "NumberValue"

class NumberValue(
    value: String,
    val sign: Operator
) {

    val value = value.toDouble()

    fun Count(
        numberValue: Double,
        sign: Operator
    ): Double {
        return when (sign) {
            plus -> numberValue + value
            minus -> numberValue - value
            times -> numberValue * value
            div -> numberValue / value
            rem -> numberValue % value
            equal -> numberValue
        }
    }

    companion object {
        val OperatorMap = mutableMapOf(
            Pair("+", plus),
            Pair("-", minus),
            Pair("*", times),
            Pair("×", times),
            Pair("/", div),
            Pair("÷", div),
            Pair("%", rem),
        )
    }


    enum class Operator {
        plus,   //  +  加
        minus,  //  -  减
        times,  //  *  乘
        div,    //  /  除
        rem,    //  %  求余
        equal   //  =  等于，如果是缺省值则返回数值本身
    }
}

fun String.eval(): Number {
    var indexOfNumberStart = -1
    var indexOfNumberEnd = -1
    val numberValues = mutableListOf<NumberValue>()
    forEachIndexed { index, c ->
        val operator = NumberValue.OperatorMap[c.toString()]
        if (operator != null) {
            numberValues.add(
                NumberValue(
                    substring(indexOfNumberStart, indexOfNumberEnd),
                    operator
                )
            )
            indexOfNumberStart = -1
            indexOfNumberEnd = -1
        }
        else
            when (c) {
                '1', '2', '3', '4', '5', '6', '7', '8', '9', '0' -> {
                    indexOfNumberEnd = index + 1
                    if (indexOfNumberStart == -1)
                        indexOfNumberStart = index
                    if (index == lastIndex)
                        numberValues.add(
                            NumberValue(
                                substring(indexOfNumberStart, indexOfNumberEnd),
                                equal
                            )
                        )
                }

            }
    }
    return eval(numberValues)
}

fun String.toSign(): NumberValue.Operator = NumberValue.OperatorMap[this] ?: equal

fun Number.make(sign: String): NumberValue =
    NumberValue(
        value = toString(),
        sign = sign.toSign()
    )

fun eval(numberValues: List<NumberValue>): Number {
    var number = 0.0

    for (index in numberValues.indices) {
        val firstNumber = when (index) {
            0 -> null
            else -> numberValues[index - 1]
        }
        val secondNumber = numberValues[index]

        number = if (firstNumber != null) {
            secondNumber.Count(number, firstNumber.sign)
        } else {
            secondNumber.value
        }

        println(TAG + ": index $index of ${numberValues.size - 1} = $number")
        println(TAG + ": firstNumber: ${firstNumber?.value}  ${firstNumber?.sign}")
        println(TAG + ": secondNumber: ${secondNumber.value}  ${secondNumber.sign}")
    }

    return number
}
