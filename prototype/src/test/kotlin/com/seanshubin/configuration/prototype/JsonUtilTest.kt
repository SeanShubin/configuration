package com.seanshubin.configuration.prototype

import com.fasterxml.jackson.databind.Module
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import kotlin.test.Test
import kotlin.test.assertEquals

class JsonUtilTest {
    val kotlinModule: Module = KotlinModule()
    val concise: ObjectMapper = ObjectMapper().registerModule(kotlinModule)
    val parser: ObjectMapper = concise
    val pretty: ObjectMapper = ObjectMapper().registerModule(kotlinModule).enable(SerializationFeature.INDENT_OUTPUT)


    fun String.parseJson(): Any? = parser.readValue(this)
    fun Any?.toConciseJson(): String = concise.writeValueAsString(this)
    fun Any?.toPrettyJson(): String = pretty.writeValueAsString(this)
    fun Any?.toStringAndType(): String = "$this:${this?.javaClass?.simpleName ?: "null"}"
    fun Any?.toKotlinLiteral(): String =
            when(this){
                is String -> "\"$this\""
                else -> toString()
            }
    fun mapToPrimitiveEntries(path: List<String>, map: Map<Any?, Any?>): List<Pair<List<String>, Any?>> {
        val keys = map.keys
        return keys.flatMap {
            if (it !is String) throw RuntimeException("Only string keys supported, got ${it.toStringAndType()} at $path")
            val newPath = path + it
            val value = map[it]
            toPrimitiveEntries(newPath, value)
        }
    }

    fun listToPrimitiveEntries(path: List<String>, list: List<Any?>): List<Pair<List<String>, Any?>> {
        return list.flatMapIndexed { index, value ->
            val newPath = path + index.toString()
            toPrimitiveEntries(newPath, value)
        }
    }

    fun toPrimitiveEntries(path: List<String>, value: Any?): List<Pair<List<String>, Any?>> {
        @Suppress("UNCHECKED_CAST")
        return when (value) {
            is Map<*, *> -> mapToPrimitiveEntries(path, value as Map<Any?, Any?>)
            is List<*> -> listToPrimitiveEntries(path, value as List<Any?>)
            is Int, is Long, is Double, is String, is Boolean, null -> listOf(Pair(path, value))
            else -> throw RuntimeException("Unsupported: ${value.toStringAndType()}")
        }
    }

    fun Any?.toPrimitiveMap(): Map<List<String>, Any?> = toPrimitiveEntries(listOf(), this).toMap()
    fun createObject(path: List<Any>, value: Any?): Any? =
            if (path.isEmpty()) value;
            else when (val key = path[0]) {
                is Int -> (1..key).map { null } + listOf(createObject(path.slice(1 until path.size), value))
                is String -> mapOf(path[0] to createObject(path.slice(1 until path.size), value))
                else -> throw RuntimeException("Index must be string or int, got ${key.toStringAndType()}")
            }

    fun mergeObjects(left: Any?, right: Any?): Any? {
        if (left is Map<*, *> && right is Map<*, *>) {
            @Suppress("UNCHECKED_CAST")
            left as Map<Any?, Any?>
            @Suppress("UNCHECKED_CAST")
            right as Map<Any?, Any?>
            return mergeMaps(left, right)
        } else if (left is Int? && right is Int?) {
            return right
        } else {
            throw RuntimeException("Can't merge ${left.toStringAndType()} with ${right.toStringAndType()}")
        }
    }

    fun mergeMaps(left: Map<Any?, Any?>, right: Map<Any?, Any?>): Map<Any?, Any?> {
        val keys = (left.keys + right.keys).distinct()
        val pairs: List<Pair<Any?, Any?>?> = keys.map {
            if (left.containsKey(it) && !right.containsKey(it)) {
                it to left[it]
            } else if (left.containsKey(it) && !right.containsKey(it)) {
                it to right[it]
            } else if (right.containsKey(it) && right[it] != null) {
                it to mergeObjects(left[it], right[it])
            } else {
                null
            }
        }
        return pairs.filterNotNull().toMap()
    }

    @Test
    fun parseJson() {
        assertEquals(null, "null".parseJson())
        assertEquals("hello", "\"hello\"".parseJson())
        assertEquals(123, "123".parseJson())
        assertEquals(9223372036854775807, "9223372036854775807".parseJson())
        assertEquals(12.34, "12.34".parseJson())
        assertEquals(true, "true".parseJson())
        assertEquals(false, "false".parseJson())
        assertEquals(listOf(1, 2, 3), "[1,2,3]".parseJson())
        assertEquals(linkedMapOf("a" to 1, "b" to 2), """{"a":1, "b":2}""".parseJson())
    }

