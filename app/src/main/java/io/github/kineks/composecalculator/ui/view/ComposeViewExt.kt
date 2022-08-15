package io.github.kineks.composecalculator.ui.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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

@Composable
fun NumberButton(
    text: String,
    color: Color = MaterialTheme.colorScheme.onSurface,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    ratio: Float = 1.15f,
    alpha: Float = 1f,
    tonalElevation: Dp = 1.dp,
    clickable: (text: String) -> Unit
) {
    OperatorButton(
        text = text,
        color = color,
        backgroundColor = backgroundColor,
        ratio = ratio,
        alpha = alpha,
        tonalElevation = tonalElevation,
        clickable = clickable
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OperatorButton(
    text: String,
    color: Color = MaterialTheme.colorScheme.onSecondary,
    backgroundColor: Color = MaterialTheme.colorScheme.secondary,
    ratio: Float = 1.135f,
    alpha: Float = 0.9f,
    tonalElevation: Dp = 0.dp,
    clickable: (text: String) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(50),
        onClick = { clickable(text) },
        tonalElevation = tonalElevation,
        shadowElevation = 1.dp,
        //contentColor = backgroundColor,
        color = backgroundColor.copy(alpha = alpha),
        modifier = Modifier
            //.fillMaxHeight()
            .padding(5.dp)
            .padding(top = 1.dp, bottom = 1.dp)
    ) {
        Box(
            modifier = Modifier
                .alpha(1f)
                //.fillMaxHeight()
                //.fillMaxWidth(ratio)
                .aspectRatio(ratio, true),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = color,
                //fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineLarge
            )
        }

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
            .height(60.dp)
            .padding(0.dp)
            .padding(top = 1.dp, bottom = 1.dp)
            .clickable {
                clickable()
            }
    ) {
        Box(
            modifier = Modifier
                //.fillMaxHeight()
                .fillMaxWidth()
            //.aspectRatio(ratio,false)
            ,
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = color,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 25.dp),
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.headlineLarge
            )
        }

    }
}

@Composable
fun ColorList() {

    LazyColumn {
        item {
            ColorItem(
                text = "Primary",
                color = MaterialTheme.colorScheme.onPrimary,
                backgroundColor = MaterialTheme.colorScheme.primary
            )
        }
        item {
            ColorItem(
                text = "PrimaryContainer",
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                backgroundColor = MaterialTheme.colorScheme.primaryContainer
            )
        }
        item {
            ColorItem(
                text = "InversePrimary",
                color = MaterialTheme.colorScheme.onPrimary,
                backgroundColor = MaterialTheme.colorScheme.inversePrimary
            )
        }
        item {
            ColorItem(
                text = "Secondary",
                color = MaterialTheme.colorScheme.onSecondary,
                backgroundColor = MaterialTheme.colorScheme.secondary
            )
        }
        item {
            ColorItem(
                text = "SecondaryContainer",
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                backgroundColor = MaterialTheme.colorScheme.secondaryContainer
            )
        }
        item {
            ColorItem(
                text = "Tertiary",
                color = MaterialTheme.colorScheme.onTertiary,
                backgroundColor = MaterialTheme.colorScheme.tertiary
            )
        }
        item {
            ColorItem(
                text = "Surface",
                color = MaterialTheme.colorScheme.onSurface,
                backgroundColor = MaterialTheme.colorScheme.surface
            )
        }
        item {
            ColorItem(
                text = "SurfaceVariant",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
        item {
            ColorItem(
                text = "SurfaceTint",
                color = MaterialTheme.colorScheme.onSurface,
                backgroundColor = MaterialTheme.colorScheme.surfaceTint
            )
        }
        item {
            ColorItem(
                text = "Primary",
                color = MaterialTheme.colorScheme.onPrimary,
                backgroundColor = MaterialTheme.colorScheme.primary
            )
        }
        item {
            ColorItem(
                text = "InverseSurface",
                color = MaterialTheme.colorScheme.inverseOnSurface,
                backgroundColor = MaterialTheme.colorScheme.inverseSurface
            )
        }
        item {
            ColorItem(
                text = "Error",
                color = MaterialTheme.colorScheme.onError,
                backgroundColor = MaterialTheme.colorScheme.error
            )
        }
        item {
            ColorItem(
                text = "ErrorContainer",
                color = MaterialTheme.colorScheme.onErrorContainer,
                backgroundColor = MaterialTheme.colorScheme.errorContainer
            )
        }
        item {
            ColorItem(
                text = "Outline",
                color = MaterialTheme.colorScheme.outlineVariant,
                backgroundColor = MaterialTheme.colorScheme.outline
            )
        }
        item {
            ColorItem(
                text = "OutlineVariant",
                color = MaterialTheme.colorScheme.outline,
                backgroundColor = MaterialTheme.colorScheme.outlineVariant
            )
        }
        item {
            ColorItem(
                text = "Scrim",
                color = MaterialTheme.colorScheme.onBackground,
                backgroundColor = MaterialTheme.colorScheme.scrim
            )
        }

    }
}

@Composable
fun CalculatorButtonLayout() {
}