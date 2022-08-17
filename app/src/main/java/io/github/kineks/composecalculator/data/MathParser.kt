package io.github.kineks.composecalculator.data

import java.util.*
import kotlin.math.abs
import kotlin.math.sin

//  虽然自己试过实现一个, 但轮子没必要每个都重新造嘛
// 更改： 加了 % 求余
/**
 * 代码来自 https://gist.github.com/ichenhe/a438769a2c40947d2d3717541e21e007
 */
class MathParser {
    companion object {
        private const val ZERO_CODE = '0'.code // 便于通过字符串编码快速 char -> int

        private const val TOKEN_NULL: Short = -1
        private const val TOKEN_NUM:  Short = 1
        private const val TOKEN_LP:   Short = 2
        private const val TOKEN_RP:   Short = 3
        private const val TOKEN_OP:   Short = 4 // 操作符，包括函数操作符
        private const val TOKEN_SP:   Short = 4 // 函数分隔符

        private const val OP_LP:  Short = 0 // (
        private const val OP_ADD: Short = 1 // +
        private const val OP_MIN: Short = 2 // -
        private const val OP_MUL: Short = 3 // *
        private const val OP_DIV: Short = 4 // /
        private const val OP_N:   Short = 5 // ~ (负号)
        private const val OP_PER: Short = 6 // % (百分比/除以100)

        // ----------------以上 OP 的编号必须与 priority 数组下标一致
        private const val OP_FUN_ABS: Short = 10   // abs
        private const val OP_FUN_SIN: Short = 11   // sin

        /** 判断是否是函数操作符。 */
        private fun isFunOp(op: Short): Boolean = op >= 10
    }

    // 函数只能出现在数学模式中，函数的返回值必须是数字，且不可以与其他数字字符拼接成新数字。

    /**
     * 表示 `(+-x/~` 的优先级。`true` 表示高于。例如 `p[1][3]=true` 表示x的优先级大于+。其中 `~` 表示负号。
     *
     * 下标与 `OP_*` 常量一致。
     */
    private val priority = arrayOf(
        //               (         +         -         *          /          ~          %
        booleanArrayOf(false, /**/true, /**/true, /**/true,  /**/true,  /**/true,  /**/true), // (
        booleanArrayOf(false,    false,    false, /**/true,  /**/true,  /**/true,  /**/true), // +
        booleanArrayOf(false,    false,    false, /**/true,  /**/true,  /**/true,  /**/true), // -
        booleanArrayOf(false,    false,    false,     false,     false, /**/true,  /**/true), // *
        booleanArrayOf(false,    false,    false,     false,     false, /**/true,  /**/true), // /
        booleanArrayOf(false,    false,    false,     false,     false, /**/true,     false), // ~ (负号)
        booleanArrayOf(false,    false,    false,     false,     false, /**/true,  /**/true), // %
    )

    private val numStack = Stack<Double>()
    private val opStack = Stack<Short>()

    private var fraction = 0.0 // 当前已经识别到数字字符表示的小数位数，0表示未进入小数模式，0.1表示十分位
    private var fractionMode
        get() = fraction != 0.0
        set(value) {
            fraction = if (!value) {
                0.0
            } else {
                if (fraction == 0.0) 1.0
                else throw IllegalStateException()
            }
        }
    private var num = 0.0
    private var readingNumFlag = false // 是否正处于解析数字的过程中

    /** 用于解析标识符。 */
    private val idBuilder = StringBuilder(10)
    private var readingIdFlag = false // 是否正处于解析函数名的过程中


    fun parse(str: String): Double {
        val s = "($str)" // 为了算法一致性确保最外层由括号包裹
        var i = 0

        /** 上一个成功解析的单词类型，用于判定减号语义。 */
        var preTokenType = TOKEN_NULL

        while (i < s.length) {
            val c = s[i]

            if (c == ' ' || c == '\n' || c == '\t') {
                i++ // 跳过空白字符
                continue
            }
            if (c.isLetter() || (readingIdFlag && (c.isDigit() || c == '_'))) {
                // 标识符部分。标识符必须是字母开头，只能是字母数字下划线。
                readingIdFlag = true
                idBuilder.append(c)
                i++
                continue
            } else if (readingIdFlag) {
                // 结束标识符解析模式并压栈
                when (val id = idBuilder.toString()) {
                    "pi" -> {
                        preTokenType = TOKEN_NUM
                        numStack.push(Math.PI)
                    }
                    "e" -> {
                        preTokenType = TOKEN_NUM
                        numStack.push(Math.E)
                    }
                    "abs" -> {
                        preTokenType = TOKEN_OP
                        eatFunction(OP_FUN_ABS)
                    }
                    "sin" -> {
                        preTokenType = TOKEN_OP
                        eatFunction(OP_FUN_SIN)
                    }
                    else -> throw ParseException("Unknown identifier $id.")
                }
                resetReadIdFlag()
                // 当前字符尚未消耗，这里不能 continue
            }

            if (c == '.') {
                readingNumFlag = true
                if (fractionMode) throw ParseException() // 不能有连续的点.
                fractionMode = true
                i++
                continue
            }
            if (c.isDigit()) {
                readingNumFlag = true
                if (!fractionMode) {
                    num = num * 10 + c.code - ZERO_CODE
                } else {
                    fraction /= 10
                    num += (c.code - ZERO_CODE) * fraction
                }
                i++
                continue
            }
            // 不是数字或小数字符
            if (readingNumFlag) {
                // 结束数字解析模式并压栈
                preTokenType = TOKEN_NUM
                numStack.push(num)
                resetReadNumberFlag()
                // 当前字符尚未消耗，这里不能 continue
            }
            if (c == ',') {
                eatSeparator()
                preTokenType = TOKEN_SP
                i++
                continue
            }
            if (isOp(c)) {
                eatOp(c, preTokenType)
                preTokenType = when (c) {
                    '(' -> TOKEN_LP
                    ')' -> TOKEN_RP
                    else -> TOKEN_OP
                }
                i++
                continue
            }
        }
        return numStack.pop()
    }

