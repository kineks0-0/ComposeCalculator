package io.github.kineks.composecalculator.ui.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
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
import io.github.kineks.composecalculator.*

class CalculatorTextFieldState(
    private var _textFieldValue: MutableState<TextFieldValue>,
    private var _textFieldLabel: MutableState<String>,
    val isError: MutableState<Boolean>,
    val interactionSource: MutableInteractionSource,
    val cursorEnabled: CalculatorTextFieldState.() -> Boolean,
    val cursorHide: CalculatorTextFieldState.() -> Boolean,
    val onValueChange: CalculatorTextFieldState.(TextFieldValue) -> Unit,
    val onDone: (KeyboardActionScope.(CalculatorTextFieldState) -> Unit)
) {

    var value: TextFieldValue
        get() = _textFieldValue.value
        set(value) { _textFieldValue.value = value }
    var label
        get() = _textFieldLabel.value
        set(value) { _textFieldLabel.value = value }

    val last: String? get() = when(true) {
        value.text.isEmpty() -> null
        (value.text.length == 1) -> value.text
        else -> value.text[value.cursorWhere(cursorHide())-1].toString()
    }
    val lastSecond: String? get() {
        return if (value.text.length == 1)
            null
        else
            value.text[value.cursorWhere(cursorHide())-2].toString()
    }


    fun isError() {
        isError.value = true
    }

    private fun isNoError() {
        isError.value = false
    }
    private fun restState() {
        label = ""
        isNoError()
    }

    fun add(text: String, deleteLast: Boolean) {
        if (deleteLast)
            deleteLast()
        add(text)
    }

    fun add(text: String, offset: Int = 0, cursorHide: Boolean = cursorHide()) {
        restState()
        if (value.text == "0") {
            if (text.isNumber() && text != ".") setTextField("")
            if (text.lastOrNull() == '(') setTextField("")
            if (text.lastOrNull() == ')') setTextField("")
            if (text.lastOrNull() == 'e') setTextField("")
            if (text.lastOrNull() == 'i') setTextField("")
        }
        value = value.add(text, offset, cursorHide)
    }

    fun deleteLast() {
        when (value.text.length) {
            0 -> {}
            1 -> clearTextField("")
            else -> {
                if (value.cursorInsert)
                    setTextField(
                        StringBuilder(value.text)
                            .deleteAt(value.cursorWhere(cursorHide())-1)
                            .toString(),
                        TextRange(value.cursorWhere(cursorHide()))
                    )
                if (value.cursorSelection)
                    setTextField(
                        value.text.replaceRange(
                            startIndex = value.selection.min,
                            endIndex = value.selection.max,
                            ""
                        ),
                        index = TextRange(value.selection.min + 1)
                    )
                onValueChange(value)
            }
        }
        restState()
    }

    fun clearTextField(defValue: String = "0") {
        restState()
        value = TextFieldValue(defValue,TextRange(defValue.length))
        operatorBracketsCounts = 0
    }

    fun setTextField(text: String, index: TextRange = value.selection) {
        isNoError()
        _textFieldValue.value = TextFieldValue(
            text = text, selection = index
        )
    }

}

@Composable
fun rememberCalculatorTextFieldState(
    textFieldValue: TextFieldValue = TextFieldValue("0"),
    textFieldLabel: String = "",
    isError: Boolean = false,
    interactionSource: MutableInteractionSource = MutableInteractionSource(),
    cursorEnabled: CalculatorTextFieldState.() -> Boolean = { value.text.isNotEmpty() },
    cursorHide: CalculatorTextFieldState.() -> Boolean = { value.cursorHide },
    onValueChange: CalculatorTextFieldState.(TextFieldValue) -> Unit = { },
    onDone: (KeyboardActionScope.(CalculatorTextFieldState) -> Unit) = { }
) = rememberSaveable(saver = Saver(save = {
    arrayOf(it.value.text, it.label, it.isError.value.toString())
}, restore = {
    CalculatorTextFieldState(
        mutableStateOf(TextFieldValue(it[0])),
        mutableStateOf(it[1]),
        mutableStateOf(it[2].toBooleanStrict()),
        interactionSource,
        cursorEnabled,
        cursorHide,
        onValueChange,
        onDone
    )
})) {
    CalculatorTextFieldState(
        mutableStateOf(textFieldValue),
        mutableStateOf(textFieldLabel),
        mutableStateOf(isError),
        interactionSource,
        cursorEnabled,
        cursorHide,
        onValueChange,
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
                state.onValueChange(it)
                value = it
            },
            label = {
                Text(
                    text = if (label.isNotEmpty()) "$label =" else label,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .alpha(0.8f)
                        .clickable {
                            // 点击可以将 Label 里的算式重新放回 输入框
                            if (label.isNotEmpty()) {
                                setTextField(label)
                                label = ""
                            }
                        })
            },
            isError = state.isError.value,
            keyboardActions = KeyboardActions(onDone = { onDone(state) }),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            interactionSource = interactionSource,
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
                if (cursorEnabled())
                    MaterialTheme.colorScheme.primary
                else
                    Color.Transparent,

                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
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

                placeholderColor = Color.Transparent,
                disabledPlaceholderColor = Color.Transparent
            )
        )
    }

}