package com.example.composecalculator.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.composecalculator.calculator.models.CalculatorAction
import com.example.composecalculator.ui.CalculatorUtils

@Composable
fun CalculatorButtonLayout(
    modifier: Modifier = Modifier, buttonSpacing: Dp = 8.dp, onAction: (CalculatorAction) -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(buttonSpacing)
    ) {
        val buttons = remember {
            CalculatorUtils.getCalculatorButtonsInGrid(columnCount = 4, onClick = onAction)
        }
        buttons.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
            ) {
                row.forEach { button ->
                    CalculatorButtonUi(symbol = button.symbol,
                        modifier = Modifier
                            .aspectRatio(button.aspectRatio)
                            .background(button.buttonColor)
                            .weight(button.weight),
                        onClick = {
                            button.onClick()
                        }) {
                        button.onLongClick()
                    }
                }
            }
        }
    }
}