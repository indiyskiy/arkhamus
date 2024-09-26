package com.arkhamusserver.arkhamus.view.dto.netty.response.parts

import com.arkhamusserver.arkhamus.model.enums.ingame.UserQuestState

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