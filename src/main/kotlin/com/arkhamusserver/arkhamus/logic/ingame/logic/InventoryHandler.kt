package com.arkhamusserver.arkhamus.logic.ingame.logic

import com.arkhamusserver.arkhamus.logic.ingame.item.Ingredient
import com.arkhamusserver.arkhamus.logic.ingame.item.Recipe
import com.arkhamusserver.arkhamus.model.enums.ingame.Item
import com.arkhamusserver.arkhamus.model.redis.RedisCrafter
import com.arkhamusserver.arkhamus.model.redis.RedisGameUser
import com.arkhamusserver.arkhamus.view.dto.netty.response.ContainerCell
import org.springframework.stereotype.Component
import java.lang.Long.min

@Component
class InventoryHandler {

    fun addItem(user: RedisGameUser, addedItem: Item)  {
        val howManyItems = howManyItems(user, addedItem)
        user.items[addedItem.id] = howManyItems + 1
    }

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

    fun consumeItems(recipe: Recipe, gameUser: RedisGameUser, crafter: RedisCrafter) {
        recipe.ingredients.forEach { ingredient: Ingredient ->
            consumeItem(ingredient, gameUser, crafter)
        }
    }

    private fun consumeItem(ingredient: Ingredient, user: RedisGameUser, crafter: RedisCrafter) {
        val itemToConsume = ingredient.item
        val toConsumeBefore = ingredient.number.toLong()

        val itemsInCrafter = crafter.items[itemToConsume.id] ?: 0
        val canBeConsumedFromCrafter = min(toConsumeBefore, itemsInCrafter)
        crafter.items[itemToConsume.id] = itemsInCrafter - canBeConsumedFromCrafter

        val toConsumeFromUser = toConsumeBefore - canBeConsumedFromCrafter
        val itemsFromUser = user.items[itemToConsume.id] ?: 0
        val canBeConsumedFromUser = min(itemsFromUser, toConsumeFromUser)
        user.items[itemToConsume.id] = itemsFromUser - canBeConsumedFromUser
    }

}