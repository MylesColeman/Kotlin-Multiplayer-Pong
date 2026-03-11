package uk.ac.tees.e4109732.mam_multiplayer_pong

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.Viewport
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ktx.async.KtxAsync
import java.net.Socket
import kotlin.math.abs

class Paddle(private val viewport: Viewport, private val texture: AtlasRegion?) {
    private val touchCoords = Vector2()
    private var centreX = 0f
    private var leftX = 0f
    private var y = 0.2f

    private var filteredAccelX = 0f
    private val alpha = 0.05f

    init {
        reset()
    }

    fun draw(batch: SpriteBatch, delta: Float) {
        if (Gdx.input.isTouched) {
            touchCoords.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat())
            viewport.unproject(touchCoords)

            centreX += (touchCoords.x - centreX) * delta * Constants.PADDLE_SPEED
        }

        val rawAccelX = Gdx.input.accelerometerX
        filteredAccelX = filteredAccelX + alpha * (rawAccelX - filteredAccelX)

        centreX -= filteredAccelX * delta * Constants.ACCELEROMETER_SENSITIVITY

        leftX = centreX - Constants.PADDLE_WIDTH * 0.5f
        if (leftX < 0f) leftX = 0f
        else if (leftX > viewport.worldWidth - Constants.PADDLE_WIDTH)
            leftX = viewport.worldWidth - Constants.PADDLE_WIDTH

        centreX = leftX + Constants.PADDLE_WIDTH * 0.5f

        texture?.let {
            batch.setColor(Constants.PADDLE_COLOR)
            batch.draw(it, leftX, y, Constants.PADDLE_WIDTH, 1f)
            batch.setColor(1f, 1f, 1f, 1f)
        }
    }

    fun updateNetwork(socket: Socket?) {
        val currentSocket = socket ?: return

        if (Gdx.input.isTouched || abs(Gdx.input.accelerometerX) > 0.1f) {
            KtxAsync.launch(Dispatchers.IO) {
                try {
                    val out = currentSocket.getOutputStream()
                    val message = "Paddle: $centreX\n"
                    out.write(message.toByteArray())
                } catch (e: Exception) {
                    Gdx.app.error("Network", "Lost connection: ${e.message}")
                }
            }
        }
    }

    fun reset() {
        centreX = viewport.worldWidth * 0.5f
        y = 0.2f
        filteredAccelX = 0f
    }

    fun resetTo(newX: Float) {
        centreX = newX
        leftX = centreX - Constants.PADDLE_WIDTH * 0.5f
        filteredAccelX = 0f
    }
}
