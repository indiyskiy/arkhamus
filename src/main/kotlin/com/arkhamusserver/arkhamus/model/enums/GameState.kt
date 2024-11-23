package com.arkhamusserver.arkhamus.model.enums

enum class GameState {
    NEW,
    PENDING,
    IN_PROGRESS,
    GAME_END_SCREEN,
    FINISHED,
    ABANDONED;

    companion object {
        val gameInProgressStates = setOf(IN_PROGRESS, GAME_END_SCREEN)
        val gameInProgressStateStrings = gameInProgressStates.map { it.name }.toSet()
    }
}