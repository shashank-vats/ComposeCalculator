package com.example.composecalculator.ui.composables

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.composecalculator.calculator.models.CalculatorAction
import com.example.composecalculator.calculator.models.CalculatorState
import com.example.composecalculator.ui.theme.ComposeCalculatorTheme
import com.example.composecalculator.ui.theme.MediumGray

@Composable
fun CalculatorUi(
    state: CalculatorState,
    modifier: Modifier = Modifier,
    buttonSpacing: Dp = 8.dp,
    onAction: (CalculatorAction) -> Unit
) {
    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .align(Alignment.BottomCenter),
            verticalArrangement = Arrangement.spacedBy(buttonSpacing)
        ) {
            if (state.error != null) {
                val context = LocalContext.current
                LaunchedEffect(key1 = state.error) {
                    Toast.makeText(context, state.error, Toast.LENGTH_SHORT).show()
                }
            }
            CalculatorScreenUi(
                state = state,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = true)
                    .padding(vertical = 32.dp)
            )
            CalculatorButtonLayout(
                onAction = onAction, modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview
@Composable
fun CalculatorPreview() {
    ComposeCalculatorTheme {
        CalculatorUi(
            state = CalculatorState(mutableListOf(), ""),
            onAction = {},
            modifier = Modifier
                .fillMaxSize()
                .background(MediumGray)
                .padding(16.dp)
        )
    }
}