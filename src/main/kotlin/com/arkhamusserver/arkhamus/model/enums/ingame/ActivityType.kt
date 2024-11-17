package com.arkhamusserver.arkhamus.model.enums.ingame

enum class ActivityType {
    //main game
    HEARTBEAT, //\/
    USER_GOT_MAD,//\/

    //containers
    CONTAINER_OPENED, //\/
    CONTAINER_CLOSED, //don't want yet

    //crafter
    CRAFTER_OPENED, //\/
    CRAFTER_CLOSED, //don't want yet
    CRAFT_STARTED, //\/

    //abilities
    ABILITY_CASTED, //\/

    //altar
    ALTAR_VOTE_STARTED, //\/
    ALTAR_VOTE_CASTED, //\/
    ALTAR_VOTE_FAILED, //don't want yet
    ALTAR_RITUAL_STARTED, //don't want yet
    ALTAR_RITUAL_FAILED, //don't want yet
    ALTAR_RITUAL_COMPLETED_WRONG, //don't want yet

    //ban votes
    BAN_SPOT_PAYED, //\/
    BAN_SPOT_CALL_STARTED, //\/
    BAN_SPOT_VOTE_CASTED, //\/
    BAN_SPOT_USER_BANED, //\/

    //lantern
    LANTERN_FILLED, //\/
    LANTERN_LIT, //\/

    //clues
    CLUE_CREATED,
    CLUE_DELETED, //don't want yet - it's ability cast

    //quests
    QUEST_READ, //don't want yet
    QUEST_ACCEPTED, //\/
    TASK_DONE, //\/ todo add redisQuestStep, update activity data
    QUEST_DECLINED, //\/
    QUEST_COMPLETE //\/
}