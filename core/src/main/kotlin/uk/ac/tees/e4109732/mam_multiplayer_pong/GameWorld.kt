package uk.ac.tees.e4109732.mam_multiplayer_pong

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion
import com.badlogic.gdx.utils.Timer
import com.badlogic.gdx.utils.viewport.Viewport

class GameWorld(private val gameScreen: GameScreen) {
    enum class GameState {
        PLAYING,
        GAME_OVER
    }

    private var playerScore = 0
    private var opponentScore = 0

    private var state = GameState.PLAYING

    private val viewport: Viewport = gameScreen.game.viewport
    private val paddleTex: AtlasRegion? = gameScreen.game.atlas?.findRegion("Paddle")
    private val ballTex: AtlasRegion? = gameScreen.game.atlas?.findRegion("Ball")

    private val ball = Ball(viewport, ballTex)
    private val paddle = Paddle(viewport, paddleTex)

    fun render(batch: SpriteBatch, delta: Float) {
        if (state != GameState.PLAYING) return

        ball.draw(batch, delta)
        paddle.draw(batch, delta)
        paddle.hitTest(ball)

        if (ball.y < -1f || ball.y > viewport.worldHeight + 0.5f) {
            state = GameState.GAME_OVER

            if (ball.y < -1) {
                gameScreen.setGameOverState(playerScore, ++opponentScore, "You Lost")
            }
            else {
                gameScreen.setGameOverState(++playerScore, opponentScore, "You Won")
            }
            Timer.schedule(object : Timer.Task() {
                override fun run() {
                    gameScreen.newGame()
                }
            }, 1.25f)
        }
    }

    fun reset() {
        ball.reset()
        paddle.reset()
        state = GameState.PLAYING
    }
}
