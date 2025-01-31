package com.arkhamusserver.arkhamus.model.ingame

import com.arkhamusserver.arkhamus.model.dataaccess.ingame.interfaces.InGameEntity
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.MapObjectState
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.InGameObjectTag
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.ingame.interfaces.*
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.InventoryCell

data class InGameContainer(
    override var id: String,
    override var gameId: Long,
    var containerId: Long,
    var holdingUser: Long? = null,
    var state: MapObjectState = MapObjectState.ACTIVE,
    var x: Double,
    var y: Double,
    var z: Double,
    var containerTags: MutableSet<String> = mutableSetOf(),
    var interactionRadius: Double = 0.0,
    var items: List<InventoryCell> = emptyList(),
    var gameTags: Set<InGameObjectTag> = setOf(),
    var visibilityModifiers: Set<VisibilityModifier>,
) : InGameEntity, WithPoint, WithTrueIngameId, WithGameTags, WithVisibilityModifiers, Interactable {

    override fun x(): Double {
        return x
    }

    override fun y(): Double {
        return y
    }

    override fun z(): Double {
        return z
    }

    override fun gameTags(): Set<InGameObjectTag> {
        return gameTags
    }

    override fun writeGameTags(gameTags: Set<InGameObjectTag>) {
        this.gameTags = gameTags
    }

    override fun inGameId(): Long {
        return containerId
    }

    override fun visibilityModifiers(): Set<VisibilityModifier> {
        return visibilityModifiers
    }

    override fun interactionRadius(): Double {
        return interactionRadius
    }
}