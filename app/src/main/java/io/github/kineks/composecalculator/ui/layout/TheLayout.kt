package io.github.kineks.composecalculator.ui.layout

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import io.github.kineks.composecalculator.ui.theme.ComposeCalculatorTheme

// Compos 没法直接看屏幕数据也有点奇怪
@Composable
@Stable
fun isHorizontal() = when (LocalConfiguration.current.orientation) {
    Configuration.ORIENTATION_LANDSCAPE -> true
    // Other wise
    else -> false
}

//  布局不知道起什么就干脆 “TheLayout” 了
//  布局里所以 TheLayoutScopeInstance ,默认提供 Column 和 Row 内部的 Modifier 的扩展
//  fun isHorizontal() 加上这个布局基本可以同个布局代码实现 横屏 和 竖屏
@Composable
@SuppressLint("ModifierParameter")
fun TheLayout(
    modifier: Modifier = Modifier,
    contentModifier: Modifier = Modifier,
    content: @Composable TheLayoutScopeInstance.() -> Unit
) {

    val scope by remember {
        mutableStateOf(TheLayoutScopeInstance(null, null))
    }

    Box(modifier = modifier) {
        when (isHorizontal()) {

            true -> {
                Row(
                    modifier = contentModifier.matchParentSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    scope.apply {
                        rowScope = this@Row
                        content()
                    }
                }
            }

            // Other wise
            false -> {
                Column(
                    modifier = contentModifier.matchParentSize(),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    scope.apply {
                        columnScope = this@Column
                        content()
                    }
                }
            }

        }
    }


}

@Preview(showBackground = true)
@Composable
fun TheLayoutPreview() {

    ComposeCalculatorTheme {
        //  自定义布局，自适应横竖屏
        TheLayout(modifier = Modifier.fillMaxSize()) {

            //  在横竖屏下不变的组件
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                Text(text = "Column1")
            }

            //  根据屏幕方向切换不同内容的组件
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .background(Color.LightGray)
            ) {
                when (isHorizontal()) {
                    true -> {
                        Text(text = "Row with Horizontal")
                    }
                    false -> {
                        Text(text = "Row with Vertical")
                    }
                }
            }

            //  仅根据屏幕方向对布局配置做差异化的组件
            Column(
                modifier = Modifier
                    .isHorizontal {
                        weight(1f)
                    }
                    .isNotHorizontal {
                        weight(2f)
                    }
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                //  不在横屏透明度就会变 0.1 的组件
                Text(
                    text = "Only Horizontal",
                    modifier = Modifier.isNotHorizontal { alpha(0.1f) })
            }

        }
    }

}