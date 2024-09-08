package com.arkhamusserver.arkhamus.logic.ingame.item

import com.arkhamusserver.arkhamus.model.enums.ingame.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.Ability.*
import com.arkhamusserver.arkhamus.model.enums.ingame.Item
import com.arkhamusserver.arkhamus.model.enums.ingame.Item.*
import org.springframework.stereotype.Component

@Component
class AbilityToItemResolver {
    fun resolve(ability: Ability): Item? =
        if (!ability.requiresItem) {
            null
        } else {
            when (ability) {
                //useful items
                TOWN_PORTAL_BY_SCROLL -> TOWN_PORTAL_SCROLL
                //cultist items
                THROW_POTATO -> CURSED_POTATO
                SUMMON_NIGHT -> MOON_STONE
                //clue search items
                SEARCH_FOR_INSCRIPTION -> INSCRIPTION_INVESTIGATION_ITEM
                SEARCH_FOR_SOUND -> SOUND_INVESTIGATION_ITEM
                SEARCH_FOR_SCENT -> SCENT_INVESTIGATION_ITEM
                SEARCH_FOR_AURA -> AURA_INVESTIGATION_ITEM
                SEARCH_FOR_CORRUPTION -> CORRUPTION_INVESTIGATION_ITEM
                SEARCH_FOR_OMEN -> OMEN_INVESTIGATION_ITEM
                SEARCH_FOR_DISTORTION -> DISTORTION_INVESTIGATION_ITEM
                KINDLE_CLOAK -> CLOAK_OF_FLAMES
                else -> null
            }
        }
}

