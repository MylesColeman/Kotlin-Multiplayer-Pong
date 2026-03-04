package uk.ac.tees.e4109732.mam_multiplayer_pong

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.assets.disposeSafely
import ktx.assets.toInternalFile
import ktx.scene2d.Scene2DSkin
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import ktx.async.KtxAsync
import com.badlogic.gdx.utils.viewport.ExtendViewport


class Main : KtxGame<KtxScreen>() {
    val batch by lazy { SpriteBatch() }
    val viewport = ExtendViewport(Constants.WORLD_WIDTH.toFloat(), Constants.WORLD_HEIGHT.toFloat())
    val assetManager = AssetManager()

    var atlas: TextureAtlas? = null

    //lateinit var font: BitmapFont - Was used for previous testing
    lateinit var font: BitmapFont

    override fun create() {
        KtxAsync.initiate()

        val generator = FreeTypeFontGenerator("gamefont.ttf".toInternalFile())
        val parameter = FreeTypeFontGenerator.FreeTypeFontParameter().apply {
            size = 48
            color = Color.WHITE
            minFilter = Texture.TextureFilter.Linear
            magFilter = Texture.TextureFilter.Linear
        }
        // font = generator.generateFont(parameter) - Was used for previous testing
        font = generator.generateFont(parameter).apply {
            data.ascent = capHeight
            data.descent = 0f
            data.setScale(0.05f)
            setUseIntegerPositions(false)
        }
        generator.dispose()

        Scene2DSkin.defaultSkin = Skin()

        addScreen(LoadingScreen(this))
        setScreen<LoadingScreen>()
    }

    override fun dispose() {
        batch.disposeSafely()
        assetManager.disposeSafely()
        atlas?.disposeSafely()
        //font.disposeSafely() - Was used for previous testing
        font.disposeSafely()
        super.dispose()
    }
}
