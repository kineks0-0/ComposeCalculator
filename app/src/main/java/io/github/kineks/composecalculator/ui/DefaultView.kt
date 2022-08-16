package io.github.kineks.composecalculator.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import io.github.kineks.composecalculator.R
import io.github.kineks.composecalculator.getString
import io.github.kineks.composecalculator.parse
import io.github.kineks.composecalculator.subZeroAndDot
import io.github.kineks.composecalculator.ui.layout.TheLayout
import io.github.kineks.composecalculator.ui.layout.isHorizontal
import io.github.kineks.composecalculator.ui.layout.isNotHorizontal
import io.github.kineks.composecalculator.ui.theme.ComposeCalculatorTheme
import io.github.kineks.composecalculator.ui.view.*


@Composable
fun DefaultView() {
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = !isSystemInDarkTheme()
    SideEffect {
        systemUiController.setStatusBarColor(Color.Transparent, useDarkIcons)
    }

    var colorDisplay by remember {
        mutableStateOf(false)
    }

    val startCalculatingEquations: (CalculatorTextFieldState) -> Unit by remember {
        mutableStateOf({ textFieldState ->
            textFieldState.apply {
                try {
                    if (value.text == getString(R.string.data_error)) {
                        clearTextField()
                    } else {
                        // 如果括号没打完
                        if (operatorArithmeticBracketsStartCounts != 0) {
                            while (operatorArithmeticBracketsStartCounts != 0) {
                                textFieldState.append(")")
                                operatorArithmeticBracketsStartCounts--
                            }
                        }
                        // 算式显示到 label
                        label = value.text
                        // 开始计算
                        val number = value.text.parse().toString()
                        setTextField(
                            number.subZeroAndDot()
                        )
                        if (number == "NaN") {
                            textFieldState.isError()
                        }
                    }
                } catch (e: Exception) {
                    setTextField(getString(R.string.data_error))
                    textFieldState.isError()
                }
            }
        })
    }
    val textFieldState =
        rememberCalculatorTextFieldState(onDone = { startCalculatingEquations(it) })


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {

        TheLayout(boxScope = this) {

            AnimatedVisibility(visible = colorDisplay) {
                ColorList { colorDisplay = !colorDisplay }
            }

            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.BottomEnd) {

                Surface(color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(
                        bottomStart = if (isHorizontal()) 0.dp else 20.dp,
                        bottomEnd = 20.dp
                    ),
                    tonalElevation = 5.dp,
                    shadowElevation = 5.dp,
                    modifier = Modifier
                        .fillMaxSize()
                        .isNotHorizontal {
                            padding(bottom = 50.dp)
                        }

                ) { }

                Box(
                    modifier = Modifier.fillMaxHeight(),
                    contentAlignment = Alignment.TopEnd
                ) {
                    var dropdownMenuExpanded by remember {
                        mutableStateOf(false)
                    }
                    IconButton(
                        modifier = Modifier
                            .statusBarsPadding()
                            .size(76.dp).padding(end = 3.dp)
                            /*.clip(RoundedCornerShape(50.dp))
                            .clickable {
                                dropdownMenuExpanded = !dropdownMenuExpanded
                            }
                            .padding(25.dp),*/,
                        onClick = { }
                    ) {
                        Icon(imageVector = Icons.Default.MoreVert,
                            tint = MaterialTheme.colorScheme.onSurface,
                            contentDescription = "Menu",
                            modifier = Modifier
                                .statusBarsPadding()
                                .size(75.dp)
                                .clip(RoundedCornerShape(50.dp))
                                .clickable {
                                    dropdownMenuExpanded = !dropdownMenuExpanded
                                }
                                .padding(25.dp)
                        )
                        DropdownMenu(
                            expanded = dropdownMenuExpanded,
                            onDismissRequest = { dropdownMenuExpanded = false },
                            modifier = Modifier.padding(end = 4.dp)) {

                            DropdownMenuItem(
                                text = {
                                    Text(stringResource(id = R.string.colors_list))
                                },
                                onClick = { colorDisplay = !colorDisplay },
                                leadingIcon = {
                                    Icon(
                                        Icons.Rounded.Palette,
                                        contentDescription = "Colors List"
                                    )
                                }
                            )

                        }

                    }
                }


                CalculatorTextField(state = textFieldState,
                    modifier = Modifier
                        .zIndex(1f)
                        .isHorizontal {
                            padding(start = 5.dp, end = 5.dp)
                                .padding(10.dp)
                        }
                        .isNotHorizontal {
                            padding(bottom = 50.dp, start = 2.dp)
                        })


                RowOperatorButton {
                    textFieldState.checkCursor { index, t, last, OneChar, cursorHide, cursorInsert ->
                        when (it) {
                            "sin", "abs" -> {
                                textFieldState.append("$it(")
                                operatorArithmeticBracketsStartCounts++
                            }
                            else -> {
                                textFieldState.append(it)
                            }
                        }
                    }
                }

            }

            CalculatorButton(
                onNumberClick = { text: String -> textFieldState.append(text) },
                onOperatorClick = { text: String ->
                    when (true) {
                        // 复位
                        text.isOperatorAC() -> {
                            textFieldState.clearTextField()
                        }
                        // 删除键
                        text.isOperatorBackSpace() -> {
                            textFieldState.deleteTextField()
                        }
                        // 括号
                        text.isOperatorBrackets() -> {
                            textFieldState.checkCursor { _, _, last, _, _, _ ->
                                val isStart = when (true) {
                                    (last == null) -> true
                                    (operatorArithmeticBracketsStartCounts != 0 && !last.isOperatorBracketStart()) -> false
                                    else -> true
                                }
                                if (isStart) {
                                    textFieldState.append("(")
                                    operatorArithmeticBracketsStartCounts++
                                } else {
                                    textFieldState.append(")")
                                    operatorArithmeticBracketsStartCounts--
                                }
                            }
                        }
                        // 其他符号
                        text.isOperator() -> {
                            textFieldState.checkCursor { index, t, last, OneChar, cursorHide, cursorInsert ->
                                when (true) {
                                    (cursorHide && last?.isOperator() == true) -> textFieldState.append(
                                        t.substring(0, t.lastIndex) + text
                                    )
                                    (cursorInsert && last?.isOperator() == true) -> {
                                        textFieldState.value = TextFieldValue(
                                            StringBuilder(t).deleteCharAt(index - 1)
                                                .insert(index - 1, text).toString(),
                                            selection = TextRange(index)
                                        )
                                    }
                                    else -> textFieldState.append(text)
                                }
                            }
                        }
                        else -> throw Exception("Unknown Operator : $text")
                    }

                },
                startCalculatingEquations = { startCalculatingEquations(textFieldState) },
                modifier = Modifier
                    .isHorizontal {
                        weight(1.1f)
                            .padding(horizontal = 25.dp)
                            .padding(top = 7.dp, bottom = 6.dp, start = 2.dp)
                            .statusBarsPadding()
                    }
                    .isNotHorizontal {
                        padding(10.dp)
                    })


        }

    }

}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeCalculatorTheme {
        DefaultView()
    }
}

