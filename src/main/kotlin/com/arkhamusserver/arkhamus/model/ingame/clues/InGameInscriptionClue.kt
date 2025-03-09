package com.arkhamusserver.arkhamus.model.ingame.clues

import com.arkhamusserver.arkhamus.model.dataaccess.ingame.interfaces.InGameEntity
import com.arkhamusserver.arkhamus.model.enums.ingame.tag.VisibilityModifier
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithPoint
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithTrueIngameId
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithVisibilityModifiers
import com.arkhamusserver.arkhamus.model.ingame.parts.InGameInscriptionClueGlyph

data class InGameInscriptionClue(
    override val id: String,
    override val gameId: Long,
    val inGameInscriptionId: Long,
    val interactionRadius: Double,
    val effectRadius: Double,
    val x: Double,
    val y: Double,
    val z: Double,
    val visibilityModifiers: Set<VisibilityModifier>,
    var inscriptionClueGlyphs: List<InGameInscriptionClueGlyph>,
    var castedAbilityUsers: Set<Long> = emptySet(),
    var turnedOn: Boolean,
    var value : Int = 0
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
        return inGameInscriptionId
    }

    override fun visibilityModifiers(): Set<VisibilityModifier> {
        return visibilityModifiers
    }
}