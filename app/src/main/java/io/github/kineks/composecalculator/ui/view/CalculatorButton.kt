package io.github.kineks.composecalculator.ui.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.kineks.composecalculator.ui.layout.isHorizontal

@Composable
fun CalculatorButton(
    onNumberClick: (text: String) -> Unit,
    onOperatorClick: (text: String) -> Unit,
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

@Composable
fun RowOperatorButton(
    modifier: Modifier = Modifier,
    @Suppress("UNINITIALIZED_PARAMETER_WARNING") rowOperatorButton: @Composable (text: String) -> Unit = {
        Text(
            text = it,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineLarge,
            modifier = modifier
                .rotate(1f)
                .clickable { clickable(it) }
        )
    },
    clickable: (text: String) -> Unit
) {

    Box(
        contentAlignment = if (isHorizontal()) Alignment.TopStart else Alignment.BottomStart,
        modifier = Modifier
            .fillMaxSize()
            .isHorizontal { padding(start = 2.dp, top = 18.dp).statusBarsPadding() }
    ) {
        LazyVerticalGrid(columns = GridCells.Fixed(4)) {
            item { rowOperatorButton("sin") }
            item { rowOperatorButton("abs") }
            item { rowOperatorButton("pi") }
            item { rowOperatorButton("e") }
        }
    }

}

val CalculatorOperatorButton = listOf("AC", "()", "%", "÷", "×", "−", "+", "←", "=")
val CalculatorNumberButton = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0", ".")
fun String.isNumber(): Boolean = CalculatorNumberButton.indexOf(this) != -1
fun String.isOperator(): Boolean = CalculatorOperatorButton.indexOf(this) != -1
fun String.isOperatorAC(): Boolean = "AC" == this
fun String.isOperatorBackSpace(): Boolean = "←" == this
fun String.isOperatorBracketStart(): Boolean = "(" == this
fun String.isOperatorBracketEnd(): Boolean = ")" == this
fun String.isOperatorBrackets(): Boolean = "()" == this || isOperatorBracketStart() || isOperatorBracketEnd()

// 标记左括号的计数值,
var operatorArithmeticBracketsStartCounts by mutableStateOf(0)