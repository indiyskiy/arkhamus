package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserMadnessHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.toItem
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import org.springframework.stereotype.Component

@Component
class OneTickUser(
    private val madnessHandler: UserMadnessHandler
) {
    fun processUsers(data: GlobalGameData, events: List<OngoingEvent>) {
        data.users.forEach { user ->
            processUser(user.value, data, events)
        }
    }

    private fun processUser(
        user: RedisGameUser,
        data: GlobalGameData,
        events: List<OngoingEvent>
    ) {
        processInventory(user, data, events)
    }

    private fun processInventory(
        user: RedisGameUser,
        data: GlobalGameData,
        events: List<OngoingEvent>
    ) {
        user.items.filter {
            it.value > 0
        }.forEach { (itemId, number) ->
            val item = itemId.toItem()
            when (item) {
                Item.CURSED_POTATO -> madnessHandler.applyMadness(user, number)
                else -> {}
            }
        }
    }
}