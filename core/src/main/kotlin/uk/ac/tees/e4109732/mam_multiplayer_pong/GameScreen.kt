package uk.ac.tees.e4109732.mam_multiplayer_pong

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.graphics.use
import ktx.scene2d.*

class GameScreen(val game: Main) : KtxScreen {
    private val echoClient = EchoClient("10.0.2.2", 4300)
    private var serverMessage = "Tap to send data"

    private val root: Table
    private val paddleRegion = game.atlas?.findRegion("Paddle")

    private val stage = Stage(game.viewport, game.batch)
    private val opponentScoreLabel: Label
    private val playerScoreLabel: Label
    private val gameOverLabel: Label

    private val gameWorld: GameWorld

    init {
        val labelStyle = Label.LabelStyle(game.font, Color.WHITE)

        gameOverLabel = Label("You Won", labelStyle)

        root = scene2d.table {
            setFillParent(true)
            setRound(false)

            add(scene2d.table {
                setRound(false)
                opponentScoreLabel = Label("0", labelStyle)
                add(opponentScoreLabel).bottom().left().padLeft(0.4f)

                add(gameOverLabel).expandX().bottom().center()
            }).expand().bottom().fillX().padTop(1.8f).padBottom(0.75f).row()

            add(scene2d.table {
                setRound(false)
                playerScoreLabel = Label("0", labelStyle)
                add(playerScoreLabel).top().left().padLeft(0.4f).expandX()
            }).expand().top().fillX().padTop(0.75f)
        }
        stage.addActor(root)

        gameWorld = GameWorld(this)
    }

    fun newGame() {
        gameOverLabel.isVisible = false
        gameWorld.reset()
    }

    fun setGameOverState(playerScore: Int, opponentScore: Int, resultText: String) {
        gameOverLabel.isVisible = true
        gameOverLabel.setText(resultText)
        opponentScoreLabel.setText(opponentScore.toString())
        playerScoreLabel.setText(playerScore.toString())
    }

    override fun render(delta: Float) {
        clearScreen(Constants.BACKGROUND_COLOR.r, Constants.BACKGROUND_COLOR.g, Constants.BACKGROUND_COLOR.b)

        if (Gdx.input.justTouched()) {
            val message = "${Gdx.input.x}, ${Gdx.input.y}"

            echoClient.connectAndSend(message) { response ->
                serverMessage = response
            }
        }

        game.viewport.apply()
        game.batch.projectionMatrix = game.viewport.camera.combined

        game.batch.use { batch ->
            paddleRegion?.let { region ->
                val xOffset = 0.25f
                val lineThickness = 0.5f
                val yPos = (game.viewport.worldHeight * 0.5f) - (lineThickness * 0.5f)
                for (i in 0 until Constants.WORLD_WIDTH) {
                    batch.draw(
                        region,
                        i.toFloat() + xOffset, yPos,
                        0.5f, lineThickness
                    )
                }
            }
            gameWorld.render(batch, delta)
            game.font.draw(batch, serverMessage, 1f, 1f)
        }

        stage.act(delta)
        stage.draw()
    }

    override fun show() {
        stage.clear()
        stage.addActor(root)
        newGame()
    }

    override fun resize(width: Int, height: Int) {
        game.viewport.update(width, height, true)
    }

    override fun dispose() {
        stage.dispose()
    }
}
