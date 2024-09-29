package com.arkhamusserver.arkhamus.logic.ingame

class GlobalGameSettings {

    companion object {
        //TEST
        const val CREATE_TEST_QUESTS = true

        //TIMINGS
        const val SECOND_IN_MILLIS: Long = 1 * 1000
        const val MINUTE_IN_MILLIS: Long = SECOND_IN_MILLIS * 60
        const val DAY_LENGTH_MINUTES: Long = 8
        const val NIGHT_LENGTH_MINUTES: Long = 4
        const val FULL_DAY_MINUTES: Long = DAY_LENGTH_MINUTES + NIGHT_LENGTH_MINUTES
        const val GAME_LENGTH: Long = FULL_DAY_MINUTES * 5

        //DISTANCE
        const val GLOBAL_VISION_DISTANCE: Double = 10.0
        const val HIGH_GROUND_HEIGHT: Double = -1.0

        //QUESTS
        const val QUESTS_ON_START = 5
        const val QUESTS_TO_REFRESH = 2
        const val QUEST_REWARD_SLOTS = 4

        //CLUES
        const val EACH_CLUE_ON_START = 2
    }
}