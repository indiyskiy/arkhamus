package com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.banvote

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.OngoingEvent
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.GameUserData
import com.arkhamusserver.arkhamus.logic.ingame.loop.netty.entity.gamedata.parts.LevelZone
import com.arkhamusserver.arkhamus.model.ingame.*
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.AbilityOfUserResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.CraftProcessResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.InventoryCell
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.UserQuestResponse
import com.arkhamusserver.arkhamus.view.dto.netty.response.parts.clues.ExtendedCluesResponse

class VoteSpotCastRequestProcessData(
    var targetUser: InGameUser?,
    var voteSpot: InGameVoteSpot?,
    var currentUserVoteSpot: InGameUserVoteSpot?,
    var thisSpotUserInfos: List<InGameUserVoteSpot>,
    var canVoteForTargetUser: Boolean,
    var successfullyVoted: Boolean,
    var targetUserBanned: Boolean,
    var canVote: Boolean,
    inZones: List<LevelZone>,
    ongoingCraftingProcess: List<CraftProcessResponse>,
    gameUser: InGameUser,
    otherGameUsers: List<InGameUser>,
    visibleOngoingEvents: List<OngoingEvent>,
    availableAbilities: List<AbilityOfUserResponse>,
    visibleItems: List<InventoryCell>,
    containers: List<InGameContainer>,
    crafters: List<InGameCrafter>,
    clues: ExtendedCluesResponse,
    userQuestProgresses: List<UserQuestResponse>,
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
    userQuest = userQuestProgresses,
    tick = tick
)