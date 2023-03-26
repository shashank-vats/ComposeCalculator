package com.example.composecalculator

data class CalculatorState(
    val displayTokens: List<CalculatorDisplayItem> = listOf(),
    val currentToken: String = "",
    val error: String? = null
)

fun CalculatorState.toDisplayString(): String {
    var displayStr = ""
    displayTokens.forEach { token ->
        displayStr += token.displayStr
    }
    displayStr += currentToken
    return displayStr
}
