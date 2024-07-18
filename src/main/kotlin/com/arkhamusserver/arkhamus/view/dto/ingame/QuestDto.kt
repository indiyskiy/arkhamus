package com.arkhamusserver.arkhamus.view.dto.ingame

data class QuestDto(
    var id: Long = 0,
    var levelId: Long = 0,
    var name: String = "",
    var steps: List<QuestStepDto> = emptyList(),
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
    var inGameId: Long = 0,
    var name: String = "",
)

data class QuestGiverDto(
    var id: Long = 0,
    var inGameId: Long = 0,
    var name: String = "",
)