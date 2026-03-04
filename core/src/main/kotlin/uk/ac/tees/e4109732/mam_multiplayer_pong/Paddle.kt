package uk.ac.tees.e4109732.mam_multiplayer_pong

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.Viewport
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

    fun hitTest(ball: Ball): Boolean {
        val sectionWidth = Constants.PADDLE_WIDTH * 0.3f

        if (abs(ball.y - y) > 1.15f) return false

        if (abs(ball.x - centreX) < Constants.PADDLE_WIDTH * 0.5f + 0.485f) {

            val distX = abs(ball.x - leftX)

            val bounceAngle: Float = when {
                distX >= 0 && distX <= sectionWidth -> {
                    Constants.PADDLE_LEFT_ANGLE +
                        MathUtils.random(-Constants.PADDLE_DELTA_ANGLE, Constants.PADDLE_DELTA_ANGLE)
                }
                distX > sectionWidth && distX <= 2 * sectionWidth -> {
                    Constants.PADDLE_CENTER_ANGLE +
                        MathUtils.random(-Constants.PADDLE_DELTA_ANGLE, Constants.PADDLE_DELTA_ANGLE)

                }
                else -> {
                    Constants.PADDLE_RIGHT_ANGLE +
                        MathUtils.random(-Constants.PADDLE_DELTA_ANGLE, Constants.PADDLE_DELTA_ANGLE)
                }
            }

            ball.setDirectionAngle(bounceAngle)
            return true
        }
        return false
    }

    fun reset() {
        centreX = viewport.worldWidth * 0.5f
        y = 0.2f
        filteredAccelX = 0f
    }
}
