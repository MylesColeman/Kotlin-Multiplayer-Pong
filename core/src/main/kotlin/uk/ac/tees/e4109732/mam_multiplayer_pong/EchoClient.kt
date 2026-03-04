package uk.ac.tees.e4109732.mam_multiplayer_pong

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.Socket
import java.io.PrintWriter
import java.io.BufferedReader
import java.io.InputStreamReader
import ktx.async.KtxAsync
import ktx.async.onRenderingThread

class EchoClient(val host: String, val port: Int) {
    fun connectAndSend(message: String, onResponse: (String) -> Unit) {
        KtxAsync.launch(Dispatchers.IO) {
            try {
                val socket = Socket(host, port)
                val out = PrintWriter(socket.getOutputStream(), true)
                val reader = BufferedReader(InputStreamReader(socket.inputStream))

                out.println(message)

                val response = reader.readLine()

                onRenderingThread {
                    onResponse(response ?: "No Response")
                }

                socket.close()
            } catch (e: Exception) {
                onRenderingThread { onResponse("Error: ${e.message}") }
            }
        }
    }
}
