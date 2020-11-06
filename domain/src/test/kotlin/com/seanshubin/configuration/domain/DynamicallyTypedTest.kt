package com.seanshubin.configuration.domain

import com.seanshubin.configuration.domain.DynamicallyTyped.merge
import com.seanshubin.configuration.domain.DynamicallyTyped.mergeTypedWithDynamic
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class DynamicallyTypedTest {
    data class Point(val x: Int, val y: Int)

    @Test
    fun mergeNull() {
        assertEquals(null, merge(null, null))
    }

    @Test
    fun mergeString() {
        assertEquals(null, merge("aaa", null))
        assertEquals("ccc", merge("bbb", "ccc"))
        assertEquals("ddd", merge(null, "ddd"))
    }

    @Test
    fun mergeMap() {
        val left = mapOf(
                "a" to mapOf(
                        "b" to mapOf(
                                "c" to 1,
                                "d" to 2,
                                "e" to 3)))
        val right = mapOf(
                "a" to mapOf(
                        "b" to mapOf(
                                "d" to null,
                                "e" to 4,
                                "f" to 5)))
        val expected = mapOf(
                "a" to mapOf(
                        "b" to mapOf(
                                "c" to 1,
                                "e" to 4,
                                "f" to 5)))
        val actual = merge(left, right)
        assertEquals(expected, actual)
    }

    @Test
    fun overrideValue() {
        val point = Point(1, 2)
        val dynamic = mapOf("y" to 3)
        val expected = Point(1, 3)
        val actual = mergeTypedWithDynamic(point, dynamic)
        assertEquals(expected, actual)
    }

    @Test
    fun sensibleMessageWhenTypesDoNotMatch() {
        val point = Point(1, 2)
        val dynamic = mapOf("y" to "a")
        val expected = "At path [y], unable to replace [2] of type java.lang.Integer with [a] of type java.lang.String"
        val exception = assertFails {
            mergeTypedWithDynamic(point, dynamic)
        }
        val actual = exception.message
        assertEquals(expected, actual)
    }
}
