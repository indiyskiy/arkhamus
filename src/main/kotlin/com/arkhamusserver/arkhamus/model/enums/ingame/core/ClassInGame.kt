package com.arkhamusserver.arkhamus.model.enums.ingame.core

import com.arkhamusserver.arkhamus.model.enums.ingame.core.RoleTypeInGame.CULTIST
import com.arkhamusserver.arkhamus.model.enums.ingame.core.RoleTypeInGame.INVESTIGATOR

enum class ClassInGame(
    val id: Int,
    val roleType: RoleTypeInGame,
    val turnedOn: Boolean = true
) {
    MIND_HEALER(101, INVESTIGATOR, false),
    BREADWINNER(102, INVESTIGATOR, false),
    FORENSIC_SCIENTIST(103, INVESTIGATOR),

    ARISTOCRAT(201, CULTIST),
    DESCENDANT(202, CULTIST)
}

