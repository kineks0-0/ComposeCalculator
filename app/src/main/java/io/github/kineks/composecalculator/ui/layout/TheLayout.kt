package io.github.kineks.composecalculator.ui.layout

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration

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
//  传入 BoxScope 的原因单纯因为 Modifier.matchParentSize() 不会导致两边布局填充大小不对又能填充剩余布局空间
@Composable
fun TheLayout(boxScope: BoxScope, content: @Composable TheLayoutScopeInstance.() -> Unit) {

    boxScope.apply {
        when (isHorizontal()) {

            true -> {
                Row(
                    modifier = Modifier.matchParentSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TheLayoutScopeInstance(null, this).apply {
                        content()
                    }
                }
            }

            // Other wise
            false -> {
                Column(
                    modifier = Modifier.matchParentSize(),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    TheLayoutScopeInstance(this, null).apply {
                        content()
                    }
                }
            }

        }
    }


}

