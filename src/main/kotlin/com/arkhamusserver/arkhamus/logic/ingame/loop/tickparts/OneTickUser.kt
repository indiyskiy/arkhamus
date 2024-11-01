package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserMadnessHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.toItem
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class OneTickUser(
    private val madnessHandler: UserMadnessHandler
) {

    companion object {
        val logger = LoggerFactory.getLogger(OneTickUser::class.java)
        const val POTATO_MADNESS_TICK_MILLIS: Double = 2.0 / 1000.0
    }

    fun processUsers(data: GlobalGameData, timePassedMillis: Long) {
        data.users.forEach { user ->
            processUser(user.value, timePassedMillis)
        }
    }

    private fun processUser(
        user: RedisGameUser,
        timePassedMillis: Long
    ) {
        processInventory(user, timePassedMillis)
    }

    private fun processInventory(
        user: RedisGameUser,
        timePassedMillis: Long
    ) {
        user.items.filter {
            it.value > 0
        }.forEach { (itemId, number) ->
            val item = itemId.toItem()
            when (item) {
                Item.CURSED_POTATO -> madnessHandler.applyMadness(user, POTATO_MADNESS_TICK_MILLIS * number * timePassedMillis)
                else -> {}
            }
        }
    }
}