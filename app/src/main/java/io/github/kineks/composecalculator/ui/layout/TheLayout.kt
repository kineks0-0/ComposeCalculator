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

@Composable
@Stable
fun isHorizontal() = when (LocalConfiguration.current.orientation) {
    Configuration.ORIENTATION_LANDSCAPE -> true
    // Other wise
    else -> false
}

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
                    TheLayoutScopeInstance(null, this).apply{
                        content()
                    }

                }
                //Text("Landscape")
            }

            // Other wise
            false -> {
                Column(
                    modifier = Modifier.matchParentSize(),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    TheLayoutScopeInstance(this, null).apply{
                        content()
                    }
                }
                //Text("Portrait")
            }

        }
    }
}

