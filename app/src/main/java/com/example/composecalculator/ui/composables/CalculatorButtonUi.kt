package com.example.composecalculator.ui.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.example.composecalculator.calculator.models.CalculatorAction

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CalculatorButtonUi(
    symbol: String,
    modifier: Modifier,
    textColor: Color = Color.White,
    fontSize: TextUnit = 36.sp,
    onClick: () -> Unit,
    onLongClick: () -> Unit = { }
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .clip(CircleShape)
            .combinedClickable(onLongClick = onLongClick) { onClick() }
            .then(modifier)
    ) {
        Text(
            text = symbol,
            fontSize = fontSize,
            color = textColor
        )
    }
}