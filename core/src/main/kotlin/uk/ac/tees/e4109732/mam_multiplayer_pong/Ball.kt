package uk.ac.tees.e4109732.mam_multiplayer_pong

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion


class Ball(private val texture: AtlasRegion?) {
    var x = 0f
    var y = 0f
    private var localAngle = 0f

    fun draw(batch: SpriteBatch, delta: Float) {
        localAngle -= delta * 1000

        texture?.let {
            batch.draw(it, x - 0.5f, y - 0.5f,
                0.5f, 0.5f, 1f, 1f,
                1f, 1f, localAngle)
        }
    }
}
