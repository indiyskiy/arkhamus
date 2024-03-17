package com.arkhamusserver.arkhamus.logic.ingame.item

import com.arkhamusserver.arkhamus.model.enums.ingame.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.Item
import com.arkhamusserver.arkhamus.model.enums.ingame.Item.*
import org.springframework.stereotype.Component

@Component
class AbilityToItemResolver {
    fun resolve(ability: Ability): Item? =
        if(!ability.isRequiresItem()){
            null
        } else {
            when (ability) {
                Ability.SUMMON_NIGHT -> MOON_STONE
            }
        }
}

