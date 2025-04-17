package com.arkhamusserver.arkhamus.model.enums.ingame.objectstate

enum class InteractionQuestType(
    val priority: Int
) {
    QUEST_START(30),
    QUEST_PROGRESS(20),
    QUEST_END(10),
}