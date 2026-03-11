package uk.ac.tees.e4109732.mam_multiplayer_pong

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion
import com.badlogic.gdx.utils.viewport.Viewport
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ktx.async.KtxAsync
import ktx.async.onRenderingThread
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.Socket

class GameWorld(gameScreen: GameScreen) {
    enum class GameState {
        PLAYING,
    }

    private var state = GameState.PLAYING

    private val viewport: Viewport = gameScreen.game.viewport
    private val paddleTex: AtlasRegion? = gameScreen.game.atlas?.findRegion("Paddle")
    private val ballTex: AtlasRegion? = gameScreen.game.atlas?.findRegion("Ball")

    private val ball = Ball(ballTex)
    private val paddle = Paddle(viewport, paddleTex)
    private val opponentPaddle = OpponentPaddle(viewport, paddleTex)

    private var socket: Socket? = null
    private var playerID: Int = -1

    init {
        startNetworkListener("10.0.2.2", 4300)
    }

    fun startNetworkListener(host: String, port: Int) {
        KtxAsync.launch(Dispatchers.IO) {
            try {
                val connectedSocket = Socket(host, port)
                socket = connectedSocket
                val inputStream = connectedSocket.getInputStream()
                val reader = BufferedReader(InputStreamReader(inputStream))

                val idBuffer = ByteArray(4)
                inputStream.read(idBuffer)
                playerID = idBuffer[0].toInt()

                while (state == GameState.PLAYING) {
                    val line = reader.readLine() ?: break

                    onRenderingThread {
                        when {
                            line.startsWith("Ball: ") -> {
                                val coords = line.substringAfter("Ball: ").split(", ")
                                var serverX = coords[0].toFloat()
                                var serverY = coords[1].toFloat()

                                if (playerID == 1) {
                                    serverX = Constants.WORLD_WIDTH - serverX
                                    serverY = Constants.WORLD_HEIGHT - serverY
                                }

                                ball.updateServerPosition(serverX, serverY)
                            }
                            line.startsWith("Paddle: ") -> {
                                val rawX = line.substringAfter("Paddle: ").toFloatOrNull()
                                if (rawX != null) {
                                    val finalX = Constants.WORLD_WIDTH - rawX

                                    opponentPaddle.updateTargetX(finalX)
                                }
                            }
                        }
                    }
                }
            } catch (_: Exception) {
                Gdx.app.error("Network", "Lost connection!")
            }
        }
    }

    fun render(batch: SpriteBatch, delta: Float) {
        if (state != GameState.PLAYING) return

        ball.draw(batch, delta)
        paddle.draw(batch, delta)
        paddle.updateNetwork(this.socket)
        opponentPaddle.draw(batch, delta)
    }

    fun reset() {
        paddle.reset()
        opponentPaddle.reset()
        state = GameState.PLAYING
    }
}
