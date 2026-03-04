package uk.ac.tees.e4109732.mam_multiplayer_pong

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import ktx.app.KtxScreen
import ktx.app.clearScreen

class LoadingScreen(val game: Main) : KtxScreen {
    override fun show() {
        game.assetManager.load("game.atlas", TextureAtlas::class.java)
    }

    override fun render(delta: Float) {
        if (game.assetManager.update()) {
            game.atlas = game.assetManager["game.atlas"]

            game.addScreen(GameScreen(game))
            game.setScreen<GameScreen>()
        }

        clearScreen(0f, 0f, 0f)
    }

    override fun resize(width: Int, height: Int) {
        game.viewport.update(width, height, true)
    }
}
