package com.seanshubin.configuration.app

import com.seanshubin.configuration.contract.FilesContract
import com.seanshubin.configuration.contract.FilesDelegate
import com.seanshubin.configuration.domain.JsonConfigApp

class Dependencies(args:Array<String>) {
    private val files: FilesContract = FilesDelegate
    private val emit:(Any?)->Unit = ::println
    val runner:Runnable = JsonConfigApp(args, files, emit)
}
