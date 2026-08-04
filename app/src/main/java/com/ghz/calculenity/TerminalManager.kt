package com.ghz.calculenity

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class TerminalManager {
    private var process: Process? = null
    private var writer: OutputStreamWriter? = null

    private val _outputFlow = MutableSharedFlow<String>(replay = 50)
    val outputFlow = _outputFlow.asSharedFlow()

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun startSession() = withContext(Dispatchers.IO) {
        try {
            process = ProcessBuilder("/system/bin/sh").redirectErrorStream(true).start()
            writer = OutputStreamWriter(process?.outputStream)

            val reader = BufferedReader(InputStreamReader(process?.inputStream))
            var line: String?

            // Continuously read the shell output and emit it to the UI
            while (reader.readLine().also { line = it } != null) {
                line?.let { _outputFlow.emit(it) }
            }
        } catch (e: Exception) {
            _outputFlow.emit("Terminal Error: ${e.message}")
        }
    }

    suspend fun sendCommand(command: String) = withContext(Dispatchers.IO) {
        writer?.apply {
            write("$command\n")
            flush()
        }
    }
}
