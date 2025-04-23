package com.arkhamusserver.arkhamus.logic.ingame

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class GlobalGameSettings {

    //TEST
    @Value("\${game.testMode}")
    var testMode: Boolean = true

    @Value("\${game.createTestQuests}")
    var createTestQuests: Boolean = true

    //TIMINGS
    @Value("\${game.secondInMillis}")
    var secondInMillis: Long = 1000

    @Value("\${game.minuteInMillis}")
    var minuteInMillis: Long = 60000

    @Value("\${game.dayLengthMinutes}")
    var dayLengthMinutes: Long = 8

    @Value("\${game.nightLengthMinutes}")
    var nightLengthMinutes: Long = 4

    @Value("\${game.fullDayMinutes}")
    var fullDayMinutes: Long = 12

    @Value("\${game.gameLengthMinutes}")
    var gameLengthMinutes: Long = 60

    //DISTANCE
    @Value("\${game.globalVisionDistance}")
    var globalVisionDistance: Double = 15.0

    @Value("\${game.highGroundHeight}")
    var highGroundHeight: Double = 1.0

    //ABILITIES
    @Value("\${game.defaultAbilityCooldownMultiplier}")
    var defaultAbilityCooldownMultiplier: Double = 1.0

    //MOVEMENT
    @Value("\${game.defaultMovementSpeedMultiplier}")
    var defaultMovementSpeedMultiplier: Double = 1.0

    //QUESTS
    @Value("\${game.questsOnStart}")
    var questsOnStart: Int = 5

    @Value("\${game.questsToRefresh}")
    var questsToRefresh: Int = 2

    @Value("\${game.questRewardSlots}")
    var questRewardSlots: Int = 4

    //CLUES
    @Value("\${game.eachClueOnStart}")
    var eachClueOnStart: Int = 2

    //MADNESS
    @Value("\${game.maxUserMadness}")
    var maxUserMadness: Double = 600.0

    //RITUAL
    @Value("\${game.ritualUserRadiusFactor}")
    var ritualUserRadiusFactor: Double = 0.6667 // 2/3 as a decimal

    companion object {
        //TIMINGS
        const val SECOND_IN_MILLIS: Long = 1 * 1000
        const val MINUTE_IN_MILLIS: Long = SECOND_IN_MILLIS * 60
    }
}
