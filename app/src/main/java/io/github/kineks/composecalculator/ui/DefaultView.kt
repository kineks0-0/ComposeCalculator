package io.github.kineks.composecalculator.ui

import android.util.Log
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
    // 沉浸状态栏
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = !isSystemInDarkTheme()
    SideEffect {
        systemUiController.setStatusBarColor(Color.Transparent, useDarkIcons)
    }

    // 显示颜色列表
    var colorDisplay by remember {
        mutableStateOf(false)
    }

    // 开始计算
    val startCalculatingEquations: (CalculatorTextFieldState) -> Unit by remember {
        mutableStateOf({ state ->
            // 清空输入框无效数据
            if (state.value.text == getString(R.string.data_error)) {
                state.clearTextField()
                return@mutableStateOf
            }
            try {
                // 如果括号没打完
                if (operatorBracketsCounts != 0) {
                    while (operatorBracketsCounts != 0) {
                        state.add(")")
                        operatorBracketsCounts--
                    }
                }
                // 将算式显示到输入框 label
                state.label = state.value.text
                // 开始计算
                val number = state.value.text.parse().toString()
                state.setTextField(number.subZeroAndDot())

                // 如果数据错误标记输入框错误状态
                if (number == "NaN") {
                    state.isError()
                }

            } catch (e: Exception) {
                state.setTextField(getString(R.string.data_error))
                state.isError()
                Log.e("DefaultView", e.message, e)
            }

        })
    }
    // 输入框 State 管理
    val state =
        rememberCalculatorTextFieldState(onDone = { startCalculatingEquations(it) })


    // 自定义布局，自适应横竖屏
    TheLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {

        AnimatedVisibility(visible = colorDisplay) {
            ColorList { colorDisplay = !colorDisplay }
        }

        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.BottomEnd) {

            // 输入框背景
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

            // 菜单
            Box(
                modifier = Modifier.fillMaxHeight(),
                contentAlignment = Alignment.TopEnd
            ) {
                var dropdownMenuExpanded by remember {
                    mutableStateOf(false)
                }
                // 玄学 bug ，不在同一布局里声明的话菜单可能会飞左下角
                Box(
                    modifier = Modifier
                        .statusBarsPadding()
                        .size(76.dp)
                        .padding(end = 3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
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
                        modifier = Modifier.padding(end = 4.dp)
                    ) {

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

            // 输入框
            CalculatorTextField(state = state,
                modifier = Modifier
                    .zIndex(1f)
                    .isHorizontal {
                        padding(start = 5.dp, end = 5.dp)
                            .padding(10.dp)
                    }
                    .isNotHorizontal {
                        padding(bottom = 50.dp, start = 2.dp)
                    })


            // 函数列表
            RowOperatorButton {
                when (it) {
                    "sin", "abs" -> {
                        state.add("$it(")
                        operatorBracketsCounts++
                    }
                    else -> {
                        state.add(it)
                    }
                }
            }

        }

        // 计算器按钮
        CalculatorButton(
            onNumberClick = { text: String -> state.add(text) },
            onOperatorClick = { text: String ->
                when (true) {
                    // 复位
                    isOperatorAC() -> {
                        state.clearTextField()
                    }
                    // 删除键
                    isOperatorBackSpace() -> {
                        state.deleteLast()
                    }
                    // 括号
                    isOperatorBrackets() -> {
                        when (true) {
                            (operatorBracketsCounts != 0 && !state.last.isOperatorBracketStart()) -> {
                                state.add(")")
                                operatorBracketsCounts--
                            }
                            else -> {
                                state.add("(")
                                operatorBracketsCounts++
                            }
                        }
                    }
                    // 其他符号
                    isOperator() -> {
                        state.apply {
                            when (true) {
                                isOperatorPercentage() -> {
                                    if (last.isNumber())
                                        add(text)
                                }
                                //  对于需要输入负数的情况
                                (isOperatorMIN() && last.isOperator()) -> {
                                    add(text, lastSecond.isOperator())
                                }
                                // 替换 --
                                (last.isOperatorMIN() && lastSecond.isOperator()) -> {
                                    deleteLast()
                                    add(text, true)
                                }
                                (last.isOperator() && !last.isOperatorPercentage()) -> add(text, true)
                                else -> add(text)
                            }
                        }
                    }
                    else -> throw Exception("Unknown Operator : $text")
                }

            },
            startCalculatingEquations = { startCalculatingEquations(state) },
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


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeCalculatorTheme {
        DefaultView()
    }
}

