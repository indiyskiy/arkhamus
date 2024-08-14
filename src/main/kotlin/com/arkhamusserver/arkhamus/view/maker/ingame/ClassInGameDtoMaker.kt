package com.arkhamusserver.arkhamus.view.maker.ingame

import com.arkhamusserver.arkhamus.model.enums.ingame.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.ClassInGame
import com.arkhamusserver.arkhamus.view.dto.ingame.ClassInGameDto
import org.springframework.stereotype.Component

@Component
class ClassInGameDtoMaker(
    private val abilityDtoMaker: AbilityDtoMaker
) {
    fun convert(
        values: Array<ClassInGame>,
        abilityMap: Map<ClassInGame, Ability?>
    ): List<ClassInGameDto> {
        return values.map { convert(it, abilityMap[it]) }
    }

    fun convert(value: ClassInGame, defaultAbility: Ability?): ClassInGameDto {
        return ClassInGameDto(
            id = value.id,
            name = value.name,
            roleType = value.roleType,
            defaultAbility = defaultAbility?.let { abilityDtoMaker.convert(it) }
        )
    }
}