package com.seanshubin.configuration.prototype

import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter

object ReflectionApp {
    data class Point(val x: Int, val y: Int)
    data class Rectangle(val topLeft: Point, val bottomRight: Point)

    @JvmStatic
    fun main(args: Array<String>) {
        val topLeft = Point(1, 2)
        val bottomRight = Point(3, 4)
        val value = Rectangle(topLeft, bottomRight)
        println(createDynamic(Rectangle::class))
    }

    fun createDynamic(rectangleClass: KClass<*>): Any? {
        val rectangleConstructor: KFunction<*> = rectangleClass.constructors.single()
        val topLeftParameter = rectangleConstructor.parameters[0]
        val bottomRightParameter = rectangleConstructor.parameters[1]
        val topLeft: KParameter = rectangleConstructor.parameters[0]
        val topLeftClass = topLeft.type.classifier as KClass<*>
        val topLeftConstructor = topLeftClass.constructors.single()
        val topParameter = topLeftConstructor.parameters[0]
        val leftParameter = topLeftConstructor.parameters[1]
        val topLeftArgs = mapOf<KParameter, Any?>(topParameter to 1, leftParameter to 2)
        val topLeftInstance = topLeftConstructor.callBy(topLeftArgs)
        val bottomRight: KParameter = rectangleConstructor.parameters[1]
        val bottomRightClass = bottomRight.type.classifier as KClass<*>
        val bottomRightConstructor = bottomRightClass.constructors.single()
        val bottomParameter = bottomRightConstructor.parameters[0]
        val rightParameter = bottomRightConstructor.parameters[1]
        val bottomRightArgs = mapOf<KParameter, Any?>(bottomParameter to 3, rightParameter to 4)
        val bottomRightInstance = bottomRightConstructor.callBy(bottomRightArgs)
        val rectangleArgs = mapOf<KParameter, Any?>(
                topLeftParameter to topLeftInstance,
                bottomRightParameter to bottomRightInstance)
        val rectangleInstance = rectangleConstructor.callBy(rectangleArgs)
        return rectangleInstance
    }
}