package com.arkhamusserver.arkhamus.logic.ingame.logic.utils

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.generateRandomId
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.statuses.SimpleStatus
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.InGameUserStatusHolderRepository
import com.arkhamusserver.arkhamus.model.enums.ingame.InGameUserStatus
import com.arkhamusserver.arkhamus.model.ingame.InGameUser
import com.arkhamusserver.arkhamus.model.ingame.InGameUserStatusHolder
import org.springframework.stereotype.Component

@Component
class UserStatusHandler(
    private val inGameUserStatusHolderRepository: InGameUserStatusHolderRepository
) {
    fun forceAddStatus(
        user: InGameUser,
        status: InGameUserStatus,
        data: GlobalGameData
    ) {
        val inGameUserStatusHolder = createNewStatus(data, SimpleStatus(user.userId, status))
        inGameUserStatusHolderRepository.save(inGameUserStatusHolder)
        data.userStatuses += inGameUserStatusHolder
    }

    fun createNewStatus(
        data: GlobalGameData,
        status: SimpleStatus
    ): InGameUserStatusHolder {
        return createNewStatus(
            data,
            status.userId,
            status.inGameStatus
        )
    }

    fun createNewStatus(
        data: GlobalGameData,
        userId: Long,
        inGameStatus: InGameUserStatus
    ): InGameUserStatusHolder {
        val inGameUserStatusHolder = InGameUserStatusHolder(
            id = generateRandomId(),
            gameId = data.game.inGameId(),
            userId = userId,
            status = inGameStatus,
            prolongation = true,
            started = data.game.globalTimer,
        )
        inGameUserStatusHolderRepository.save(inGameUserStatusHolder)
        return inGameUserStatusHolder
    }

}