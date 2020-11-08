package com.seanshubin.configuration.prototype

import com.fasterxml.jackson.databind.Module
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths


object JsonConfigurationPrototypeApp {
    val charset = StandardCharsets.UTF_8
    val kotlinModule: Module = KotlinModule()
    val objectMapper: ObjectMapper = ObjectMapper().registerModule(kotlinModule).enable(SerializationFeature.INDENT_OUTPUT)

    data class TwoStringConfiguration(val a: String, val b: String)

    @JvmStatic
    fun main(args: Array<String>) {
        val defaultA = "default-a"
        val defaultB = "default-b"
        val default = TwoStringConfiguration(defaultA, defaultB)
        val baseName = "generated/prototype/"
        val base = Paths.get(baseName)
        val originalPath = base.resolve("original.json")
        val effectivePath = base.resolve("effective.json")
        val effective = load(originalPath, default)
        store(effectivePath, effective)
        println(effective)
    }

    fun load(path: Path, default: TwoStringConfiguration): TwoStringConfiguration {
        return if (Files.exists(path)) loadExisting(path, default)
        else {
            store(path, default)
            load(path, default)
        }
    }

    fun loadExisting(path: Path, default: TwoStringConfiguration): TwoStringConfiguration {
        val fileText = Files.readString(path, charset)
        val fileAst = objectMapper.readValue<Any?>(fileText)
        val defaultText = objectMapper.writeValueAsString(default)
        val defaultAst = objectMapper.readValue<Any?>(defaultText)
        val effectiveAst = Dynamic.merge(defaultAst, fileAst)
        val effectiveText = objectMapper.writeValueAsString(effectiveAst)
        val configuration = objectMapper.readValue<TwoStringConfiguration>(effectiveText)
        return configuration
    }

    fun store(path: Path, value: TwoStringConfiguration) {
        if (!Files.exists(path.parent)) {
            Files.createDirectories(path.parent)
        }
        val text = objectMapper.writeValueAsString(value)
        Files.writeString(path, text, charset)
    }
}