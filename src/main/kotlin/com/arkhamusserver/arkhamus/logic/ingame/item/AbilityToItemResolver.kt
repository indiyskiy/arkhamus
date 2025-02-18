package com.arkhamusserver.arkhamus.logic.ingame.item

import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability.*
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Item.*
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
                HEAL_MADNESS_BY_PILL -> PILL
                MINOR_DISPELL -> DISPELL_FLASK
                PRETEND_CULTIST -> FAKE_CULTIST_SCARF
                //cultist items
                THROW_POTATO -> CURSED_POTATO
                SUMMON_NIGHT -> MOON_STONE
                FAKE_VOTE -> ANNOYING_BELL
                PEEKABOO_CURSE_ITEM -> RITUAL_DAGGER
                //clue search items
                SEARCH_FOR_INSCRIPTION -> INSCRIPTION_INVESTIGATION_ITEM
                //clue 2.0
                ADVANCED_SEARCH_FOR_SCENT -> INNOVATE_SCENT_INVESTIGATION_ITEM
                ADVANCED_SEARCH_FOR_SOUND -> INNOVATE_SOUND_INVESTIGATION_ITEM
                ADVANCED_SEARCH_FOR_OMEN -> INNOVATE_OMEN_INVESTIGATION_ITEM
                ADVANCED_SEARCH_FOR_CORRUPTION -> INNOVATE_CORRUPTION_INVESTIGATION_ITEM
                ADVANCED_SEARCH_FOR_DISTORTION -> INNOVATE_DISTORTION_INVESTIGATION_ITEM
                ADVANCED_SEARCH_FOR_AURA -> INNOVATE_AURA_INVESTIGATION_ITEM
                //advanced items
                TOWN_PORTAL_BY_AMULET -> TOWN_PORTAL_AMULET
                KINDLE_CLOAK -> CLOAK_OF_FLAMES
                INVULNERABILITY -> INVULNERABILITY_POTION
                GREAT_DISPELL -> DISPELL_STICK
                //advanced cultist items
                CLEAN_UP_CLUE -> DUST_OF_DISAPPEARANCE
                DARK_TEMPTATION -> CIRCLET_OF_NOBILITY
                //class based abilities
                HEAL_MADNESS, SPAWN_LOOT, TAKE_FINGERPRINTS, FARSIGHT, PARALYSE -> null
            }
        }
}

