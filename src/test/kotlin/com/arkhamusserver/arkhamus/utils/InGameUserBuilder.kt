package com.arkhamusserver.arkhamus.utils

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.generateRandomId
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession
import com.arkhamusserver.arkhamus.model.enums.SkinColor
import com.arkhamusserver.arkhamus.model.enums.ingame.core.ClassInGame
import com.arkhamusserver.arkhamus.model.enums.ingame.core.RoleTypeInGame
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.model.ingame.parts.*

class InGameUserBuilder {
    fun buildPerfectUser(
        userOfGameSession: UserOfGameSession,
        gameSession: GameSession
    ): InGameUser = InGameUser(
        id = generateRandomId(),
        userId = userOfGameSession.id!!,
        role = RoleTypeInGame.INVESTIGATOR,
        classInGame = ClassInGame.MIND_HEALER,
        gameId = gameSession.id!!,
        x = 0.0,
        y = 0.0,
        z = 0.0,
        stateTags = mutableSetOf(),
        visibilityModifiers = mutableSetOf(),
        additionalData = AdditionalInGameUserData(
            madness = MadnessAdditionalInGameUserData(
                madness = 0.0,
                madnessNotches = listOf(100.0, 300.0, 600.0),
                madnessDebuffs = emptySet()
            ),
            inventory = InventoryAdditionalInGameUserData(
                items = emptyList(),
                maxItems = 16
            ),
            callToArms = 1,
            originalSkin = InGameUserSkinSetting(
                nickName = "user-nickname",
                skinColor = SkinColor.LAVENDER
            )
        ),
        techData = TechInGameUserData(
            won = null,
            sawTheEndOfTimes = false,
            connected = true,
            leftTheGame = false
        )
    )
}