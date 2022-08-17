package io.github.kineks.composecalculator.ui.view

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.kineks.composecalculator.ui.layout.isHorizontal
import io.github.kineks.composecalculator.ui.layout.isNotHorizontal


//  数字按钮, 实际代码实现是  OperatorButton()
@Composable
fun NumberButton(
    text: String,
    color: Color = MaterialTheme.colorScheme.onSurface,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    ratio: Float = 1.15f,
    alpha: Float = 1f,
    tonalElevation: Dp = 1.dp,
    content: @Composable (BoxScope.() -> Unit)? = null,
    clickable: String.(text: String) -> Unit
) {
    OperatorButton(
        text = text,
        color = color,
        backgroundColor = backgroundColor,
        ratio = ratio,
        alpha = alpha,
        tonalElevation = tonalElevation,
        content = content,
        clickable = clickable
    )
}



//  运算符号按钮, 为了能同时显示图标将 Text 给独立出来
@Suppress("OPT_IN_IS_NOT_ENABLED")
@SuppressLint("ModifierParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun OperatorButton(
    text: String,
    color: Color = MaterialTheme.colorScheme.onSecondary,
    backgroundColor: Color = MaterialTheme.colorScheme.secondary,
    ratio: Float = 1.135f,
    alpha: Float = 0.9f,
    tonalElevation: Dp = 0.dp,
    shadowElevation: Dp = 1.dp,
    modifier: Modifier =
            Modifier.isHorizontal {
                padding(4.dp).padding(horizontal = 2.dp)
            }.isNotHorizontal {
                padding(5.dp).padding(vertical = 1.dp)
            },
    content: @Composable (BoxScope.() -> Unit)? = null,
    clickable: String.(text: String) -> Unit
) {

    Surface(
        shape = RoundedCornerShape(50),
        onClick = { text.apply { clickable(text) } },
        tonalElevation = tonalElevation,
        shadowElevation = shadowElevation,
        //contentColor = backgroundColor,
        color = backgroundColor.copy(alpha = alpha),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .aspectRatio(
                    if (isHorizontal())
                        ratio + 0.28f
                    else
                        ratio, true
                ),
            contentAlignment = Alignment.Center,
            content = (content ?: {
                Text(
                    text = text,
                    color = color,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineLarge
                )
            })
        )

    }
}





/**
 * 显示 Material You 所有颜色列表的‘项目’
 */
//  因为不熟悉 Material You 的颜色, 每个系统因为 动态颜色 再加上compose和官网那张图 名称不一致 导致仅供参考
//  有个坑主要注意的是, tonalElevation 的实现是改“颜色”来分区Z轴 ,所以需注意下颜色选择和主题自配色
//  好处就是背景和按钮能用同一个颜色 , MY会根据Z轴处理实际可视对比颜色
@Composable
fun ColorItem(
    text: String,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    tonalElevation: Dp = 1.dp,
    clickable: () -> Unit = { }
) {
    Surface(
        tonalElevation = tonalElevation,
        color = backgroundColor,
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(0.dp)
            .padding(top = 0.dp, bottom = 0.dp)
            .clickable { clickable() }
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().offset(y =(-18).dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = color,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 22.dp),
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.headlineSmall
            )
        }

    }
}


/**
 * 显示 Material You 所有颜色项目的‘列表’
 */
@Composable
fun ColorList(clickable: () -> Unit) {

    LazyVerticalGrid(columns = GridCells.Adaptive(180.dp), modifier = Modifier.statusBarsPadding())  {
        item {
            ColorItem(
                text = "Primary",
                color = MaterialTheme.colorScheme.onPrimary,
                backgroundColor = MaterialTheme.colorScheme.primary,
                clickable = clickable
            )
        }
        item {
            ColorItem(
                text = "PrimaryContainer",
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                clickable = clickable
            )
        }
        item {
            ColorItem(
                text = "InversePrimary",
                color = MaterialTheme.colorScheme.onPrimary,
                backgroundColor = MaterialTheme.colorScheme.inversePrimary,
                clickable = clickable
            )
        }
        item {
            ColorItem(
                text = "Secondary",
                color = MaterialTheme.colorScheme.onSecondary,
                backgroundColor = MaterialTheme.colorScheme.secondary,
                clickable = clickable
            )
        }
        item {
            ColorItem(
                text = "SecondaryContainer",
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                clickable = clickable
            )
        }
        item {
            ColorItem(
                text = "Tertiary",
                color = MaterialTheme.colorScheme.onTertiary,
                backgroundColor = MaterialTheme.colorScheme.tertiary,
                clickable = clickable
            )
        }
        item {
            ColorItem(
                text = "Surface",
                color = MaterialTheme.colorScheme.onSurface,
                backgroundColor = MaterialTheme.colorScheme.surface,
                clickable = clickable
            )
        }
        item {
            ColorItem(
                text = "SurfaceVariant",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                clickable = clickable
            )
        }
        item {
            ColorItem(
                text = "SurfaceTint",
                color = MaterialTheme.colorScheme.onSurface,
                backgroundColor = MaterialTheme.colorScheme.surfaceTint,
                clickable = clickable
            )
        }
        item {
            ColorItem(
                text = "Primary",
                color = MaterialTheme.colorScheme.onPrimary,
                backgroundColor = MaterialTheme.colorScheme.primary,
                clickable = clickable
            )
        }
        item {
            ColorItem(
                text = "InverseSurface",
                color = MaterialTheme.colorScheme.inverseOnSurface,
                backgroundColor = MaterialTheme.colorScheme.inverseSurface,
                clickable = clickable
            )
        }
        item {
            ColorItem(
                text = "Error",
                color = MaterialTheme.colorScheme.onError,
                backgroundColor = MaterialTheme.colorScheme.error,
                clickable = clickable
            )
        }
        item {
            ColorItem(
                text = "ErrorContainer",
                color = MaterialTheme.colorScheme.onErrorContainer,
                backgroundColor = MaterialTheme.colorScheme.errorContainer,
                clickable = clickable
            )
        }
        item {
            ColorItem(
                text = "Outline",
                color = MaterialTheme.colorScheme.outlineVariant,
                backgroundColor = MaterialTheme.colorScheme.outline,
                clickable = clickable
            )
        }
        item {
            ColorItem(
                text = "OutlineVariant",
                color = MaterialTheme.colorScheme.outline,
                backgroundColor = MaterialTheme.colorScheme.outlineVariant,
                clickable = clickable
            )
        }
        item {
            ColorItem(
                text = "Scrim",
                color = MaterialTheme.colorScheme.onBackground,
                backgroundColor = MaterialTheme.colorScheme.scrim,
                clickable = clickable
            )
        }

    }
}

