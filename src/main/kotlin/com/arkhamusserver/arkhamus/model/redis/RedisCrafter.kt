package com.arkhamusserver.arkhamus.model.redis

import com.arkhamusserver.arkhamus.model.dataaccess.redis.interfaces.RedisGameEntity
import com.arkhamusserver.arkhamus.model.enums.ingame.core.CrafterType
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.MapObjectState
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.InGameObjectTag
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.redis.interfaces.*
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.InventoryCell

data class RedisCrafter(
    override var id: String,
    override var gameId: Long,
    var crafterId: Long,
    var holdingUser: Long? = null,
    var state: MapObjectState = MapObjectState.ACTIVE,
    var x: Double,
    var y: Double,
    var z: Double,
    var interactionRadius: Double = 0.0,
    var items: List<InventoryCell> = emptyList(),
    var crafterType: CrafterType,
    var gameTags: Set<InGameObjectTag> = setOf(),
    var visibilityModifiers: Set<VisibilityModifier>,
) : RedisGameEntity, WithPoint, WithTrueIngameId, WithGameTags, WithVisibilityModifiers, Interactable {

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
        return crafterId
    }

    override fun visibilityModifiers(): Set<VisibilityModifier> {
        return visibilityModifiers
    }

    override fun interactionRadius(): Double {
        return interactionRadius
    }
}