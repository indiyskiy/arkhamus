package com.arkhamusserver.arkhamus.model.enums.ingame.objectstate

enum class UserQuestState {
    AWAITING,
    READ,
    DECLINED,
    IN_PROGRESS,
    COMPLETED, //ready to take reward
    FINISHED, //reward taken, quest is finished completely
    FINISHED_AVAILABLE,
    DECLINED_AVAILABLE,
    INVALID
}