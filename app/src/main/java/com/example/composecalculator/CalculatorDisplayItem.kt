package com.example.composecalculator

sealed class CalculatorDisplayItem(val displayStr: String) {
    object AddSymbol : CalculatorDisplayItem("+"), CalculatorOperator, CalculatorSign
    object MinusSymbol : CalculatorDisplayItem("-"), CalculatorOperator, CalculatorSign
    object MultiplySymbol : CalculatorDisplayItem("x"), CalculatorOperator
    object DivisionSymbol : CalculatorDisplayItem("/"), CalculatorOperator
    data class Number(val num: String) : CalculatorDisplayItem(num), CalculatorOperand
    object LeftParentheses : CalculatorDisplayItem("("), CalculatorParentheses
    object RightParentheses: CalculatorDisplayItem(")"), CalculatorParentheses
}

sealed interface CalculatorOperand

sealed interface CalculatorOperator

sealed interface CalculatorSign

sealed interface CalculatorParentheses
