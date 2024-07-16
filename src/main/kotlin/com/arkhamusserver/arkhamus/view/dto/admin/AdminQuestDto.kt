package com.arkhamusserver.arkhamus.view.dto.admin

import com.arkhamusserver.arkhamus.model.enums.ingame.QuestState

data class AdminQuestDto(
    var id: Long? = null,
    var levelId: Long? = null,
    var name: String? = null,
    var state: QuestState? = null,
    var steps:MutableList<QuestStepDto> = mutableListOf()
)

data class QuestStepDto(
    var id: Long? = null,
    var number: Int? = null,
    var levelTask: LevelTaskDto? = null
)

data class LevelTaskDto(
    var id: Long? = null,
    var name: String? = null,
)