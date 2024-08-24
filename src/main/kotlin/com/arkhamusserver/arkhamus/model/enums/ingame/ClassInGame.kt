package com.arkhamusserver.arkhamus.model.enums.ingame

import com.arkhamusserver.arkhamus.model.enums.ingame.RoleTypeInGame.CULTIST
import com.arkhamusserver.arkhamus.model.enums.ingame.RoleTypeInGame.INVESTIGATOR

enum class ClassInGame(val id: Int, val roleType: RoleTypeInGame) {
    MIND_HEALER(101, INVESTIGATOR),
    BREADWINNER(102, INVESTIGATOR),

    ARISTOCRAT(201, CULTIST),
    DESCENDANT(202, CULTIST)
}