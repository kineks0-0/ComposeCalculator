package io.github.kineks.composecalculator

import io.github.kineks.composecalculator.data.eval
import io.github.kineks.composecalculator.data.make
import io.github.kineks.composecalculator.data.parse
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun test() {
        println(eval(listOf(
            1.make("+"),
            1.make("-"),
            2.make("*"),
            3.make("/"),
            4.make("%")
        )))
        println(
            "1+1+1+1-2*3/4".eval()
        )
        println(
            "1+1+1+1-2*3/4".parse()
        )
    }
}