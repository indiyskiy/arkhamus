package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.statuses

import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.enums.ingame.InGameUserStatus
import com.arkhamusserver.arkhamus.model.enums.ingame.MadnessDebuff
import com.arkhamusserver.arkhamus.model.enums.ingame.MadnessDebuff.*
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import org.springframework.stereotype.Component

@Component
class OneTickUserStatusHandlerMadnessPart() : OneTickUserStatusHandlerPart {
    override fun updateStatuses(data: GlobalGameData): List<SimpleStatus> {
        return data.users.flatMap { user ->
            user.value.additionalData.madness.madnessDebuffs.map { debuff ->
                createSimpleStatus(user.value, debuff)
            }
        }
    }

    private fun createSimpleStatus(
        user: InGameUser,
        madnessDebuff: MadnessDebuff
    ): SimpleStatus {
        return SimpleStatus(
            userId = user.inGameId(),
            inGameStatus = getStatusByDebuff(madnessDebuff)
        )
    }

    private fun getStatusByDebuff(debuff: MadnessDebuff): InGameUserStatus {
        return when (debuff) {
            BLIND -> InGameUserStatus.BLIND
            PSYCHIC_UNSTABLE -> InGameUserStatus.PSYCHIC_UNSTABLE
            CURSED_AURA -> InGameUserStatus.CURSED_AURA
            MAGIC_ADDICTED -> InGameUserStatus.MAGIC_ADDICTED
            CRAFT_ADDICTED -> InGameUserStatus.CRAFT_ADDICTED
            BAN_ADDICTED -> InGameUserStatus.BAN_ADDICTED
            LIGHT_ADDICTED -> InGameUserStatus.LIGHT_ADDICTED
            UNSTABLE_POSITION -> InGameUserStatus.UNSTABLE_POSITION
            PROPHET -> InGameUserStatus.PROPHET
        }
    }

}