package com.example.composecalculator

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CalculatorViewModel : ViewModel() {
    var state by mutableStateOf(CalculatorState())
        private set

    fun onAction(action: CalculatorAction) {
        state = state.copy(error = null)
        when (action) {
            is CalculatorAction.Number -> enterNumber(action.number)
            is CalculatorAction.Decimal -> enterDecimal()
            is CalculatorAction.Clear -> state = CalculatorState()
            is CalculatorAction.Operation -> enterOperation(action.operation)
            is CalculatorAction.Calculate -> performCalculation()
            is CalculatorAction.Delete -> performDeletion()
            is CalculatorAction.Parentheses -> enterParentheses(action.parentheses)
        }
    }

    private fun performDeletion() {
        viewModelScope.launch(Dispatchers.IO) {
            if (state.currentToken.isNotEmpty()) {
                state = state.copy(
                    currentToken = state.currentToken.dropLast(1)
                )
            } else if (state.displayTokens.isNotEmpty()) {
                val lastDisplayToken = state.displayTokens.last()
                val displayTokens = state.displayTokens.dropLast(1).toMutableList()
                val newToken =
                    if (lastDisplayToken.displayStr == "Infinity" || lastDisplayToken.displayStr == "-Infinity") {
                        ""
                    } else {
                        lastDisplayToken.displayStr.dropLast(1)
                    }
                if (newToken.isNotEmpty()) {
                    displayTokens.add(CalculatorDisplayItem.Number(newToken))
                }
                state = state.copy(
                    displayTokens = displayTokens
                )
            }
        }
    }

    private fun performCalculation() {
        viewModelScope.launch(Dispatchers.IO) {
            val tokenList = state.displayTokens.toMutableList()
            if (state.currentToken.isNotEmpty()) {
                val currentToken = state.currentToken.toCalculatorDisplayItem() ?: return@launch
                tokenList.add(currentToken)
            }
            try {
                val result =
                    CalculatorUtils.evaluateInfix(tokenList).toString().removeTrailingZeroes()
                        .toCalculatorDisplayItem() ?: return@launch
                state = state.copy(
                    displayTokens = mutableListOf(result), currentToken = ""
                )
            } catch (ex: java.lang.IllegalArgumentException) {
                state = state.copy(
                    error = ex.message
                )
            } catch (ex: java.lang.Exception) {
                state = state.copy(
                    error = "Some error occurred!"
                )
            }
        }
    }

    private fun enterOperation(operation: CalculatorOperator) {
        viewModelScope.launch(Dispatchers.IO) {
            if ((operation as? CalculatorDisplayItem) == null) return@launch
            val tokensList = state.displayTokens.toMutableList()
            if (state.currentToken.isNotEmpty()) {
                val currentToken = state.currentToken.toCalculatorDisplayItem()
                if (currentToken != null) {
                    tokensList.add(currentToken)
                }
                tokensList.add(operation)
                state = state.copy(
                    displayTokens = tokensList, currentToken = ""
                )
            } else {
                val lastToken = tokensList.lastOrNull()
                if (lastToken == null) {
                    if (operation is CalculatorSign) {
                        state = state.copy(
                            currentToken = operation.displayStr
                        )
                    }
                    return@launch
                }
                if (lastToken is CalculatorOperator) return@launch
                tokensList.add(operation)
                state = state.copy(
                    displayTokens = tokensList
                )
            }
        }
    }

    private fun enterDecimal() {
        viewModelScope.launch(Dispatchers.IO) {
            if (state.currentToken.isEmpty() && state.displayTokens.lastOrNull() is CalculatorDisplayItem.Number) {
                val lastToken = state.displayTokens.last()
                if (lastToken.displayStr.contains(".")) return@launch
                val tokensList = state.displayTokens.dropLast(1)
                state = state.copy(
                    displayTokens = tokensList, currentToken = lastToken.displayStr + "."
                )
            } else if (!state.currentToken.contains(".")) {
                state = state.copy(
                    currentToken = state.currentToken + "."
                )
            }
        }
    }

    private fun enterNumber(number: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val lastNum = state.currentToken.filter {
                it.toString().toCalculatorDisplayItem() !is CalculatorOperator && it != '.'
            }
            if (lastNum.length < MAX_NUM_LENGTH) {
                state = state.copy(
                    currentToken = state.currentToken + number.toString()
                )
            }
        }
    }

    private fun enterParentheses(parentheses: CalculatorParentheses) {
        viewModelScope.launch(Dispatchers.IO) {
            if ((parentheses as? CalculatorDisplayItem) == null) return@launch
            val displayTokens = state.displayTokens.toMutableList()
            when (parentheses as CalculatorParentheses) {
                is CalculatorDisplayItem.LeftParentheses -> {
                    if (state.currentToken.isNotEmpty()) {
                        val currentDisplayItem = state.currentToken.toCalculatorDisplayItem()
                        if (currentDisplayItem is CalculatorDisplayItem.Number) {
                            displayTokens.add(currentDisplayItem)
                            displayTokens.add(CalculatorDisplayItem.MultiplySymbol)
                            displayTokens.add(parentheses)
                            state = state.copy(
                                displayTokens = displayTokens,
                                currentToken = ""
                            )
                        }
                    } else {
                        val lastToken = state.displayTokens.lastOrNull()
                        if (lastToken == null) {
                            displayTokens.add(parentheses)
                            state = state.copy(
                                displayTokens = displayTokens,
                                currentToken = ""
                            )
                        } else if (lastToken is CalculatorDisplayItem.Number) {
                            displayTokens.add(CalculatorDisplayItem.MultiplySymbol)
                            displayTokens.add(parentheses)
                            state = state.copy(
                                displayTokens = displayTokens,
                                currentToken = ""
                            )
                        } else {
                            displayTokens.add(parentheses)
                            state = state.copy(
                                displayTokens = displayTokens,
                                currentToken = ""
                            )
                        }
                    }
                }
                is CalculatorDisplayItem.RightParentheses -> {
                    if (state.currentToken.isNotEmpty()) {
                        val currentDisplayItem = state.currentToken.toCalculatorDisplayItem()
                        if (currentDisplayItem is CalculatorDisplayItem.Number) {
                            displayTokens.add(currentDisplayItem)
                            displayTokens.add(parentheses)
                            state = state.copy(
                                displayTokens = displayTokens,
                                currentToken = ""
                            )
                        }
                    } else {
                        val lastToken = state.displayTokens.lastOrNull() ?: return@launch
                        if (lastToken !is CalculatorDisplayItem.Number) return@launch
                        displayTokens.add(parentheses)
                        state = state.copy(
                            displayTokens = displayTokens,
                            currentToken = ""
                        )
                    }
                }
            }
        }
    }

    companion object {
        private const val MAX_NUM_LENGTH = 8
    }
}

fun String.removeTrailingZeroes(): String {
    if (contains(".")) {
        return dropLastWhile { it == '0' }.dropLastWhile { it == '.' }
    }
    return this
}

fun String.toCalculatorDisplayItem(): CalculatorDisplayItem? {
    return when (this) {
        "+" -> CalculatorDisplayItem.AddSymbol
        "-" -> CalculatorDisplayItem.MinusSymbol
        "x" -> CalculatorDisplayItem.MultiplySymbol
        "/" -> CalculatorDisplayItem.DivisionSymbol
        "(" -> CalculatorDisplayItem.LeftParentheses
        ")" -> CalculatorDisplayItem.RightParentheses
        else -> {
            val num = toDoubleOrNull()
            if (num == null) null
            else CalculatorDisplayItem.Number(this)
        }
    }
}