    /**
     * 处理运算符。函数内不需要自行调用 `i++`。
     *
     * @param c 运算符，可能的值参考 [isOp]。
     * @param preToken 上一个解析的单词类型，值为 `TOKEN_` 常量。
     */
    private fun eatOp(c: Char, preToken: Short) {
        while (true) {
            if (c == ')') {
                // 将栈中元素依次出栈入队，直到左括号，括号出栈不入队。
                // 此时若栈顶元素是函数，则出栈入队
                while (!opStack.isEmpty() && opStack.peek() != OP_LP) {
                    calc(opStack.pop())
                }
                if (opStack.isEmpty())
                    throw ParseException("parentheses do not match.")
                opStack.pop()
                if (!opStack.isEmpty() && isFunOp(opStack.peek())) {
                    calc(opStack.pop())
                }
                return
            }
            var c1 = c
            if (c == '-') {
                c1 = if (preToken == TOKEN_NUM)
                    '-'
                else if (preToken == TOKEN_LP || preToken == TOKEN_OP || preToken == TOKEN_SP)
                    '~'
                else if (preToken == TOKEN_RP)
                    if (numStack.isEmpty()) '~' else '-'
                else
                    throw ParseException("Wrong '-' position.")
            }
            if (c1 == '(' || opStack.isEmpty() || opStack.peek() == OP_LP) {
                // 若A为空｜A是左括号｜B是左括号，则B入栈。
                opStack.push(op2d(c1))
                return
            }
            if (preThan(op2d(c1), opStack.peek())) {
                // 若B优先级大于A，则B入栈。
                opStack.push(op2d(c1))
                return
            }
            // 否则A出栈入队循环，直到满足上述条件之一。
            calc(opStack.pop())
        }
    }

    /**
     * 处理函数操作符。函数内不需要自行调用 `i++`。
     *
     * @param opFun 操作符代码。
     */
    private fun eatFunction(opFun: Short) {
        opStack.push(opFun)
    }

    /**
     * 处理函数参数分隔符。函数内不需要自行调用 `i++`。
     */
    private fun eatSeparator() {
        while (opStack.peek() != OP_LP) {
            calc(opStack.pop())
        }
    }

    /**
     * 根据给定操作符执行操作。自行判断 [numStack] 状态并进行相应操作。
     *
     * @param op 操作符。
     */
    private fun calc(op: Short) {
        when (op) {
            OP_ADD -> numStack.push(numStack.pop() + numStack.pop())
            OP_MIN -> {
                val t = numStack.pop()
                numStack.push(numStack.pop() - t)
            }
            OP_MUL -> numStack.push(numStack.pop() * numStack.pop())
            OP_DIV -> {
                val t = numStack.pop()
                numStack.push(numStack.pop() / t)
            }
            OP_PER -> {
                numStack.push(numStack.pop() / 100)
            }
            OP_N -> {
                numStack.push(-1 * numStack.pop())
            }
            OP_FUN_ABS -> numStack.push(abs(numStack.pop()))
            OP_FUN_SIN -> numStack.push(sin(numStack.pop()))
            else -> throw ParseException("Cannot recognize operator $op. ")
        }
    }

    /** 判断是否是运算符，包括括号()不包括点.。*/
    private fun isOp(c: Char): Boolean = c == '(' || c == ')' || c == '+' || c == '-' || c == '*' || c == '/' || c == '%'

    /** 将操作符转为数字以便高效存入[opStack]。 */
    private fun op2d(c: Char): Short = when (c) {
        '(' -> OP_LP
        '+' -> OP_ADD
        '-' -> OP_MIN
        '*' -> OP_MUL
        '/' -> OP_DIV
        '~' -> OP_N
        '%' -> OP_PER
        else -> throw IllegalArgumentException("$c is not an operator")
    }

    /** 判断 [op1] 是否比 [op2] 优先级更高。不支持函数操作符。 */
    private fun preThan(op1: Short, op2: Short): Boolean {
        if (isFunOp(op1) || isFunOp(op2)) throw IllegalArgumentException("Function op is not supported.")
        return priority[op2.toInt()][op1.toInt()]
    }

    /**
     * 重置解析数字标记。将解除数字解析状态并重置相关变量。
     */
    private fun resetReadNumberFlag() {
        fractionMode = false
        readingNumFlag = false
        num = 0.0
    }

    /**
     * 重置解析标识符标记。
     */
    private fun resetReadIdFlag() {
        readingIdFlag = false
        idBuilder.clear()
    }

    class ParseException(s: String? = null) : RuntimeException(s)
}