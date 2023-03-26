package com.example.composecalculator.ui

import androidx.compose.ui.graphics.Color
import com.example.composecalculator.calculator.models.CalculatorAction

data class CalculatorButton(
    val symbol: String,
    val buttonColor: Color,
    val textColor: Color = Color.White,
    val aspectRatio: Float = 1f,
    val weight: Float = aspectRatio,
    val onClick: () -> Unit,
    val onLongClick: () -> Unit = { }
)
