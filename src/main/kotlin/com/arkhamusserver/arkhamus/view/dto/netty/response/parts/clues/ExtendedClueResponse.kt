package com.arkhamusserver.arkhamus.view.dto.netty.response.parts.clues

import com.arkhamusserver.arkhamus.model.enums.ingame.GameObjectType
import com.arkhamusserver.arkhamus.model.enums.ingame.core.Clue
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.InnovateClueState
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.clues.additional.AdditionalClueDataResponse

data class ExtendedClueResponse(
    val id: String,
    val clue: Clue,
    val relatedObjectId: Long?,
    val relatedObjectType: GameObjectType?,
    val x: Double?,
    val y: Double?,
    val z: Double?,
    val possibleRadius: Double?,
    var state: InnovateClueState,
    val additionalData: AdditionalClueDataResponse? = null,
)