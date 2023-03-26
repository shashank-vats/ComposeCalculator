package com.example.composecalculator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composecalculator.calculator.Calculator
import com.example.composecalculator.calculator.models.CalculatorAction
import com.example.composecalculator.calculator.models.CalculatorState
import kotlinx.coroutines.flow.StateFlow

class CalculatorViewModel : ViewModel() {

    private val calculator = Calculator(viewModelScope)

    val state: StateFlow<CalculatorState>
        get() = calculator.calculatorState

    fun onAction(action: CalculatorAction) {
        calculator.onAction(action)
    }
}