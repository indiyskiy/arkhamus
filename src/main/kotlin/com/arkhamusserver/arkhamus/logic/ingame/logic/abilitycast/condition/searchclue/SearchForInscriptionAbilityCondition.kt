package com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.condition.searchclue

import com.arkhamusserver.arkhamus.logic.ingame.logic.abilitycast.condition.AdditionalAbilityCondition
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.UserLocationHandler
import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.GameObjectFinder
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.model.ingame.clues.InGameInscriptionClue
import com.arkhamusserver.arkhamus.model.ingame.parts.InGameInscriptionClueGlyph
import org.springframework.stereotype.Component

@Component
class SearchForInscriptionAbilityCondition(
    private val userLocationHandler: UserLocationHandler,
    private val gameObjectFinder: GameObjectFinder
) : AdditionalAbilityCondition {

    override fun accepts(ability: Ability): Boolean =
        ability == Ability.SEARCH_FOR_INSCRIPTION

    override fun canBeCastedRightNow(
        ability: Ability,
        user: InGameUser,
        target: Any?,
        globalGameData: GlobalGameData
    ): Boolean {
        return target != null &&
                target is InGameInscriptionClueGlyph &&
                userLocationHandler.userCanSeeTargetInRange(
                    user,
                    target,
                    globalGameData.levelGeometryData,
                    ability.range ?: 0.0,
                    true,
                ) &&
                with(findClue(globalGameData, target)) {
                    this != null &&
                            !this.castedAbilityUsers.contains(user.inGameId())
                }
    }

    private fun findClue(
        globalGameData: GlobalGameData,
        target: InGameInscriptionClueGlyph
    ): InGameInscriptionClue? =
        globalGameData.clues.inscription.firstOrNull {
            it.inscriptionClueGlyphs.any {
                it.inGameId == target.inGameId
            }
        }

    override fun canBeCastedAtAll(
        ability: Ability,
        user: InGameUser,
        globalGameData: GlobalGameData
    ): Boolean {
        return gameObjectFinder.all(
            ability.targetTypes ?: emptyList(),
            globalGameData
        ).any {
            return canBeCastedRightNow(
                ability,
                user,
                it,
                globalGameData
            )
        }
    }

}