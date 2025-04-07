package com.arkhamusserver.arkhamus.model.enums.ingame.core

import com.arkhamusserver.arkhamus.model.enums.ingame.core.RoleTypeInGame.CULTIST
import com.arkhamusserver.arkhamus.model.enums.ingame.core.RoleTypeInGame.INVESTIGATOR

enum class ClassInGame(
    val id: Int,
    val roleType: RoleTypeInGame,
    val globalTurnedOn: Boolean = true
) {
    MIND_HEALER(101, INVESTIGATOR),
    BREADWINNER(102, INVESTIGATOR),
    FORENSIC_SCIENTIST(103, INVESTIGATOR),
    KEEPER(103, INVESTIGATOR),

    ARISTOCRAT(201, CULTIST),
    DESCENDANT(202, CULTIST),
    MADNESS_SHIFTER(203, CULTIST),
    DOOR_LOCKER(204, CULTIST),
}

