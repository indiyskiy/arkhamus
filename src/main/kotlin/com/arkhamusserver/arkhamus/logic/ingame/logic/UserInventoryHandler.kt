package com.arkhamusserver.arkhamus.logic.ingame.logic

import com.arkhamusserver.arkhamus.model.enums.ingame.Item
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import org.springframework.stereotype.Component

@Component
class UserInventoryHandler {
    fun userHaveItem(user: RedisGameUser, requiredItem: Item): Boolean {
        return (user.items[requiredItem.getId()] ?: 0) > 0
    }

    fun consumeItem(user: RedisGameUser, item: Item) {
        if (userHaveItem(user, item)) {
            user.items[item.getId()] = user.items[item.getId()]!! - 1
        }
    }

}