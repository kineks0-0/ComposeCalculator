package io.github.kineks.composecalculator.ui.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import io.github.kineks.composecalculator.append

class CalculatorTextFieldState(
    private var _textFieldValue: MutableState<TextFieldValue>,
    private var _textFieldLabel: MutableState<String>,
    val isError: MutableState<Boolean>,
    val textFieldCursorEnabled: (CalculatorTextFieldState) -> Boolean,
    val onDone: (KeyboardActionScope.(CalculatorTextFieldState) -> Unit)
) {
    var value
        get() = _textFieldValue.value
        set(value) {
            _textFieldValue.value = value
        }
    var label
        get() = _textFieldLabel.value
        set(value) {
            _textFieldLabel.value = value
        }

    fun isError() {
        isError.value = true
    }

    fun isNoError() {
        isError.value = false
    }

    fun append(
        text: String,
        offset: Int = 0,
        callback: (index: Int, text: String, last: String?, OneChar: Boolean, cursorHide: Boolean, cursorInsert: Boolean) -> Unit = { _, _, _, _, _, _ -> }
    ) {
        label = ""
        isNoError()
        if (value.text == "0") {
            if (text.isNumber()) setTextField("")
            if (text.lastOrNull() == '(') setTextField("")
            if (text.lastOrNull() == ')') setTextField("")
        }
        _textFieldValue.value = _textFieldValue.value.append(text, offset, callback)
    }

    fun checkCursor(
        offset: Int = 0,
        callback: (index: Int, text: String, last: String?, OneChar: Boolean, cursorHide: Boolean, cursorInsert: Boolean) -> Unit = { _, _, _, _, _, _ -> }
    ) {
        _textFieldValue.value.append("", offset, callback)
    }

    fun deleteTextField() {
        when (value.text.length) {
            0 -> {}
            1 -> {
                clearTextField()
                value = value.copy(selection = TextRange(0))
            }
            else -> {
                checkCursor { index, _, _, _, _, _ ->
                    setTextField(
                        value.text.substring(
                            0, value.text.lastIndex
                        ),
                        TextRange(index)
                    )
                }

            }
        }
        label = ""
    }

    fun clearTextField() {
        isNoError()
        _textFieldValue.value = TextFieldValue("0")
        _textFieldLabel.value = ""
        operatorArithmeticBracketsStartCounts = 0
    }

    fun setTextField(text: String, index: TextRange = value.selection) {
        isNoError()
        _textFieldValue.value = TextFieldValue(
            text = text, selection = index//TextRange(text.length)
        )
    }

}

@Composable
fun rememberCalculatorTextFieldState(
    textFieldValue: TextFieldValue = TextFieldValue("0"),
    textFieldLabel: String = "",
    isError: Boolean = false,
    cursorEnabled: (CalculatorTextFieldState) -> Boolean = { it.value.text.isNotEmpty() },
    onDone: (KeyboardActionScope.(CalculatorTextFieldState) -> Unit) = { }
) = rememberSaveable(saver = Saver(save = {
    arrayOf(it.value.text, it.label, it.isError.value.toString())
}, restore = {
    CalculatorTextFieldState(
        mutableStateOf(TextFieldValue(it[0])),
        mutableStateOf(it[1]),
        mutableStateOf(it[2].toBooleanStrict()),
        cursorEnabled,
        onDone
    )
})) {
    CalculatorTextFieldState(
        mutableStateOf(textFieldValue),
        mutableStateOf(textFieldLabel),
        mutableStateOf(isError),
        cursorEnabled,
        onDone
    )
}

@Suppress("OPT_IN_IS_NOT_ENABLED")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalUnitApi::class, ExperimentalComposeUiApi::class)
@Composable
fun CalculatorTextField(
    state: CalculatorTextFieldState, modifier: Modifier = Modifier
) {
    val keyboard = LocalSoftwareKeyboardController.current
    keyboard?.hide()
    state.apply {
        TextField(
            value = value,
            onValueChange = {
                if (it.text.length < value.text.length) {
                    // 判断如果删除的是最后一位则尝试修正计数
                    val itLast = it.text.lastOrNull()?.toString()
                    val last = value.text.lastOrNull()?.toString()
                    if (itLast != last && last?.isOperatorBracketStart() == true) operatorArithmeticBracketsStartCounts--
                    if (itLast != last && last?.isOperatorBracketEnd() == true) operatorArithmeticBracketsStartCounts++
                }
                value = it
            },// todo: 对比差异值 避免键盘删除括号导致括号计数异常
            label = {
                Text(text = label,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .alpha(0.8f)
                        .clickable {
                            // 点击可以将 Label 里的算式重新放回 输入框
                            if (label.isNotEmpty()) setTextField(label)
                        })
            },
            isError = state.isError.value,
            keyboardActions = KeyboardActions(onDone = { onDone(state) }),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
            modifier = modifier.fillMaxWidth(),
            // 根据文本长度调整大小，从而显示效果类似 TextView 的 AutoSize
            textStyle = TextStyle(
                fontSize = TextUnit(
                    50f + 80f / (value.text.length / 6 + 1), TextUnitType.Sp
                ), textAlign = TextAlign.End
            ),
            colors = TextFieldDefaults.textFieldColors(

                textColor =
                if (state.isError.value)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.onSurface,

                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                cursorColor =
                if (textFieldCursorEnabled(state))
                    MaterialTheme.colorScheme.primary
                else
                    Color.Transparent,

                //errorCursorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                //errorIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedLeadingIconColor = Color.Transparent,
                unfocusedLeadingIconColor = Color.Transparent,
                disabledLeadingIconColor = Color.Transparent,
                errorLeadingIconColor = Color.Transparent,
                focusedTrailingIconColor = Color.Transparent,
                unfocusedTrailingIconColor = Color.Transparent,
                disabledTrailingIconColor = Color.Transparent,
                errorTrailingIconColor = Color.Transparent,
                containerColor = Color.Transparent,

                // Label 颜色
                focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurface,
                disabledLabelColor = MaterialTheme.colorScheme.onSurface,
                //errorLabelColor = MaterialTheme.colorScheme.onSurface,

                placeholderColor = Color.Transparent,
                disabledPlaceholderColor = Color.Transparent
            )
        )
    }

}