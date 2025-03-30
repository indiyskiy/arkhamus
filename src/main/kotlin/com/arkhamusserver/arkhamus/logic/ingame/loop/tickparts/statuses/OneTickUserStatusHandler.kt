package com.arkhamusserver.arkhamus.logic.ingame.loop.tickparts.statuses

import com.arkhamusserver.arkhamus.logic.ingame.logic.utils.tech.generateRandomId
import com.arkhamusserver.arkhamus.logic.ingame.loop.entrity.GlobalGameData
import com.arkhamusserver.arkhamus.model.dataaccess.ingame.InGameUserStatusHolderRepository
import com.arkhamusserver.arkhamus.model.ingame.InGameUserStatusHolder
import org.springframework.stereotype.Component

@Component
class OneTickUserStatusHandler(
    private val inGameUserStatusHolderRepository: InGameUserStatusHolderRepository,
    private val oneTickUserStatusHandlerParts: List<OneTickUserStatusHandlerPart>,
) {
    fun nullifyOldStatuses(data: GlobalGameData) {
        data.userStatuses.forEach {
            it.prolongation = false
        }
    }

    fun updateStatuses(data: GlobalGameData) {
        val updatedStatuses: List<InGameUserStatusHolder> = oneTickUserStatusHandlerParts.flatMap { handler ->
            updateStatusesOneHandler(handler, data)
        }
        cleanStatuses(data)
        data.userStatuses = updatedStatuses
    }

    private fun updateStatusesOneHandler(
        part: OneTickUserStatusHandlerPart,
        data: GlobalGameData
    ): List<InGameUserStatusHolder> {
        val simpleStatuses = part.updateStatuses(data)
        val newStatuses = simpleStatuses.map { simpleStatus ->
            updateOrCreateUserStatus(data, simpleStatus)
        }
        return newStatuses
    }

    private fun updateOrCreateUserStatus(
        data: GlobalGameData,
        simpleStatus: SimpleStatus
    ): InGameUserStatusHolder {
        val oldSameStatus = data.userStatuses.firstOrNull {
            it.userId == simpleStatus.userId &&
                    it.status == simpleStatus.inGameStatus
        }
        return if (oldSameStatus != null) {
            updateOldStatus(oldSameStatus)
        } else {
            createNewStatus(data, simpleStatus)
        }
    }

    private fun updateOldStatus(oldSameStatus: InGameUserStatusHolder): InGameUserStatusHolder {
        oldSameStatus.prolongation = true
        inGameUserStatusHolderRepository.save(oldSameStatus)
        return oldSameStatus
    }

    private fun createNewStatus(
        data: GlobalGameData,
        status: SimpleStatus
    ): InGameUserStatusHolder {
        val inGameUserStatusHolder = InGameUserStatusHolder(
            id = generateRandomId(),
            gameId = data.game.inGameId(),
            userId = status.userId,
            status = status.inGameStatus,
            prolongation = true,
            started = data.game.globalTimer,
        )
        inGameUserStatusHolderRepository.save(inGameUserStatusHolder)
        return inGameUserStatusHolder
    }

    private fun cleanStatuses(data: GlobalGameData) {
        data.userStatuses.filter {
            it.prolongation == false
        }.forEach {
            inGameUserStatusHolderRepository.delete(it)
        }
    }
}