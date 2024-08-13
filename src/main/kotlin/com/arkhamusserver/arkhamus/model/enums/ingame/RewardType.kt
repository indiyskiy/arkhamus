package com.arkhamusserver.arkhamus.model.enums.ingame

enum class RewardType(
    private val oneForQuest: Boolean = false,
) {
    ITEM,
    ADD_CLUE(true),
    REMOVE_CLUE(true);

    fun getOneForQuest(): Boolean = oneForQuest
}