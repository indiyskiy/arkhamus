package com.arkhamusserver.arkhamus.logic.ingame

class GlobalGameSettings {

    companion object {
        const val SECOND_IN_MILLIS: Long = 1 * 1000
        const val MINUTE_IN_MILLIS: Long = SECOND_IN_MILLIS * 60
        const val DAY_LENGTH: Long = 8
        const val NIGHT_LENGTH: Long = 4
        const val FULL_DAY: Long = DAY_LENGTH + NIGHT_LENGTH
        const val GAME_LENGTH: Long = FULL_DAY * 5
        const val GLOBAL_VISION_DISTANCE: Double = 10.0
    }
}