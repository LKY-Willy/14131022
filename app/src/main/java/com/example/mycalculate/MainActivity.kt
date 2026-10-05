package com.example.mycalculate

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.math.BigDecimal

class MainActivity : AppCompatActivity() {

    private lateinit var tvExpression: TextView
    private lateinit var tvDisplay: TextView

    private var expression = ""
    private var displayValue = "0"
    private var firstOperand: Double? = null
    private var operator: String? = null
    private var isNewInput = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvExpression = findViewById(R.id.tvExpression)
        tvDisplay = findViewById(R.id.tvDisplay)

        // Digit buttons
        val digitButtons = mapOf(
            R.id.btn0 to "0",
            R.id.btn1 to "1",
            R.id.btn2 to "2",
            R.id.btn3 to "3",
            R.id.btn4 to "4",
            R.id.btn5 to "5",
            R.id.btn6 to "6",
            R.id.btn7 to "7",
            R.id.btn8 to "8",
            R.id.btn9 to "9"
        )

        for ((id, digit) in digitButtons) {
            findViewById<Button>(id).setOnClickListener { onDigitClick(digit) }
        }

        findViewById<Button>(R.id.btnDecimal).setOnClickListener { onDecimalClick() }
        findViewById<Button>(R.id.btnAllClear).setOnClickListener { onAllClearClick() }
        findViewById<Button>(R.id.btnClear).setOnClickListener { onClearClick() }
        findViewById<Button>(R.id.btnBackspace).setOnClickListener { onBackspaceClick() }

        // Operator buttons
        val operatorButtons = mapOf(
            R.id.btnAdd to "+",
            R.id.btnSubtract to "-",
            R.id.btnMultiply to "×",
            R.id.btnDivide to "÷"
        )

        for ((id, op) in operatorButtons) {
            findViewById<Button>(id).setOnClickListener { onOperatorClick(op) }
        }

        findViewById<Button>(R.id.btnEqual).setOnClickListener { onEqualClick() }
    }

    private fun updateDisplay() {
        tvExpression.text = expression
        tvDisplay.text = displayValue
    }

    private fun formatResult(value: Double): String {
        if (value.isNaN() || value.isInfinite()) return "Error"
        return try {
            val bd = BigDecimal(value.toString()).stripTrailingZeros()
            bd.toPlainString()
        } catch (_: Exception) {
            value.toString()
        }
    }

    private fun calculate(a: Double, b: Double, op: String): Double? {
        return when (op) {
            "+" -> a + b
            "-" -> a - b
            "×" -> a * b
            "÷" -> if (b == 0.0) null else a / b
            else -> null
        }
    }

    private fun onDigitClick(digit: String) {
        if (isNewInput || displayValue == "0") {
            displayValue = digit
            isNewInput = false
        } else {
            if (displayValue.length < 12) {
                displayValue += digit
            }
        }
        updateDisplay()
    }

    private fun onDecimalClick() {
        if (isNewInput) {
            displayValue = "0."
            isNewInput = false
        } else if (!displayValue.contains(".")) {
            displayValue += "."
        }
        updateDisplay()
    }

    private fun onClearClick() {
        displayValue = "0"
        isNewInput = true
        updateDisplay()
    }

    private fun onAllClearClick() {
        expression = ""
        displayValue = "0"
        firstOperand = null
        operator = null
        isNewInput = true
        updateDisplay()
    }

    private fun onBackspaceClick() {
        if (isNewInput) return
        if (displayValue.length > 1) {
            displayValue = displayValue.dropLast(1)
            if (displayValue == "-") displayValue = "0"
        } else {
            displayValue = "0"
            isNewInput = true
        }
        updateDisplay()
    }

    private fun onOperatorClick(op: String) {
        val currentVal = displayValue.toDoubleOrNull() ?: return
        if (firstOperand != null && operator != null && !isNewInput) {
            val result = calculate(firstOperand!!, currentVal, operator!!)
            if (result == null) {
                displayValue = "Error"
                firstOperand = null
                operator = null
                isNewInput = true
                updateDisplay()
                return
            }
            firstOperand = result
            displayValue = formatResult(result)
        } else {
            firstOperand = currentVal
        }
        operator = op
        expression = "${formatResult(firstOperand!!)} $op"
        isNewInput = true
        updateDisplay()
    }

    private fun onEqualClick() {
        if (firstOperand != null && operator != null) {
            val secondOperand = displayValue.toDoubleOrNull() ?: return
            val result = calculate(firstOperand!!, secondOperand, operator!!)
            expression = "${formatResult(firstOperand!!)} $operator ${formatResult(secondOperand)} ="
            if (result == null) {
                displayValue = "Error"
            } else {
                displayValue = formatResult(result)
            }
            firstOperand = null
            operator = null
            isNewInput = true
            updateDisplay()
        }
    }
}
