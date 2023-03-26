package com.example.composecalculator.ui

import androidx.compose.ui.graphics.Color
import com.example.composecalculator.calculator.models.CalculatorAction
import com.example.composecalculator.calculator.models.CalculatorDisplayItem
import com.example.composecalculator.ui.theme.LightGray
import com.example.composecalculator.ui.theme.Orange

object CalculatorUtils {
    fun getCalculatorButtonsInGrid(
        onClick: (CalculatorAction) -> Unit, columnCount: Int = 4
    ): List<List<CalculatorButton>> {
        return getCalculatorButtons(onClick).withIndex().groupBy {
            it.index / columnCount
        }.map { it.value.map { it.value } }
    }

    fun getCalculatorButtons(
        onClick: (CalculatorAction) -> Unit
    ): List<CalculatorButton> {
        return listOf(
            CalculatorButton(symbol = "AC", buttonColor = LightGray, onClick = {
                onClick(CalculatorAction.Clear)
            }),
            CalculatorButton(symbol = "(", buttonColor = Color.DarkGray, onClick = {
                onClick(CalculatorAction.Parentheses(CalculatorDisplayItem.LeftParentheses))
            }),
            CalculatorButton(symbol = ")", buttonColor = Color.DarkGray, onClick = {
                onClick(CalculatorAction.Parentheses(CalculatorDisplayItem.RightParentheses))
            }),
            CalculatorButton(symbol = "Del", buttonColor = LightGray, onClick = {
                onClick(CalculatorAction.Delete)
            }) {
                onClick(CalculatorAction.Clear)
            },
            CalculatorButton(symbol = "7", buttonColor = Color.DarkGray, onClick = {
                onClick(CalculatorAction.Number(7))
            }),
            CalculatorButton(symbol = "8", buttonColor = Color.DarkGray, onClick = {
                onClick(CalculatorAction.Number(8))
            }),
            CalculatorButton(symbol = "9", buttonColor = Color.DarkGray, onClick = {
                onClick(CalculatorAction.Number(9))
            }),
            CalculatorButton(symbol = "/", buttonColor = Orange, onClick = {
                onClick(CalculatorAction.Operation(CalculatorDisplayItem.DivisionSymbol))
            }),
            CalculatorButton(symbol = "4", buttonColor = Color.DarkGray, onClick = {
                onClick(CalculatorAction.Number(4))
            }),
            CalculatorButton(symbol = "5", buttonColor = Color.DarkGray, onClick = {
                onClick(CalculatorAction.Number(5))
            }),
            CalculatorButton(symbol = "6", buttonColor = Color.DarkGray, onClick = {
                onClick(CalculatorAction.Number(6))
            }),
            CalculatorButton(symbol = "x", buttonColor = Orange, onClick = {
                onClick(CalculatorAction.Operation(CalculatorDisplayItem.MultiplySymbol))
            }),
            CalculatorButton(symbol = "1", buttonColor = Color.DarkGray, onClick = {
                onClick(CalculatorAction.Number(1))
            }),
            CalculatorButton(symbol = "2", buttonColor = Color.DarkGray, onClick = {
                onClick(CalculatorAction.Number(2))
            }),
            CalculatorButton(symbol = "3", buttonColor = Color.DarkGray, onClick = {
                onClick(CalculatorAction.Number(3))
            }),
            CalculatorButton(symbol = "-", buttonColor = Orange, onClick = {
                onClick(CalculatorAction.Operation(CalculatorDisplayItem.MinusSymbol))
            }),
            CalculatorButton(symbol = "0", buttonColor = Color.DarkGray, onClick = {
                onClick(CalculatorAction.Number(0))
            }),
            CalculatorButton(symbol = ".", buttonColor = Color.DarkGray, onClick = {
                onClick(CalculatorAction.Decimal)
            }),
            CalculatorButton(symbol = "=", buttonColor = Orange, onClick = {
                onClick(CalculatorAction.Calculate)
            }),
            CalculatorButton(symbol = "+", buttonColor = Orange, onClick = {
                onClick(CalculatorAction.Operation(CalculatorDisplayItem.AddSymbol))
            }),
        )
    }
}