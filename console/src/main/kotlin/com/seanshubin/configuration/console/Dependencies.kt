package com.seanshubin.configuration.console

import com.seanshubin.configuration.contract.FilesContract
import com.seanshubin.configuration.contract.FilesDelegate
import com.seanshubin.configuration.domain.JsonConfigApp

class Dependencies(args:Array<String>) {
    val files: FilesContract = FilesDelegate
    val emit:(Any?)->Unit = ::println
    val runner:Runnable = JsonConfigApp(args, files, emit)
}
