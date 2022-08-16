package io.github.kineks.composecalculator.ui.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CalculatorButton(
    onNumberClick: (text:String) -> Unit,
    onOperatorClick: (text:String) -> Unit,
    startCalculatingEquations: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = modifier,
        verticalArrangement = Arrangement.Bottom,
        userScrollEnabled = false
    ) {

        item {
            OperatorButton(
                text = "AC",
                color = MaterialTheme.colorScheme.onTertiary,
                backgroundColor = MaterialTheme.colorScheme.tertiary,
                tonalElevation = 40.dp,
                clickable = onOperatorClick
            )
        }
        item { OperatorButton(text = "( )") { onOperatorClick("()") } }
        item { OperatorButton(text = "%", clickable = onOperatorClick) }
        item { OperatorButton(text = "÷", clickable = onOperatorClick) }

        item { NumberButton(text = "7", clickable = onNumberClick) }
        item { NumberButton(text = "8", clickable = onNumberClick) }
        item { NumberButton(text = "9", clickable = onNumberClick) }
        item { OperatorButton(text = "×", clickable = onOperatorClick) }

        item { NumberButton(text = "4", clickable = onNumberClick) }
        item { NumberButton(text = "5", clickable = onNumberClick) }
        item { NumberButton(text = "6", clickable = onNumberClick) }
        item { OperatorButton(text = "−", clickable = onOperatorClick) }

        item { NumberButton(text = "1", clickable = onNumberClick) }
        item { NumberButton(text = "2", clickable = onNumberClick) }
        item { NumberButton(text = "3", clickable = onNumberClick) }
        item { OperatorButton(text = "+", clickable = onOperatorClick) }

        item {
            NumberButton(
                text = "←",
                content = {
                    Icon(
                        imageVector = Icons.Outlined.Backspace,
                        contentDescription = "Backspace"
                    )
                },
                clickable = onOperatorClick
            )
        }
        item { NumberButton(text = "0", clickable = onNumberClick) }
        item {
            NumberButton(text = "•", clickable = { onNumberClick(".") })
        }
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

val CalculatorOperatorButton = listOf("AC","()","%","÷","×","−","+","←","=")
val CalculatorNumberButton = listOf("1","2","3","4","5","6","7","8","9","0",".")
fun String.isNumber(): Boolean = CalculatorNumberButton.indexOf(this) != -1
fun String.isOperator(): Boolean = CalculatorOperatorButton.indexOf(this) != -1
fun String.isOperatorAC(): Boolean = "AC" == this
fun String.isOperatorBackSpace(): Boolean = "←" == this
fun String.isOperatorArithmeticBrackets(): Boolean = "()" == this