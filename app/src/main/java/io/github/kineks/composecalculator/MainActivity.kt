package io.github.kineks.composecalculator

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import io.github.kineks.composecalculator.ui.theme.ComposeCalculatorTheme
import io.github.kineks.composecalculator.ui.view.ColorList
import io.github.kineks.composecalculator.ui.view.NumberButton
import io.github.kineks.composecalculator.ui.view.OperatorButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DefaultView()
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalUnitApi::class, ExperimentalComposeUiApi::class)
@Composable
fun DefaultView() {
    ComposeCalculatorTheme {

        var colorDisplay by remember {
            mutableStateOf(false)
        }

        var textFieldValue: TextFieldValue by remember {
            mutableStateOf(TextFieldValue())
        }
        var textFieldLabel: String by remember {
            mutableStateOf("")
        }
        val textFieldCursorEnabled by mutableStateOf(textFieldValue.text.isNotEmpty())


        val onClick by remember {
            mutableStateOf({ text: String ->
                textFieldLabel = ""
                textFieldValue = textFieldValue.append(text)
            })
        }
        val clearTextField by remember {
            mutableStateOf({
                textFieldValue = TextFieldValue()
                textFieldLabel = ""
            })
        }
        val setTextField: (String) -> Unit by remember {
            mutableStateOf({ text ->
                textFieldValue = TextFieldValue(
                    text = text,
                    selection = TextRange(text.length)
                )
            })
        }
        val startCalculatingEquations by remember {
            mutableStateOf({
                try {
                    if (textFieldValue.text == getString(R.string.data_error)) {
                        clearTextField()
                    } else {
                        textFieldLabel = textFieldValue.text
                        setTextField(
                            textFieldValue.text.parse().toString().subZeroAndDot()
                        )
                    }
                } catch (e: Exception) {
                    setTextField(getString(R.string.data_error))
                }
            })
        }
        val keyboard = LocalSoftwareKeyboardController.current
        keyboard?.hide()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.TopCenter
        ) {

            Column(
                modifier = Modifier.matchParentSize(), verticalArrangement = Arrangement.Bottom
            ) {
                if (colorDisplay) ColorList()

                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.BottomEnd) {
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
                        tonalElevation = 5.dp,
                        shadowElevation = 5.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .padding(bottom = 50.dp)
                    ) {}
                    TextField(
                        value = textFieldValue,
                        onValueChange = { textFieldValue = it },
                        label = {
                            Text(
                                text = textFieldLabel,
                                style = MaterialTheme.typography.headlineMedium,
                                modifier = Modifier
                                    .alpha(0.8f)
                                    .clickable {
                                        if (textFieldLabel.isNotEmpty())
                                            setTextField(textFieldLabel)
                                    }
                            )
                        },
                        keyboardActions = KeyboardActions(
                            onDone = {
                                startCalculatingEquations()
                            }
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .zIndex(1f)
                            .padding(bottom = 50.dp),
                        textStyle = TextStyle(
                            fontSize = TextUnit(
                                50f + 80f / (textFieldValue.text.length / 6 + 1), TextUnitType.Sp
                            ), textAlign = TextAlign.End
                        ),
                        colors = TextFieldDefaults.textFieldColors(
                            textColor = MaterialTheme.colorScheme.onSurface,
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            cursorColor = if (textFieldCursorEnabled)
                                MaterialTheme.colorScheme.primary
                            else
                                Color.Transparent,
                            errorCursorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            errorIndicatorColor = Color.Transparent,
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

                            focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurface,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurface,
                            errorLabelColor = MaterialTheme.colorScheme.onSurface,

                            placeholderColor = Color.Transparent,
                            disabledPlaceholderColor = Color.Transparent
                        )
                    )

                }


                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.Bottom,
                    userScrollEnabled = false
                ) {

                    item {
                        OperatorButton(
                            text = "AC",
                            color = MaterialTheme.colorScheme.onTertiary,
                            backgroundColor = MaterialTheme.colorScheme.tertiary,
                            tonalElevation = 40.dp
                        ) { clearTextField() }
                    }
                    item { OperatorButton(text = "( )", clickable = {
                        textFieldValue = textFieldValue.append("()",-1)
                    }) }
                    item { OperatorButton(text = "%", clickable = onClick) }
                    item { OperatorButton(text = "÷", clickable = onClick) }

                    item { NumberButton(text = "7", clickable = onClick) }
                    item { NumberButton(text = "8", clickable = onClick) }
                    item { NumberButton(text = "9", clickable = onClick) }
                    item { OperatorButton(text = "×", clickable = onClick) }

                    item { NumberButton(text = "4", clickable = onClick) }
                    item { NumberButton(text = "5", clickable = onClick) }
                    item { NumberButton(text = "6", clickable = onClick) }
                    item { OperatorButton(text = "−", clickable = onClick) }

                    item { NumberButton(text = "1", clickable = onClick) }
                    item { NumberButton(text = "2", clickable = onClick) }
                    item { NumberButton(text = "3", clickable = onClick) }
                    item { OperatorButton(text = "+", clickable = onClick) }

                    item {
                        NumberButton(text = "←", clickable = {
                            when (textFieldValue.text.length) {
                                0 -> {}
                                1 -> clearTextField()
                                else -> setTextField(
                                    textFieldValue.text.substring(
                                        0, textFieldValue.text.lastIndex
                                    )
                                )
                            }
                            textFieldLabel = ""
                        })
                    }
                    item { NumberButton(text = "0", clickable = onClick) }
                    item { NumberButton(text = "•", clickable = onClick) }
                    item {
                        OperatorButton(
                            text = "=",
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            backgroundColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            alpha = 0.95f,
                            tonalElevation = 0.dp
                        ) {
                            startCalculatingEquations()
                        }
                    }
                }
            }

        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    DefaultView()
}