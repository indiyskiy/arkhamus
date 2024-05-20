package com.arkhamusserver.arkhamus.logic.admin

import com.arkhamusserver.arkhamus.logic.UserSkinLogic
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.GameSessionRepository
import com.arkhamusserver.arkhamus.view.dto.GameSessionDto
import com.arkhamusserver.arkhamus.view.maker.GameSessionDtoMaker
import org.springframework.stereotype.Component

@Component
class AdminGameLogic(
    private val gameSessionRepository: GameSessionRepository,
    private val gameSessionDtoMaker: GameSessionDtoMaker,
    private val userSkinLogic: UserSkinLogic,
) {
    fun all(): List<GameSessionDto> {
        return gameSessionRepository.findAll().map { game ->
            val skins = userSkinLogic.allSkinsOf(game)
            gameSessionDtoMaker.toDtoAsAdmin(game, skins)
        }
    }


}