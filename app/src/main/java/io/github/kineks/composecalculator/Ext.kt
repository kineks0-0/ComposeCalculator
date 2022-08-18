package io.github.kineks.composecalculator

import androidx.annotation.StringRes
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import io.github.kineks.composecalculator.data.MathParser


/**
 * 根据光标状态插入文本
 * @return TextFieldValue
 */
fun TextFieldValue.add(
    string: String,
    offset: Int = 0
): TextFieldValue {

    // 两边都是空就没必要继续的必要了
    if (string.isEmpty() && text.isEmpty()) return this

    /**     看源码文档貌似没有光标状态，只有一个表示选择范围的数值              */
    /**     为啥想从光标位置插入文本还得自己判断状态，还没法判断光标是否激活      */
    //  由于 TextRange 索引 0 实际是从 输入框左边 开始,而且也没法获取光标是否显示
    //  如果需要正常输入就没法在左边插入文本了

    var index: Int
    val stringBuilder = when (true) {

        //  光标隐藏 || 光标默认状态
        cursorHide -> {
            (text + string).apply { index = length }
        }

        //  光标插入
        cursorInsert -> {
            StringBuilder(text).insert(selection.max, string).apply {
                index = selection.max + string.length
            }
        }

        //  选中多个
        else -> {
            index = selection.min + 1
            text.replaceRange(
                startIndex = selection.min,
                endIndex = selection.max,
                string
            )
        }
    }

    return copy(
        text = stringBuilder.toString(),
        selection = TextRange(index + offset)
    )
}

val TextFieldValue.cursorWhere: Int
    get() = when (true) {
        cursorHide -> text.length
        cursorInsert -> selection.max
        else -> selection.min + 1
    }
val TextFieldValue.cursorHide: Boolean get() = selection.max == 0
val TextFieldValue.cursorInsert: Boolean get() = selection.collapsed
val TextFieldValue.cursorSelection: Boolean get() = !selection.collapsed


/**
 * 规范化算式并返回计算结果
 */
fun String.parse(): Double = this
    .replace("−", "-")
    .replace("×", "*")
    .replace("÷", "/")
    .replace("（", "(")
    .replace("）", ")")
    .replace("(", "(")
    .replace(")", ")")
    .let { MathParser().parse(it) }

/**
 * 使用正则表达式去掉多余的 ‘.’ 与 ‘0’
 * @return String
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
