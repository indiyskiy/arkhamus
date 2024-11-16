package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserMadnessHandler
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.madness.MadnessTickProcessHandler
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item
import com.arkhamusserver.arkhamus.model.enums.ingame.core.toItem
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class OneTickUser(
    private val madnessHandler: UserMadnessHandler,
    private val madnessTickProcessHandler: MadnessTickProcessHandler
) {

    companion object {
        val logger = LoggerFactory.getLogger(OneTickUser::class.java)
        const val POTATO_MADNESS_TICK_MILLIS: Double = 2.0 / 1000.0
    }

    fun processUsers(data: GlobalGameData, timePassedMillis: Long) {
        data.users.forEach { user ->
            processUser(user.value, data, timePassedMillis)
        }
    }

    private fun processUser(
        user: RedisGameUser,
        data: GlobalGameData,
        timePassedMillis: Long
    ) {
        processInventory(user, timePassedMillis, data.game.globalTimer)
        processMadness(user, data, timePassedMillis)
    }

    private fun processInventory(
        user: RedisGameUser,
        timePassedMillis: Long,
        gameTime: Long
    ) {
        user.items.filter {
            it.value > 0
        }.forEach { (itemId, number) ->
            val item = itemId.toItem()
            when (item) {
                Item.CURSED_POTATO -> madnessHandler.applyMadness(
                    user,
                    POTATO_MADNESS_TICK_MILLIS * number * timePassedMillis,
                    gameTime,
                )

                else -> {}
            }
        }
    }

    private fun processMadness(
        user: RedisGameUser,
        data: GlobalGameData,
        timePassedMillis: Long
    ) {
        madnessTickProcessHandler.processMadness(
            user = user,
            data = data,
            timePassedMillis = timePassedMillis
        )
    }
}