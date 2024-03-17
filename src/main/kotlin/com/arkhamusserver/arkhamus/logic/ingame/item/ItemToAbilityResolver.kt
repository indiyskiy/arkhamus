package com.arkhamusserver.arkhamus.logic.ingame.item

import com.arkhamusserver.arkhamus.model.enums.ingame.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.Item
import com.arkhamusserver.arkhamus.model.enums.ingame.Item.MOON_STONE
import org.springframework.stereotype.Component

@Component
class ItemToAbilityResolver {
    fun resolve(item: Item): Ability? =
        when (item) {
            MOON_STONE -> Ability.SUMMON_NIGHT
            else -> null
        }
}

