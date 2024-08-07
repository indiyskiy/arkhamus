package com.arkhamusserver.arkhamus.view.dto.admin

import com.arkhamusserver.arkhamus.model.enums.ingame.QuestDifficulty
import com.arkhamusserver.arkhamus.model.enums.ingame.QuestState

data class AdminQuestDto(
    var id: Long = 0,
    var textKey: String = "",
    var levelId: Long = 0,
    var name: String = "",
    var state: QuestState = QuestState.DRAFT,
    var steps: MutableList<AdminQuestStepDto> = mutableListOf(),
    var startQuestGiver: AdminQuestGiverDto = AdminQuestGiverDto(),
    var endQuestGiver: AdminQuestGiverDto = AdminQuestGiverDto(),
    var difficulty: QuestDifficulty = QuestDifficulty.NORMAL
)

data class AdminQuestStepDto(
    var id: Long = 0,
    var number: Int = 0,
    var levelTask: AdminLevelTaskDto = AdminLevelTaskDto()
)

data class AdminLevelTaskDto(
    var id: Long = 0,
    var name: String = "",
)

data class AdminQuestGiverDto(
    var id: Long = 0,
    var name: String = "",
)