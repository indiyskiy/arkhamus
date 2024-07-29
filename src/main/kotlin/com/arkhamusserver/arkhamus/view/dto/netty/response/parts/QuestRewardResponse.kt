package com.arkhamusserver.arkhamus.view.dto.netty.response.parts

import com.arkhamusserver.arkhamus.model.enums.ingame.RewardType

data class QuestRewardResponse(
    var rewardType: RewardType,
    var rewardAmount: Int = 0,
    var rewardItem: Int?
)