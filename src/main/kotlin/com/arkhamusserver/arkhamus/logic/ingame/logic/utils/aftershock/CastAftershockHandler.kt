package com.arkhamusserver.arkhamus.logic.ingame.logic.utils.aftershock

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Ability
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithStringId

interface CastAftershockHandler {
    fun accept(ability: Ability): Boolean
    fun processCastAftershocks(
        ability: Ability,
        sourceUser: InGameUser,
        target: WithStringId?,
        data: GlobalGameData
    )
}