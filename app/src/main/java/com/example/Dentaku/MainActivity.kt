package com.example.Dentaku

import Dentaku.R
import android.annotation.SuppressLint
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import net.objecthunter.exp4j.ExpressionBuilder
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainScreen.isSelected = true
        btn0.setOnClickListener { makeCommand("0") }
        btn1.setOnClickListener { makeCommand("1") }
        btn2.setOnClickListener { makeCommand("2") }
        btn3.setOnClickListener { makeCommand("3") }
        btn4.setOnClickListener { makeCommand("4") }
        btn5.setOnClickListener { makeCommand("5") }
        btn6.setOnClickListener { makeCommand("6") }
        btn7.setOnClickListener { makeCommand("7") }
        btn8.setOnClickListener { makeCommand("8") }
        btn9.setOnClickListener { makeCommand("9") }
        bt00.setOnClickListener { makeCommand("00") }
        btnDot.setOnClickListener { makeCommand(".") }
        btnClear.setOnClickListener { makeCommand("clear") }
        btnBackspace.setOnClickListener { makeCommand("backspace") }
        btnPercent.setOnClickListener { makeCommand("%") }
        btnDivide.setOnClickListener { makeCommand("÷") }
        btnMultiply.setOnClickListener { makeCommand("×") }
        btnMinus.setOnClickListener { makeCommand("-") }
        btnPlus.setOnClickListener { makeCommand("+") }
        btnEqual.setOnClickListener { makeCommand("=") }
    }

    //---------------------- ANIMATION----------------------


    @SuppressLint("ClickableViewAccessibility")
    fun buttonEffect(button: View) {
        button.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.background.setColorFilter(-0x1f0b8adf, PorterDuff.Mode.SRC_ATOP)
                    v.invalidate()
                }
                MotionEvent.ACTION_UP -> {
                    v.background.clearColorFilter()
                    v.invalidate()
                }
            }
            false
        }
    }


    //------------------------ANIMATION END--------------------

    private fun makeCommand(p0: String) {
        if (mainScreen.text.toString().toIntOrNull() == 0) {
            mainScreen.text = ""
            findResult()
        }

        if (mainScreen.text.toString().isEmpty()) {
            when (p0) {
                "1", "2", "3", "4", "5", "6", "7", "8", "9", "0" -> {
                    mainScreen.append(p0)
                    findResult()
                }
                "00" -> {
                    mainScreen.append("0")
                    findResult()
                }
                "-" -> {
                    mainScreen.append(p0)
                    findResult()
                }
                "." -> {
                    mainScreen.append("0.")
                    findResult()
                }
            }

        } else {

            when (p0) {
                "clear" -> {
                    mainScreen.text = ""
                    preResult.text = ""
                }
                "backspace" -> {
                    mainScreen.text = mainScreen.text.toString()
                        .substring(0, mainScreen.text.toString().length - 1)
                    findResult()
                }
                "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "00" -> {
                    if ((!isNotDot(mainScreen.text.toString())) || isNotZero(
                            mainScreen.text.toString()
                        )
                    ) {
                        mainScreen.append(p0)
                        findResult()
                    } else {
                        mainScreen.text = mainScreen.text.toString()
                            .substring(0, mainScreen.text.toString().length - 1)
                        if (p0 == "00") {
                            mainScreen.append("0")
                            findResult()
                        } else {
                            mainScreen.append(p0)
                            findResult()

                        }
                    }
                }
                "." -> {
                    if (isLastCharOperator(mainScreen.text.toString())) {
                        mainScreen.append("0.")
                    } else if (isNotDot(mainScreen.text.toString()) && isNotLastCharDot(
                            mainScreen.text.toString()
                        )
                    ) {
                        mainScreen.append(p0)
                    }
                }
                "%", "÷", "×", "+" -> {
                    if (isLastCharOperator(mainScreen.text.toString()) && mainScreen.text.toString() != "-" && isNotLastOperatorMinusWithAdjacentOperator(
                            mainScreen.text.toString()
                        )
                    ) {
                        mainScreen.text = mainScreen.text.toString()
                            .substring(0, mainScreen.text.toString().length - 1)
                        mainScreen.append(p0)
                    } else if (mainScreen.text.toString() != "-" && isNotLastOperatorMinusWithAdjacentOperator(
                            mainScreen.text.toString()
                        )
                    ) {
                        mainScreen.append(p0)
                    } else if (!isNotLastOperatorMinusWithAdjacentOperator(mainScreen.text.toString())) {
                        mainScreen.text = mainScreen.text.toString()
                            .substring(0, mainScreen.text.toString().length - 2)
                        mainScreen.append(p0)
                    }
                }
                "-" -> {
                    if (isNotMinus(mainScreen.text.toString()) && isNotLastCharDot((mainScreen.text.toString())) && !isLastCharPlus()) {
                        mainScreen.append(p0)
                    } else if (isLastCharPlus()) {
                        mainScreen.text = mainScreen.text.toString()
                            .substring(0, mainScreen.text.toString().length - 1)
                        mainScreen.append(p0)
                    }
                }
                "=" -> {
                    applyResult()
                }
            }
        }
    }

    private fun isNotLastOperatorMinusWithAdjacentOperator(p0: String): Boolean {
        val regex = Regex("(?<=\\D)-$")
        val output: String? = regex.find(p0)?.value
        return output == null
    }

    private fun applyResult() {
        try {
            val p0 = mainScreen.text.toString()
            var inputExpression = p0.replace('×', '*')
            inputExpression = inputExpression.replace('÷', '/')
            val expression = ExpressionBuilder(inputExpression).build()
            val result = expression.evaluate()
            val longResult = result.toLong()
            if (result == longResult.toDouble()) {
                preResult.text = longResult.toString()
            } else {
                preResult.text = result.toString()
            }
            mainScreen.text = preResult.text
            preResult.text = ""
        } catch (e: Exception) {
            Log.e("Exception", "message" + e.message)
            if (e.message == "Expression can not be empty")
                preResult.text = ""
        }
    }

    private fun isLastCharPlus(): Boolean {
        return mainScreen.text.toString().isNotEmpty() && mainScreen.text.toString()
            .takeLast(1) == "+"
    }

    private fun isNotZero(p0: String): Boolean {
        val regex = Regex("\\d+$")
        val output: String? = regex.find(p0)?.value
        return output?.toIntOrNull() != 0
    }

    private fun isLastCharOperator(p0: String): Boolean {
        val regex = Regex("\\D$")
        val output: String? = regex.find(p0)?.value
        return output != null
    }

    private fun isNotLastCharDot(p0: String): Boolean {
        val regex = Regex("\\.$")
        val output: String? = regex.find(p0)?.value
        return output == null
    }

    private fun isNotMinus(p0: String): Boolean {
        val regex = Regex("-$")
        val output: String? = regex.find(p0)?.value
        return output == null
    }

    private fun isNotDot(p0: String): Boolean {
        val regex = Regex("\\.(?:\\d)+$")
        val output: String? = regex.find(p0)?.value
        return output == null
    }

    private fun findResult() {
        try {
            val p0 = mainScreen.text.toString()
            var inputExpression = p0.replace('×', '*')
            inputExpression = inputExpression.replace('÷', '/')
            val expression = ExpressionBuilder(inputExpression).build()
            val result = expression.evaluate()
            val longResult = result.toLong()
            if (result == longResult.toDouble()) {
                preResult.text = longResult.toString()
            } else {
                preResult.text = result.toString()
            }
        } catch (e: Exception) {
            Log.e("Exception", "message" + e.message)
            if (e.message == "Expression can not be empty")
                preResult.text = ""
        }
    }
}
