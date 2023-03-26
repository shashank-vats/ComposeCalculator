package com.example.composecalculator

import java.lang.IllegalArgumentException
import java.util.Stack

object CalculatorUtils {
    private val opPrecedence = mapOf(
        CalculatorDisplayItem.AddSymbol to 1,
        CalculatorDisplayItem.MinusSymbol to 1,
        CalculatorDisplayItem.MultiplySymbol to 2,
        CalculatorDisplayItem.DivisionSymbol to 3
    )

    private val CalculatorDisplayItem.precedence: Int
        get() = opPrecedence[this] ?: -1

    private fun convertInfixToPostFix(infix: List<CalculatorDisplayItem>): List<CalculatorDisplayItem> {
        val opStack = Stack<CalculatorDisplayItem>()
        val outputList = mutableListOf<CalculatorDisplayItem>()
        infix.forEach { token ->
            when (token) {
                is CalculatorOperand -> {
                    outputList.add(token)
                }
                is CalculatorOperator -> {
                    while (opStack.isNotEmpty() && opStack.peek() is CalculatorOperator && opStack.peek().precedence >= token.precedence) {
                        outputList.add(opStack.pop())
                    }
                    opStack.push(token)
                }
                is CalculatorDisplayItem.LeftParentheses -> {
                    opStack.push(token)
                }
                is CalculatorDisplayItem.RightParentheses -> {
                    while (opStack.isNotEmpty() && opStack.peek() !is CalculatorDisplayItem.LeftParentheses) {
                        outputList.add(opStack.pop())
                    }
                }
            }
        }
        while (opStack.isNotEmpty()) {
            outputList.add(opStack.pop())
        }
        return outputList
    }

    private fun evaluatePostFix(postFix: List<CalculatorDisplayItem>): Double {
        val operandStack = Stack<Double>()
        postFix.forEach { token ->
            when (token) {
                is CalculatorDisplayItem.Number -> {
                    operandStack.push(token.num.toDoubleOrNull())
                }
                is CalculatorDisplayItem.AddSymbol -> {
                    if (operandStack.size < 2) {
                        throw IllegalArgumentException("Expression not correct!")
                    }
                    val operand2 = operandStack.pop()
                    val operand1 = operandStack.pop()
                    if (operand1 != null && operand2 != null) {
                        operandStack.push(operand1 + operand2)
                    }
                }
                is CalculatorDisplayItem.MinusSymbol -> {
                    if (operandStack.size < 2) {
                        throw IllegalArgumentException("Expression not correct!")
                    }
                    val operand2 = operandStack.pop()
                    val operand1 = operandStack.pop()
                    if (operand1 != null && operand2 != null) {
                        operandStack.push(operand1 - operand2)
                    }
                }
                is CalculatorDisplayItem.MultiplySymbol -> {
                    if (operandStack.size < 2) {
                        throw IllegalArgumentException("Expression not correct!")
                    }
                    val operand2 = operandStack.pop()
                    val operand1 = operandStack.pop()
                    if (operand1 != null && operand2 != null) {
                        operandStack.push(operand1 * operand2)
                    }
                }
                is CalculatorDisplayItem.DivisionSymbol -> {
                    if (operandStack.size < 2) {
                        throw IllegalArgumentException("Expression not correct!")
                    }
                    val operand2 = operandStack.pop()
                    val operand1 = operandStack.pop()
                    if (operand1 != null && operand2 != null) {
                        operandStack.push(operand1 / operand2)
                    }
                }
                else -> {

                }
            }
        }
        if (operandStack.size != 1) {
            throw java.lang.IllegalArgumentException("Expression not correct!")
        }
        return operandStack.pop()
    }

    fun evaluateInfix(infix: List<CalculatorDisplayItem>): Double {
        return evaluatePostFix(convertInfixToPostFix(infix))
    }
}