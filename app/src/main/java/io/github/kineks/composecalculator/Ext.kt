package io.github.kineks.composecalculator

import androidx.annotation.StringRes
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import io.github.kineks.composecalculator.data.MathParser

/**
 * 根据光标状态插入文本
 * @return TextFieldValue
 */
fun TextFieldValue.append(text: String,offset: Int = 0): TextFieldValue {

    // 由于 TextRange 索引是从 1 开始，0 代表光标被隐藏
    // 所以实际和 String 的 index 偏移 1
    var index: Int
    val stringBuilder = when(true) {
        // 光标隐藏
        (selection.max == 0) -> {
            (this.text + text).apply {
                index = length
            }
        }
        // 光标插入
        selection.collapsed -> {
            StringBuilder(this.text).insert(selection.max,text).apply {
                index = selection.max + text.length
            }
        }
        // 选中多个
        else -> {
            index = selection.min + 1
            this.text.replaceRange(
                startIndex = selection.min,
                endIndex = selection.max,
                text
            )
        }
    }

    return copy(
        text = stringBuilder.toString(),
        selection = TextRange(index + offset)
    )
}

fun String.replaceIfEqual(oldValue: String?,newValue: String) = let {
    (if (it == oldValue) it.substring(
        0,
        it.lastIndex
    ) else it) + newValue
}

fun String.parse(): Double = this
    .replace("−","-")
    .replace("×","*")
    .replace("÷","/")
    .replace("（","(")
    .replace("）",")")
    .replace("(","(")
    .replace(")",")")
    .let { MathParser().parse(it) }

/**
 * 使用正则表达式去掉多余的 ‘.’ 与 ‘0’
 * @return
 */
fun String.subZeroAndDot(): String {
    var s = this
    if (s.indexOf(".") > 0) {
        s = s.replace("0+?$".toRegex(), "") //去掉多余的0
        s = s.replace("[.]$".toRegex(), "") //如最后一位是.则去掉
    }
    return s
}

fun getString(@StringRes id: Int) = App.context.getString(id)

val OperatorMap = mutableMapOf(
    Pair("+", Operator.plus),
    Pair("-", Operator.minus),
    Pair("−", Operator.minus),
    Pair("*", Operator.times),
    Pair("×", Operator.times),
    Pair("/", Operator.div),
    Pair("÷", Operator.div),
    Pair("%", Operator.rem),
    Pair("=", Operator.equal),
)

enum class Operator {
    plus,   //  +  加
    minus,  //  -  减
    times,  //  *  乘
    div,    //  /  除
    rem,    //  %  求余
    equal   //  =  等于，如果是缺省值则返回数值本身
}