package com.arkhamusserver.arkhamus.view.dto.netty.response.parts

import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.MapObjectState
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.UserQuestState

data class UserQuestResponse(
    var id: String,
    var questId: Long?,
    var questState: UserQuestState,
    var questCurrentStep: Int = -1,
    var questSteps: List<QuestStepResponse> = emptyList(),
    var endQuestGiver: QuestGiverResponse? = null,
    var startQuestGiver: QuestGiverResponse? = null,
    var textKey: String?,
    var creationGameTime: Long,
    var readGameTime: Long? = null,
    var acceptanceGameTime: Long? = null,
    var finishGameTime: Long? = null,
)

data class QuestGiverResponse(
    var id: Long,
    var state: MapObjectState,
)
data class QuestStepResponse(
    var id: Long,
    var state: MapObjectState,
)