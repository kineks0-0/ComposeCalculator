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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.kineks.composecalculator.ui.layout.isHorizontal

//  本质上就是干了数字小键盘的活
//  注意：OperatorButton 的组件本身对横屏和竖屏的下 Padding 参数 和 宽高比 差异化
//  这样竖屏下按钮接近圆形，而横屏则是椭圆。 同时间距也自响应
@Composable
fun CalculatorButton(
    onNumberClick: String.(text: String) -> Unit,
    onOperatorClick: String.(text: String) -> Unit,
    startCalculatingEquations: () -> Unit,
    modifier: Modifier = Modifier,
    rowModifier: ColumnScope.() -> Modifier = {
        Modifier
            .fillMaxWidth()
            .weight(1f)
    }
) {
    Column(modifier = modifier) {
        Row(modifier = rowModifier()) {
            OperatorButton(
                text = "AC",
                color = MaterialTheme.colorScheme.onTertiary,
                backgroundColor = MaterialTheme.colorScheme.tertiary,
                tonalElevation = 40.dp,
                clickable = onOperatorClick,
                modifier = Modifier.weight(1f)
            )
            OperatorButton(text = "( )", modifier = Modifier.weight(1f)) {
                "()".apply { onOperatorClick("()") }
            }
            OperatorButton(text = "%", modifier = Modifier.weight(1f), clickable = onOperatorClick)
            OperatorButton(text = "÷", modifier = Modifier.weight(1f), clickable = onOperatorClick)
        }
        Row(modifier = rowModifier()) {
            NumberButton(text = "7", modifier = Modifier.weight(1f), clickable = onNumberClick)
            NumberButton(text = "8", modifier = Modifier.weight(1f), clickable = onNumberClick)
            NumberButton(text = "9", modifier = Modifier.weight(1f), clickable = onNumberClick)
            OperatorButton(text = "×", modifier = Modifier.weight(1f), clickable = onOperatorClick)
        }
        Row(modifier = rowModifier()) {
            NumberButton(text = "4", modifier = Modifier.weight(1f), clickable = onNumberClick)
            NumberButton(text = "5", modifier = Modifier.weight(1f), clickable = onNumberClick)
            NumberButton(text = "6", modifier = Modifier.weight(1f), clickable = onNumberClick)
            OperatorButton(text = "−", modifier = Modifier.weight(1f), clickable = onOperatorClick)
        }
        Row(modifier = rowModifier()) {
            NumberButton(text = "1", modifier = Modifier.weight(1f), clickable = onNumberClick)
            NumberButton(text = "2", modifier = Modifier.weight(1f), clickable = onNumberClick)
            NumberButton(text = "3", modifier = Modifier.weight(1f), clickable = onNumberClick)
            OperatorButton(text = "+", modifier = Modifier.weight(1f), clickable = onOperatorClick)
        }
        Row(modifier = rowModifier()) {
            NumberButton(
                text = "←",
                content = {
                    Icon(
                        imageVector = Icons.Outlined.Backspace,
                        contentDescription = "Backspace"
                    )
                },
                modifier = Modifier.weight(1f),
                clickable = onOperatorClick
            )
            NumberButton(text = "0", modifier = Modifier.weight(1f), clickable = onNumberClick)
            NumberButton(text = "•", modifier = Modifier.weight(1f), clickable = {
                ".".apply { onNumberClick(".") }
            })
            OperatorButton(
                text = "=",
                color = MaterialTheme.colorScheme.secondaryContainer,
                backgroundColor = MaterialTheme.colorScheme.onSecondaryContainer,
                alpha = 0.95f,
                tonalElevation = 0.dp,
                modifier = Modifier.weight(1f)
            ) {
                startCalculatingEquations()
            }

        }
    }





}

//  本来是用 LazyRow 实现的，结果发现还是 LazyVerticalGrid 在固定项目列表更好用
@Composable
fun RowOperatorButton(
    modifier: Modifier = Modifier,
    @Suppress("UNINITIALIZED_PARAMETER_WARNING") rowOperatorButton: @Composable (text: String) -> Unit = {
        Text(
            text = it,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier
                .rotate(1f)
                .clickable { clickable(it) }
        )
    },
    clickable: (text: String) -> Unit
) {
    Box(
        contentAlignment = if (isHorizontal()) Alignment.TopStart else Alignment.BottomCenter,
        modifier = modifier
    ) {
        LazyVerticalGrid(columns = GridCells.Fixed(4)) {
            item { rowOperatorButton("sin") }
            item { rowOperatorButton("abs") }
            item { rowOperatorButton("pi") }
            item { rowOperatorButton("e") }
        }
    }

}

// 这里的实现都是判断单字符,按理说应该改成 fun Char.isOperator(): Boolean
val CalculatorOperatorButton = listOf("AC", "()", "%", "÷", "×", "−", "+", "←", "=")
val CalculatorNumberButton = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0", ".")
fun String?.isNumber(): Boolean = CalculatorNumberButton.indexOf(this) != -1
fun String?.isOperator(): Boolean = CalculatorOperatorButton.indexOf(this) != -1
fun String?.isOperatorMIN(): Boolean = "−" == this
fun String?.isOperatorPercentage(): Boolean = "%" == this
fun String?.isOperatorAC(): Boolean = "AC" == this
fun String?.isOperatorBackSpace(): Boolean = "←" == this
fun String?.isOperatorBracketStart(): Boolean = "(" == this
fun String?.isOperatorBracketEnd(): Boolean = ")" == this
fun String?.isOperatorBrackets(): Boolean = "()" == this || isOperatorBracketStart() || isOperatorBracketEnd()

// 标记左括号的计数值,
var operatorBracketsCounts by mutableStateOf(0)
fun updateBracketsCounts(textFieldValue: TextFieldValue) {
    // 避免键盘或者输入法删除括号导致计数错误
    operatorBracketsCounts = 0
    textFieldValue.text.forEach {
        when (it) {
            '(' -> operatorBracketsCounts++
            ')' -> operatorBracketsCounts--
        }
    }
}