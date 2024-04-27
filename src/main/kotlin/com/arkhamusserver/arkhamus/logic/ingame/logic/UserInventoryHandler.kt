package com.arkhamusserver.arkhamus.logic.ingame.logic

import com.arkhamusserver.arkhamus.model.enums.ingame.Item
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.view.dto.netty.response.ContainerCell
import org.springframework.stereotype.Component

@Component
class UserInventoryHandler {
    fun userHaveItem(user: RedisGameUser, requiredItem: Item): Boolean {
        return (user.items[requiredItem.id] ?: 0) > 0
    }

    fun howManyItems(user: RedisGameUser, requiredItem: Item?): Long {
        return requiredItem?.let { user.items[it.id] } ?: 0
    }

    fun consumeItem(user: RedisGameUser, item: Item) {
        if (userHaveItem(user, item)) {
            user.items[item.id] = user.items[item.id]!! - 1
        }
    }

    fun mapUsersItems(items: MutableMap<Int, Long>): List<ContainerCell> {
        return items.map {
            ContainerCell(
                itemId = it.key,
                number = it.value
            )
        }
    }

}