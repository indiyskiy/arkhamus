package com.arkhamusserver.arkhamus.model.enums.ingame

enum class ActivityType {
    //main game
    HEARTBEAT, //\/
    USER_GOT_MAD,//\/

    //containers
    CONTAINER_OPENED, //\/
    CONTAINER_CLOSED,

    //crafter
    CRAFTER_OPENED, //\/
    CRAFTER_CLOSED,
    CRAFT_STARTED, //\/

    //abilities
    ABILITY_CASTED, //\/

    //altar
    ALTAR_VOTE_STARTED,
    ALTAR_VOTE_CASTED,
    ALTAR_VOTE_FAILED,
    ALTAR_RITUAL_STARTED,
    ALTAR_RITUAL_FAILED,
    ALTAR_RITUAL_COMPLETED_WRONG,

    //ban votes
    BAN_SPOT_PAYED,
    BAN_SPOT_CALL_STARTED,
    BAN_SPOT_VOTE_CASTED,
    BAN_SPOT_USER_BANED,

    //lantern
    LANTERN_FILLED, //\/
    LANTERN_LIT, //\/

    //clues
    CLUE_CREATED,
    CLUE_DELETED,

    //quests
    QUEST_READ,
    QUEST_ACCEPTED,
    TASK_DONE,
    QUEST_REJECTED,
    QUEST_COMPLETE
}