    @Test
    fun testCreateObject() {
        val value = 123
        val path = listOf("a", "b", "c")
        val actual = createObject(path, value)
        val expected = mapOf("a" to mapOf("b" to mapOf("c" to value)))
        assertEquals(expected, actual)
    }

    @Test
    fun testCreateObjectWithArrayIndex() {
        val value = 123
        val path = listOf("a", 2, "c")
        val actual = createObject(path, value)
        val expected = mapOf("a" to listOf(null, null, mapOf("c" to value)))
        assertEquals(expected, actual)
    }

    @Test
    fun mergeObjects() {
        val left = mapOf("a" to 1, "b" to mapOf("c" to 2, "d" to 3), "e" to "a")
        val right = mapOf("a" to null, "b" to mapOf("d" to 4), "f" to 5)
        val expected = mapOf("b" to mapOf("c" to 2, "d" to 4), "e" to "a", "f" to 5)
        val actual = mergeObjects(left, right)
        assertEquals(expected, actual)
    }

    @Test
    fun displayJsonElements() {
        val exampleArrayLeaf = listOf(
                Integer.MAX_VALUE,
                Long.MAX_VALUE,
                Double.MAX_VALUE,
                0,
                "string value",
                true,
                false,
                null
        )
        val exampleKeys = ('a'..'h').map { it.toString() }
        val exampleMapLeaf = (exampleKeys zip exampleArrayLeaf).toMap()
        val exampleArrayDeep = listOf(exampleMapLeaf, exampleArrayLeaf) + exampleArrayLeaf
        val exampleMapDeep = mapOf(
                "i" to exampleMapLeaf,
                "j" to exampleArrayDeep
        )
        val exampleMap = exampleMapLeaf + exampleMapDeep
        println(exampleMap.toPrettyJson())
        val primitiveMap = exampleMap.toPrimitiveMap()
        primitiveMap.map { entry ->
            val (key, value) = entry
            val keyString = key.joinToString(",", "listOf(", ")", transform = {it.toKotlinLiteral()})
            val valueString = value.toKotlinLiteral()
            "verify($keyString, $valueString)"
        }.forEach(::println)
        fun verify(path:List<String>, expected:Any?){
            val actual = primitiveMap[path]
            assertEquals(expected, actual)
        }
        verify(listOf("a"), 2147483647)
        verify(listOf("b"), 9223372036854775807)
        verify(listOf("c"), 1.7976931348623157E308)
        verify(listOf("d"), 0)
        verify(listOf("e"), "string value")
        verify(listOf("f"), true)
        verify(listOf("g"), false)
        verify(listOf("h"), null)
        verify(listOf("i","a"), 2147483647)
        verify(listOf("i","b"), 9223372036854775807)
        verify(listOf("i","c"), 1.7976931348623157E308)
        verify(listOf("i","d"), 0)
        verify(listOf("i","e"), "string value")
        verify(listOf("i","f"), true)
        verify(listOf("i","g"), false)
        verify(listOf("i","h"), null)
        verify(listOf("j","0","a"), 2147483647)
        verify(listOf("j","0","b"), 9223372036854775807)
        verify(listOf("j","0","c"), 1.7976931348623157E308)
        verify(listOf("j","0","d"), 0)
        verify(listOf("j","0","e"), "string value")
        verify(listOf("j","0","f"), true)
        verify(listOf("j","0","g"), false)
        verify(listOf("j","0","h"), null)
        verify(listOf("j","1","0"), 2147483647)
        verify(listOf("j","1","1"), 9223372036854775807)
        verify(listOf("j","1","2"), 1.7976931348623157E308)
        verify(listOf("j","1","3"), 0)
        verify(listOf("j","1","4"), "string value")
        verify(listOf("j","1","5"), true)
        verify(listOf("j","1","6"), false)
        verify(listOf("j","1","7"), null)
        verify(listOf("j","2"), 2147483647)
        verify(listOf("j","3"), 9223372036854775807)
        verify(listOf("j","4"), 1.7976931348623157E308)
        verify(listOf("j","5"), 0)
        verify(listOf("j","6"), "string value")
        verify(listOf("j","7"), true)
        verify(listOf("j","8"), false)
        verify(listOf("j","9"), null)
    }
}
