package uk.ac.tees.e4109732.mam_multiplayer_pong

import com.badlogic.gdx.graphics.Color

object Constants {
    val BACKGROUND_COLOR: Color = Color.valueOf("#333333")

    const val WORLD_WIDTH = 20
    const val WORLD_HEIGHT = 40

    const val PADDLE_WIDTH = 2.5f
    val PADDLE_COLOR: Color = Color.GREEN
    val OPPONENT_PADDLE_COLOR: Color = Color.RED
    const val PADDLE_SPEED = 5f
    const val ACCELEROMETER_SENSITIVITY = 10f
}
