package com.example.composecalculator.calculator

import com.example.composecalculator.calculator.models.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class Calculator(private val coroutineScope: CoroutineScope) {
    private val _calculatorState: MutableStateFlow<CalculatorState> =
        MutableStateFlow(CalculatorState())
    val calculatorState: StateFlow<CalculatorState> = _calculatorState

    fun onAction(action: CalculatorAction) {
        _calculatorState.value = _calculatorState.value.copy(error = null)
        when (action) {
            is CalculatorAction.Number -> enterNumber(action.number)
            is CalculatorAction.Decimal -> enterDecimal()
            is CalculatorAction.Clear -> _calculatorState.value = CalculatorState()
            is CalculatorAction.Operation -> enterOperation(action.operation)
            is CalculatorAction.Calculate -> performCalculation()
            is CalculatorAction.Delete -> performDeletion()
            is CalculatorAction.Parentheses -> enterParentheses(action.parentheses)
        }
    }

    private fun performDeletion() {
        coroutineScope.launch(Dispatchers.IO) {
            if (_calculatorState.value.currentToken.isNotEmpty()) {
                _calculatorState.value = _calculatorState.value.copy(
                    currentToken = _calculatorState.value.currentToken.dropLast(1)
                )
            } else if (_calculatorState.value.displayTokens.isNotEmpty()) {
                val lastDisplayToken = _calculatorState.value.displayTokens.last()
                val displayTokens = _calculatorState.value.displayTokens.dropLast(1).toMutableList()
                val newToken =
                    if (lastDisplayToken.displayStr == "Infinity" || lastDisplayToken.displayStr == "-Infinity") {
                        ""
                    } else {
                        lastDisplayToken.displayStr.dropLast(1)
                    }
                if (newToken.isNotEmpty()) {
                    displayTokens.add(CalculatorDisplayItem.Number(newToken))
                }
                _calculatorState.value = _calculatorState.value.copy(
                    displayTokens = displayTokens
                )
            }
        }
    }

    private fun performCalculation() {
        coroutineScope.launch(Dispatchers.IO) {
            val tokenList = _calculatorState.value.displayTokens.toMutableList()
            if (_calculatorState.value.currentToken.isNotEmpty()) {
                val currentToken =
                    _calculatorState.value.currentToken.toCalculatorDisplayItem() ?: return@launch
                tokenList.add(currentToken)
            }
            try {
                val result =
                    CalculatorUtils.evaluateInfix(tokenList).toString().removeTrailingZeroes()
                        .toCalculatorDisplayItem() ?: return@launch
                _calculatorState.value = _calculatorState.value.copy(
                    displayTokens = mutableListOf(result), currentToken = ""
                )
            } catch (ex: java.lang.IllegalArgumentException) {
                _calculatorState.value = _calculatorState.value.copy(
                    error = ex.message
                )
            } catch (ex: java.lang.Exception) {
                _calculatorState.value = _calculatorState.value.copy(
                    error = "Some error occurred!"
                )
            }
        }
    }

    private fun enterOperation(operation: CalculatorOperator) {
        coroutineScope.launch(Dispatchers.IO) {
            if ((operation as? CalculatorDisplayItem) == null) return@launch
            val tokensList = _calculatorState.value.displayTokens.toMutableList()
            if (_calculatorState.value.currentToken.isNotEmpty()) {
                val currentToken = _calculatorState.value.currentToken.toCalculatorDisplayItem()
                if (currentToken != null) {
                    tokensList.add(currentToken)
                }
                tokensList.add(operation)
                _calculatorState.value = _calculatorState.value.copy(
                    displayTokens = tokensList, currentToken = ""
                )
            } else {
                val lastToken = tokensList.lastOrNull()
                if (lastToken == null) {
                    if (operation is CalculatorSign) {
                        _calculatorState.value = _calculatorState.value.copy(
                            currentToken = operation.displayStr
                        )
                    }
                    return@launch
                }
                if (lastToken is CalculatorOperator) return@launch
                tokensList.add(operation)
                _calculatorState.value = _calculatorState.value.copy(
                    displayTokens = tokensList
                )
            }
        }
    }

    private fun enterDecimal() {
        coroutineScope.launch(Dispatchers.IO) {
            if (_calculatorState.value.currentToken.isEmpty() && _calculatorState.value.displayTokens.lastOrNull() is CalculatorDisplayItem.Number) {
                val lastToken = _calculatorState.value.displayTokens.last()
                if (lastToken.displayStr.contains(".")) return@launch
                val tokensList = _calculatorState.value.displayTokens.dropLast(1)
                _calculatorState.value = _calculatorState.value.copy(
                    displayTokens = tokensList, currentToken = lastToken.displayStr + "."
                )
            } else if (!_calculatorState.value.currentToken.contains(".")) {
                _calculatorState.value = _calculatorState.value.copy(
                    currentToken = _calculatorState.value.currentToken + "."
                )
            }
        }
    }

    private fun enterNumber(number: Int) {
        coroutineScope.launch(Dispatchers.IO) {
            val lastNum = _calculatorState.value.currentToken.filter {
                it.toString().toCalculatorDisplayItem() !is CalculatorOperator && it != '.'
            }
            if (lastNum.length < MAX_NUM_LENGTH) {
                _calculatorState.value = _calculatorState.value.copy(
                    currentToken = _calculatorState.value.currentToken + number.toString()
                )
            }
        }
    }

    private fun enterParentheses(parentheses: CalculatorParentheses) {
        coroutineScope.launch(Dispatchers.IO) {
            if ((parentheses as? CalculatorDisplayItem) == null) return@launch
            val displayTokens = _calculatorState.value.displayTokens.toMutableList()
            when (parentheses as CalculatorParentheses) {
                is CalculatorDisplayItem.LeftParentheses -> {
                    if (_calculatorState.value.currentToken.isNotEmpty()) {
                        val currentDisplayItem =
                            _calculatorState.value.currentToken.toCalculatorDisplayItem()
                        if (currentDisplayItem is CalculatorDisplayItem.Number) {
                            displayTokens.add(currentDisplayItem)
                            displayTokens.add(CalculatorDisplayItem.MultiplySymbol)
                            displayTokens.add(parentheses)
                            _calculatorState.value = _calculatorState.value.copy(
                                displayTokens = displayTokens,
                                currentToken = ""
                            )
                        }
                    } else {
                        val lastToken = _calculatorState.value.displayTokens.lastOrNull()
                        if (lastToken == null) {
                            displayTokens.add(parentheses)
                            _calculatorState.value = _calculatorState.value.copy(
                                displayTokens = displayTokens,
                                currentToken = ""
                            )
                        } else if (lastToken is CalculatorDisplayItem.Number) {
                            displayTokens.add(CalculatorDisplayItem.MultiplySymbol)
                            displayTokens.add(parentheses)
                            _calculatorState.value = _calculatorState.value.copy(
                                displayTokens = displayTokens,
                                currentToken = ""
                            )
                        } else {
                            displayTokens.add(parentheses)
                            _calculatorState.value = _calculatorState.value.copy(
                                displayTokens = displayTokens,
                                currentToken = ""
                            )
                        }
                    }
                }
                is CalculatorDisplayItem.RightParentheses -> {
                    if (_calculatorState.value.currentToken.isNotEmpty()) {
                        val currentDisplayItem =
                            _calculatorState.value.currentToken.toCalculatorDisplayItem()
                        if (currentDisplayItem is CalculatorDisplayItem.Number) {
                            displayTokens.add(currentDisplayItem)
                            displayTokens.add(parentheses)
                            _calculatorState.value = _calculatorState.value.copy(
                                displayTokens = displayTokens,
                                currentToken = ""
                            )
                        }
                    } else {
                        val lastToken =
                            _calculatorState.value.displayTokens.lastOrNull() ?: return@launch
                        if (lastToken !is CalculatorDisplayItem.Number) return@launch
                        displayTokens.add(parentheses)
                        _calculatorState.value = _calculatorState.value.copy(
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