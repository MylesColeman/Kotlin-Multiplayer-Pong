package uk.ac.tees.e4109732.mam_multiplayer_pong

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion
import com.badlogic.gdx.utils.viewport.Viewport

class OpponentPaddle(private val viewport: Viewport, private val texture: AtlasRegion?) {
    private var centreX = 0f
    private var targetX = 0f
    private var leftX = 0f
    private var y = Constants.WORLD_HEIGHT - 1.2f

    init {
        reset()
    }

    fun updateTargetX(newX: Float) {
        targetX = newX
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

    fun reset() {
        targetX = viewport.worldWidth * 0.5f
        centreX = viewport.worldWidth * 0.5f
        y = viewport.worldHeight - 1.2f
    }

    fun resetTo(newX: Float) {
        targetX = newX
        centreX = newX
        leftX = centreX - Constants.PADDLE_WIDTH * 0.5f
    }
}
