package com.arkhamusserver.arkhamus.logic.ingame

import com.arkhamusserver.arkhamus.model.enums.ingame.Ability
import com.arkhamusserver.arkhamus.view.dto.ingame.AbilityDto
import com.arkhamusserver.arkhamus.view.maker.ingame.AbilityDtoMaker
import org.springframework.stereotype.Component

@Component
class AbilityLogic(private val abilityDtoMaker: AbilityDtoMaker) {
    fun listAllAbilities(): List<AbilityDto> {
        return abilityDtoMaker.convert(Ability.values())
    }

}