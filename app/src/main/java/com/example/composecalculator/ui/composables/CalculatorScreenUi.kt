package com.example.composecalculator.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.composecalculator.calculator.models.CalculatorState
import com.example.composecalculator.calculator.models.toDisplayString

@Composable
fun CalculatorScreenUi(
    state: CalculatorState,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val text = remember(state) {
        state.toDisplayString()
    }
    LaunchedEffect(key1 = text) {
        scrollState.scrollTo(scrollState.maxValue)
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.Bottom
    ) {
        Text(
            text = text,
            textAlign = TextAlign.End,
            modifier = Modifier.fillMaxWidth(),
            fontWeight = FontWeight.Light,
            fontSize = 80.sp,
            color = Color.White
        )
    }
}