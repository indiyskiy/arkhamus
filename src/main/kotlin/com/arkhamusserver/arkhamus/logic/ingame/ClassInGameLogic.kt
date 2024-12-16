package com.arkhamusserver.arkhamus.logic.ingame

import com.arkhamusserver.arkhamus.logic.ingame.item.AbilityToClassResolver
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.enums.ingame.core.ClassInGame
import com.arkhamusserver.arkhamus.view.dto.ingame.ClassInGameDto
import com.arkhamusserver.arkhamus.view.maker.ingame.ClassInGameDtoMaker
import org.springframework.stereotype.Component

@Component
class ClassInGameLogic(
    private val abilityToClassResolver: AbilityToClassResolver,
    private val classInGameDtoMaker: ClassInGameDtoMaker
) {

    fun listAllClasses(): List<ClassInGameDto> {
        val abilityMap = ClassInGame
            .values()
            .associate {
                it to resolveAbility(it)
            }
        return classInGameDtoMaker.convert(ClassInGame.values(), abilityMap)
    }

    private fun resolveAbility(classInGame: ClassInGame): Ability? {
        return Ability.values().firstOrNull {
            abilityToClassResolver.resolve(it)?.contains(classInGame) == true
        }
    }

}