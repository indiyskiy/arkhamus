package com.arkhamusserver.arkhamus.model.ingame.clues

import com.arkhamusserver.arkhamus.model.dataaccess.ingame.interfaces.InGameEntity
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithPoint
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithTrueIngameId
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithVisibilityModifiers
import com.arkhamusserver.arkhamus.model.ingame.parts.InGameSoundClueJammer

data class InGameSoundClue(
    override val id: String,
    override val gameId: Long,
    val inGameSoundId: Long,
    val x: Double,
    val y: Double,
    val z: Double,
    val visibilityModifiers: Set<VisibilityModifier>,
    var turnedOn: Boolean,
    var zoneId: Long,
    var soundClueJammers: List<InGameSoundClueJammer>
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
        return inGameSoundId
    }

    override fun visibilityModifiers(): Set<VisibilityModifier> {
        return visibilityModifiers
    }
}