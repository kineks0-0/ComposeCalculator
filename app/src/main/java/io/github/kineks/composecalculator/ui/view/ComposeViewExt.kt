package io.github.kineks.composecalculator.ui.view

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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.kineks.composecalculator.ui.layout.isHorizontal

@Composable
fun NumberButton(
    text: String,
    color: Color = MaterialTheme.colorScheme.onSurface,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    ratio: Float = 1.15f,
    alpha: Float = 1f,
    tonalElevation: Dp = 1.dp,
    content: @Composable (BoxScope.() -> Unit)? = null,
    clickable: (text: String) -> Unit
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

@Suppress("OPT_IN_IS_NOT_ENABLED")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OperatorButton(
    text: String,
    color: Color = MaterialTheme.colorScheme.onSecondary,
    backgroundColor: Color = MaterialTheme.colorScheme.secondary,
    ratio: Float = 1.135f,
    alpha: Float = 0.9f,
    tonalElevation: Dp = 0.dp,
    shadowElevation: Dp = 1.dp,
    content: @Composable (BoxScope.() -> Unit)? = null,
    clickable: (text: String) -> Unit
) {
    val modifier =
    if (isHorizontal())
        Modifier
            .padding(3.dp)
            .padding(horizontal = 4.dp)
    else
        Modifier
            .padding(5.dp)
            .padding(vertical = 1.dp)


    Surface(
        shape = RoundedCornerShape(50),
        onClick = { clickable(text) },
        tonalElevation = tonalElevation,
        shadowElevation = shadowElevation,
        //contentColor = backgroundColor,
        color = backgroundColor.copy(alpha = alpha),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .alpha(1f)
                .aspectRatio(
                    if (isHorizontal())
                        ratio + 0.3f
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


@Composable
fun ColorItem(
    text: String,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    tonalElevation: Dp = 1.dp,
    clickable: () -> Unit = { }
) {
    Surface(
        //hape = MaterialTheme.shapes.medium,
        tonalElevation = tonalElevation,
        color = backgroundColor,
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(0.dp)
            .padding(top = 0.dp, bottom = 0.dp)
            .clickable {
                clickable()
            }
    ) {
        Box(
            modifier = Modifier
                //.fillMaxHeight()
                .fillMaxWidth().offset(y =(-18).dp)
            //.aspectRatio(ratio,false)
            ,
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

