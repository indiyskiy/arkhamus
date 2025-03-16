package com.arkhamusserver.arkhamus.model.ingame

import com.arkhamusserver.arkhamus.model.dataaccess.ingame.interfaces.InGameEntity
import com.arkhamusserver.arkhamus.model.enums.ingame.core.ClassInGame
import com.arkhamusserver.arkhamus.model.enums.ingame.core.RoleTypeInGame
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.UserStateTag
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithPoint
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithTrueIngameId
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithVisibilityModifiers
import com.arkhamusserver.arkhamus.model.ingame.parts.AdditionalInGameUserData
import com.arkhamusserver.arkhamus.model.ingame.parts.TechInGameUserData

data class InGameUser(
    override var id: String,
    var userId: Long,
    var role: RoleTypeInGame,
    var classInGame: ClassInGame,
    override var gameId: Long,
    var x: Double,
    var y: Double,
    var z: Double,
    var stateTags: Set<UserStateTag> = emptySet(),
    var visibilityModifiers: Set<VisibilityModifier>,
    val additionalData: AdditionalInGameUserData,
    val techData: TechInGameUserData
) : InGameEntity, WithPoint, WithTrueIngameId, WithVisibilityModifiers {

    override fun x(): Double {
        return x
    }

    override fun y(): Double {
        return y
    }

    override fun z(): Double {
        return z
    }

    override fun inGameId(): Long {
        return userId
    }

    override fun visibilityModifiers(): Set<VisibilityModifier> {
        return visibilityModifiers
    }
}