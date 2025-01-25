package com.arkhamusserver.arkhamus.view.maker

import com.arkhamusserver.arkhamus.model.database.entity.GameInvitation
import com.arkhamusserver.arkhamus.view.dto.GameInvitationDto
import com.arkhamusserver.arkhamus.view.dto.ShortGameInfoDto
import com.arkhamusserver.arkhamus.view.dto.user.UserDto
import org.springframework.stereotype.Component

@Component
class GameInvitationDtoMaker {
    fun convert(
        invitation: GameInvitation
    ): GameInvitationDto {
        return GameInvitationDto(
            id = invitation.id!!,
            sourceUser = UserDto(
                invitation.sourceUserAccount!!.id!!,
                invitation.sourceUserAccount!!.steamId,
                invitation.sourceUserAccount!!.nickName
            ),
            targetUser = UserDto(
                invitation.targetUserAccount!!.id!!,
                invitation.targetUserAccount!!.steamId,
                invitation.targetUserAccount!!.nickName
            ),
            shortGameInfo = ShortGameInfoDto(
                gameId = invitation.gameSession!!.id!!,
                gameType = invitation.gameSession!!.gameType,
                token = invitation.gameSession!!.token,
                lobbySize = invitation.gameSession!!.gameSessionSettings.lobbySize
            ),
        )
    }

}