package io.github.kineks.composecalculator.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import io.github.kineks.composecalculator.*
import io.github.kineks.composecalculator.R
import io.github.kineks.composecalculator.ui.layout.TheLayout
import io.github.kineks.composecalculator.ui.layout.isHorizontal
import io.github.kineks.composecalculator.ui.layout.isNotHorizontal
import io.github.kineks.composecalculator.ui.theme.ComposeCalculatorTheme
import io.github.kineks.composecalculator.ui.view.*


@Composable
fun DefaultView() {

    rememberSystemUiController().setStatusBarColor(MaterialTheme.colorScheme.surface)

    var colorDisplay by remember {
        mutableStateOf(false)
    }
    val startCalculatingEquations: (CalculatorTextFieldState) -> Unit by remember {
        mutableStateOf({ textFieldState ->
            textFieldState.apply {
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
            }
        })
    }
    val textFieldState = rememberCalculatorTextFieldState(
        onDone = { startCalculatingEquations(it) }
    )


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopCenter
    ) {

        TheLayout(boxScope = this) {
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
                        .isNotHorizontal {
                            padding(bottom = 50.dp)
                        }

                ) {}


                CalculatorTextField(
                    state = textFieldState, modifier = Modifier
                        .zIndex(1f)
                        .isHorizontal {
                            padding(start = 5.dp, end = 10.dp)
                        }
                        .isNotHorizontal {
                            padding(bottom = 50.dp)
                        }
                )
            }

            CalculatorButton(
                onNumberClick = { text: String ->
                    textFieldState.textFieldLabel = ""
                    textFieldState.setTextField(
                        textFieldState.textFieldValue.text.replaceIfEqual("0",text)
                    )
                },
                onOperatorClick = { text: String ->
                    textFieldState.textFieldLabel = ""
                    when(true) {
                        text.isOperatorAC() -> {
                            textFieldState.clearTextField()
                        }
                        text.isOperatorBackSpace() -> {
                            textFieldState.apply {
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
                            }
                        }
                        text.isOperatorArithmeticBrackets() -> {
                            textFieldState.append("()", -1)
                        }
                        text.isOperator() -> {
                            textFieldState.setTextField(
                                textFieldState.textFieldValue.text.let {
                                    (if (OperatorMap[it.lastOrNull().toString()] != null) it.substring(
                                        0,
                                        it.lastIndex
                                    ) else it) + text
                                }
                            )
                        }
                        else -> throw Exception("Unknown Operator : $text")
                    }

                },
                startCalculatingEquations = { startCalculatingEquations(textFieldState) },
                modifier = Modifier
                    .isHorizontal {
                        weight(1.2f)
                            .padding(
                                vertical = 2.dp, horizontal = 18.dp
                            )
                    }
                    .isNotHorizontal {
                        padding(10.dp)
                    }
            )


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

