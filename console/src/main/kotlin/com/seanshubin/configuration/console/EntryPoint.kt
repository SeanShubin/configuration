package com.seanshubin.configuration.console

object EntryPoint {
    @JvmStatic
    fun main(args: Array<String>) {
        Dependencies(args).runner.run()
    }
}