package com.arkhamusserver.arkhamus.logic.ingame

import com.arkhamusserver.arkhamus.logic.ingame.item.AbilityToItemResolver
import com.arkhamusserver.arkhamus.model.enums.ingame.Ability
import com.arkhamusserver.arkhamus.view.dto.ingame.AbilityDto
import com.arkhamusserver.arkhamus.view.dto.ingame.ItemToAbilityDto
import com.arkhamusserver.arkhamus.view.dto.ingame.SimpleItemToAbilityDto
import com.arkhamusserver.arkhamus.view.maker.ingame.AbilityDtoMaker
import com.arkhamusserver.arkhamus.view.maker.ingame.ItemInformationDtoMaker
import org.springframework.stereotype.Component

@Component
class AbilityLogic(
    private val abilityDtoMaker: AbilityDtoMaker,
    private val itemInformationDtoMaker: ItemInformationDtoMaker,
    private val abilityToItemResolver: AbilityToItemResolver
) {
    fun listAllAbilities(): List<AbilityDto> {
        return abilityDtoMaker.convert(Ability.values())
    }

    fun getItemsToAbilityMap(): List<ItemToAbilityDto> =
        Ability
            .values()
            .filter { it.requiresItem }
            .map {
                abilityToItemResolver.resolve(it) to it
            }
            .filter { it.first != null }
            .map { (item, ability) ->
                ItemToAbilityDto(
                    item = itemInformationDtoMaker.convert(item!!),
                    ability = abilityDtoMaker.convert(ability)
                )
            }

    fun getSimpleItemsToAbilityMap(): List<SimpleItemToAbilityDto> =
        Ability
            .values()
            .filter { it.requiresItem }
            .map {
                abilityToItemResolver.resolve(it) to it
            }
            .filter { it.first != null }
            .map { (item, ability) ->
                SimpleItemToAbilityDto(
                    itemId = item!!.id,
                    abilityId = ability.id
                )
            }

}