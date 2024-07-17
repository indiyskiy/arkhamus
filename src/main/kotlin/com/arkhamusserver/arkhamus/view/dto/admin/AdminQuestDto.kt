package com.arkhamusserver.arkhamus.view.dto.admin

import com.arkhamusserver.arkhamus.model.enums.ingame.QuestState

data class AdminQuestDto(
    var id: Long = 0,
    var levelId: Long = 0,
    var name: String = "",
    var state: QuestState = QuestState.DRAFT,
    var steps: MutableList<QuestStepDto> = mutableListOf(),
    var startQuestGiver: QuestGiverDto = QuestGiverDto(),
    var endQuestGiver: QuestGiverDto = QuestGiverDto(),
)

data class QuestStepDto(
    var id: Long = 0,
    var number: Int = 0,
    var levelTask: LevelTaskDto = LevelTaskDto()
)

data class LevelTaskDto(
    var id: Long = 0,
    var name: String = "",
)

data class QuestGiverDto(
    var id: Long = 0,
    var name: String = "",
)