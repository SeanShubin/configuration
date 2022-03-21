package com.seanshubin.configuration.app

object EntryPoint {
    @JvmStatic
    fun main(args: Array<String>) {
        Dependencies(args).runner.run()
    }
}