package io.github.kineks.composecalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import io.github.kineks.composecalculator.ui.DefaultView
import io.github.kineks.composecalculator.ui.theme.ComposeCalculatorTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeCalculatorTheme {
                DefaultView()
            }
        }
    }

}
