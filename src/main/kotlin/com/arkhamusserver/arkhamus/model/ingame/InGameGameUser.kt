package com.arkhamusserver.arkhamus.model.ingame

import com.arkhamusserver.arkhamus.model.dataaccess.ingame.interfaces.InGameEntity
import com.arkhamusserver.arkhamus.model.enums.ingame.core.ClassInGame
import com.arkhamusserver.arkhamus.model.enums.ingame.core.RoleTypeInGame
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.UserStateTag
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithPoint
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithTrueIngameId
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithVisibilityModifiers
import com.arkhamusserver.arkhamus.model.ingame.parts.InGameUserSkinSetting
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.InventoryCell

data class InGameGameUser(
    override var id: String,
    var userId: Long,
    var nickName: String,
    var role: RoleTypeInGame,
    var classInGame: ClassInGame,
    override var gameId: Long,
    var x: Double,
    var y: Double,
    var z: Double,
    var madness: Double,
    var madnessNotches: List<Double>,
    var items: List<InventoryCell> = emptyList(),
    var stateTags: Set<UserStateTag> = emptySet(),
    var madnessDebuffs: Set<String> = emptySet(),
    var callToArms: Int,
    var originalSkin: InGameUserSkinSetting,
    //tech
    var won: Boolean? = null,
    var sawTheEndOfTimes: Boolean = false,
    var connected: Boolean,
    var leftTheGame: Boolean = false,
    var visibilityModifiers: Set<VisibilityModifier>,
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