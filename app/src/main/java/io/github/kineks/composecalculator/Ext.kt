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
    text: String,
    offset: Int = 0,
    callback: (index: Int, text: String,last: String?, OneChar: Boolean, cursorHide: Boolean, cursorInsert: Boolean) -> Unit = { _, _, _, _, _,_ -> }
): TextFieldValue {

    if (text.isEmpty()&&this.text.isEmpty()) return this

    /**     不确定，看源码文档说得有点迷糊                                  */
    /**     为啥想从光标位置插入文本还得自己判断状态，还没法判断光标是否激活      */
    //  由于 TextRange 索引是从 1 开始，0 代表光标被隐藏
    //  所以实际和 String 的 index 偏移 1
    var index: Int
    val stringBuilder = when (true) {
        //  光标隐藏
        (selection.max == 0) -> {
            (this.text + text).apply {
                index = length
                callback(index, this@add.text, this@add.text.lastOrNull()?.toString(),text.length == 1,true,false)
            }
        }
        //  光标插入
        selection.collapsed -> {
            StringBuilder(this.text).insert(selection.max, text).apply {
                index = selection.max + text.length
                callback(index, this@add.text,
                    this@add.text[selection.max-1].toString(),text.length == 1,false,true)
            }
        }
        //  选中多个
        else -> {
            index = selection.min + 1
            callback(index, this.text, this.text[selection.min].toString(),text.length == 1,false,false)
            this.text.replaceRange(
                startIndex = selection.min,
                endIndex = selection.max,
                text
            )
        }
    }.toString()

    if (text.isEmpty()) return this
    return copy(
        text = stringBuilder,
        selection = TextRange(index + offset)
    )
}

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
