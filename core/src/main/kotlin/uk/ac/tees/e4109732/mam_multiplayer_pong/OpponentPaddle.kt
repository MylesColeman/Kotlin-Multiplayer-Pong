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

class OpponentPaddle(private val viewport: Viewport, private val texture: AtlasRegion?) {
    var socket: Socket? = null
        private set

    private var centreX = 0f
    private var targetX = 0f
    private var leftX = 0f
    private var y = Constants.WORLD_HEIGHT - 1.2f

    init {
        reset()
    }

    fun startNetworkListener(host: String, port: Int) {
        KtxAsync.launch(Dispatchers.IO) {
            try {
                socket = Socket(host, port)
                val inputStream = socket!!.getInputStream()
                val idBuffer = ByteArray(4)
                inputStream.read(idBuffer)
                val reader = BufferedReader(InputStreamReader(inputStream))

                while (true) {
                    val line = reader.readLine() ?: break
                    val newX = line.toFloatOrNull()

                    if (newX != null) {
                        onRenderingThread {
                            targetX = newX
                        }
                    }
                }
            } catch (e: Exception) {
                Gdx.app.error("Network", "Lost connection: ${e.message}")
            }
        }
    }

    fun draw(batch: SpriteBatch, delta: Float) {
        leftX = centreX - Constants.PADDLE_WIDTH * 0.5f
        if (leftX < 0f) leftX = 0f
        else if (leftX > viewport.worldWidth - Constants.PADDLE_WIDTH)
            leftX = viewport.worldWidth - Constants.PADDLE_WIDTH

        centreX += (targetX - centreX) * delta * Constants.PADDLE_SPEED

        texture?.let {
            batch.setColor(Constants.OPPONENT_PADDLE_COLOR)
            batch.draw(it, leftX, y, Constants.PADDLE_WIDTH, 1f)
            batch.setColor(1f, 1f, 1f, 1f)
        }
    }

    //fun hitTest(ball: Ball): Boolean {
    //}

    fun reset() {
        targetX = viewport.worldWidth * 0.5f
        centreX = viewport.worldWidth * 0.5f
        y = viewport.worldHeight - 1.2f
    }
}
