package uk.ac.tees.e4109732.mam_multiplayer_pong

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.Viewport

class Ball(private val viewport: Viewport, private val texture: AtlasRegion?) {
    var x = 0f
    var y = 0f
    private var localAngle = 0f
    private val velocity = Vector2(1f, 0f)

    init {
        reset()
    }

    fun draw(batch: SpriteBatch, delta: Float) {
        localAngle -= delta * 1000

        x += velocity.x * delta * Constants.BALL_SPEED
        y += velocity.y * delta * Constants.BALL_SPEED

        if (x >= viewport.worldWidth - 0.5f || x <= 0.5f) {
            velocity.setAngleDeg(180f - velocity.angleDeg())
            x += velocity.x * delta * Constants.BALL_SPEED
            y += velocity.y * delta * Constants.BALL_SPEED
        }

        texture?.let {
            batch.draw(it, x - 0.5f, y - 0.5f,
                0.5f, 0.5f, 1f, 1f,
                1f, 1f, localAngle)
        }
    }

    fun setDirectionAngle(angle: Float) {
        velocity.setAngleDeg(angle)
    }

    fun reset() {
        velocity.set(1f, 0f)
        x = viewport.worldWidth * 0.5f

        if (MathUtils.randomBoolean()) {
            velocity.setAngleDeg(-Constants.PADDLE_LEFT_ANGLE + MathUtils.random(-30f, 30f))
            y = viewport.worldHeight - 1f
        }
        else {
            velocity.setAngleDeg(Constants.PADDLE_RIGHT_ANGLE + MathUtils.random(-30f, 30f))
            y = 1.15f
        }
    }
}
