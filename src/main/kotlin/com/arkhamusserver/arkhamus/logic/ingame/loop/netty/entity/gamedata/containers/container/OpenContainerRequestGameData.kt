package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.containers.container

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.GameUserData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.parts.LevelZone
import com.arkhamusserver.arkhamus.model.redis.*
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.AbilityOfUserResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.CraftProcessResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.InventoryCell

class OpenContainerRequestGameData(
    var container: RedisContainer,
    gameUser: RedisGameUser,
    otherGameUsers: List<RedisGameUser>,
    inZones: List<LevelZone>,
    visibleOngoingEvents: List<OngoingEvent>,
    availableAbilities: List<AbilityOfUserResponse>,
    ongoingCraftingProcess: List<CraftProcessResponse>,
    visibleItems: List<InventoryCell>,
    containers: List<RedisContainer>,
    crafters: List<RedisCrafter>,
    clues: List<RedisClue>,
    userQuestProgresses: List<RedisUserQuestProgress>,
    tick: Long
) : GameUserData(
    gameUser = gameUser,
    otherGameUsers = otherGameUsers,
    inZones = inZones,
    visibleOngoingEvents = visibleOngoingEvents,
    availableAbilities = availableAbilities,
    ongoingCraftingProcess = ongoingCraftingProcess,
    visibleItems = visibleItems,
    containers = containers,
    crafters = crafters,
    clues = clues,
    userQuestProgresses = userQuestProgresses,
    tick = tick
)