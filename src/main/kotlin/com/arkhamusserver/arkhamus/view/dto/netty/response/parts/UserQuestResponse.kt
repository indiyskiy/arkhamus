package com.arkhamusserver.arkhamus.view.dto.netty.response.parts

import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.InteractionQuestType
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.MapObjectState
import com.arkhamusserver.arkhamus.model.enums.ingame.objectstate.UserQuestState

data class UserQuestResponse(
    var id: String,
    var questId: Long?,
    var questState: UserQuestState,
    var questCurrentStep: Int = -1,
    var questStepIds: List<Long> = emptyList(),
    var endQuestGiverId: Long? = null,
    var startQuestGiverId: Long? = null,
    var textKey: String?,
    var creationGameTime: Long,
    var readGameTime: Long? = null,
    var acceptanceGameTime: Long? = null,
    var finishGameTime: Long? = null,
)

data class QuestGiverResponse(
    var id: Long,
    var state: MapObjectState,
    var questProgressDataResponses: List<QuestProgressDataResponse>
)

data class QuestStepResponse(
    var id: Long,
    var state: MapObjectState,
    var questProgressDataResponses: List<QuestProgressDataResponse>
)

data class QuestProgressDataResponse(
    var questStepId: Long,
    var interactionQuestType: InteractionQuestType,
    var questId: Long,
    var currentStep: Boolean,
    val questProgressId: String,
)