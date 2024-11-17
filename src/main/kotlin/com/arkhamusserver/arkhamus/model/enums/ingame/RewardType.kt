package com.arkhamusserver.arkhamus.model.enums.ingame

enum class RewardType(
    private val onlyOneForQuest: Boolean = false,
) {
    ITEM,
    ADD_CLUE(true);

    fun getOnlyOneForQuest(): Boolean = onlyOneForQuest
}