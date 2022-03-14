package com.seanshubin.configuration.dynamic

import com.fasterxml.jackson.databind.Module
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import java.nio.charset.StandardCharsets
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter

object DynamicallyTyped {
    val charset = StandardCharsets.UTF_8
    val kotlinModule: Module = KotlinModule()
    val objectMapper: ObjectMapper = ObjectMapper()
        .registerModule(kotlinModule)
        .enable(SerializationFeature.INDENT_OUTPUT)

    fun merge(left: Any?, right: Any?): Any? =
        mergeWithPath(emptyList(), left, right)

    private fun typeCheckedAssign(path: List<Any?>, left: Any?, right: Any?): Any? {
        return if (left == null) {
            right
        } else if (right == null) {
            null
        } else {
            val leftType = left.javaClass.name
            val rightType = right.javaClass.name
            if (leftType == rightType) {
                right
            } else {
                val pathString = path.joinToString(", ")
                throw RuntimeException("At path [$pathString], unable to replace [$left] of type $leftType with [$right] of type $rightType")
            }
        }
    }

    private fun mergeWithPath(path: List<Any?>, left: Any?, right: Any?): Any? =
        if (left is Map<*, *> && right is Map<*, *>) {
            @Suppress("UNCHECKED_CAST")
            mergeMap(path, left as Map<Any?, Any?>, right as Map<Any?, Any?>)
        } else typeCheckedAssign(path, left, right)

    private fun mergeMap(path: List<Any?>, left: Map<Any?, Any?>, right: Map<Any?, Any?>): Map<Any?, Any?> {
        val keys = (left.keys + right.keys).distinct()
        val entries = keys.map { key ->
            if (right.containsKey(key)) {
                if (right[key] == null) {
                    null
                } else {
                    key to mergeWithPath(path + key, left[key], right[key])
                }
            } else {
                key to left[key]
            }
        }
        return entries.filterNotNull().toMap()
    }

    inline fun <reified T> mergeTypedWithDynamic(typed: T, dynamic: Any?): T {
        val leftJson = objectMapper.writeValueAsString(typed)
        val leftAst = objectMapper.readValue<Any?>(leftJson)
        val mergedAst = merge(leftAst, dynamic)
        val mergedJson = objectMapper.writeValueAsString(mergedAst)
        return objectMapper.readValue(mergedJson)
    }

    inline fun <reified T> createDynamic(dynamic: Any?): T {
        val theClass = T::class
        val path = emptyList<Any?>()
        val result = createDynamic(path, theClass, dynamic)
        return result as T
    }

    fun createDynamic(path: List<Any?>, kClass: KClass<*>, dynamic: Any?): Any? = when {
        dynamic == null || kClass.javaObjectType == dynamic.javaClass -> dynamic
        else -> {
            val constructor = kClass.constructors.single()
            val args = createArgs(path, constructor, dynamic)
            constructor.call(*args)
        }
    }

    fun createArgs(path: List<Any?>, constructor: KFunction<*>, dynamic: Any?): Array<Any?> {
        val createArgLocal = { parameter: KParameter -> createArg(path, parameter, dynamic) }
        val args: List<Any?> = constructor.parameters.map(createArgLocal)
        return args.toTypedArray()
    }

    fun createArg(path: List<Any?>, parameter: KParameter, dynamic: Any?): Any? {
        if (dynamic !is Map<*, *>) {
            val pathString = path.joinToString(", ")
            val typeName = dynamic?.javaClass?.typeName ?: "<null>"
            throw RuntimeException("At path [$pathString], expected a map fo values, got [$dynamic] of type $typeName")
        }
        val key = parameter.name
        if (!dynamic.containsKey(key)) {
            val pathString = path.joinToString(", ")
            throw RuntimeException("At path [$pathString], unable to find key [$key] in map [$dynamic]")
        }
        val dynamicValue = dynamic[key]
        val newPath = path + key
        val kClass = parameter.type.classifier as KClass<*>
        val value = createDynamic(newPath, kClass, dynamicValue)
        return value
    }
}
