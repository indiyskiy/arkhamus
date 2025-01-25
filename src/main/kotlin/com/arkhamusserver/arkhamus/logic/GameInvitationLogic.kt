package com.arkhamusserver.arkhamus.logic

import com.arkhamusserver.arkhamus.logic.exception.ArkhamusServerRequestException
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.GameInvitationRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.GameSessionRepository
import com.arkhamusserver.arkhamus.model.dataaccess.sql.repository.UserAccountRepository
import com.arkhamusserver.arkhamus.model.database.entity.GameInvitation
import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.UserAccount
import com.arkhamusserver.arkhamus.model.enums.GameState
import com.arkhamusserver.arkhamus.model.enums.InvitationState.*
import com.arkhamusserver.arkhamus.model.enums.ingame.GameType
import com.arkhamusserver.arkhamus.view.dto.GameInvitationDto
import com.arkhamusserver.arkhamus.view.dto.GameSessionDto
import com.arkhamusserver.arkhamus.view.maker.GameInvitationDtoMaker
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.sql.Timestamp

@Component
class GameInvitationLogic(
    private val gameInvitationRepository: GameInvitationRepository,
    private val gameSessionRepository: GameSessionRepository,
    private val userAccountRepository: UserAccountRepository,
    private val currentUserService: CurrentUserService,
    private val customGameLogic: CustomGameLogic,
    private val gameInvitationDtoMaker: GameInvitationDtoMaker,
) {

    companion object {
        const val MAX_PENDING_INVITATIONS = 16 * 2
        const val RELATED_ENTITY = "GameInvitation"
    }

    @Transactional
    fun myInvitations(): List<GameInvitationDto> {
        val currentUser = currentUserService.getCurrentUserAccount()
        val invitations =
            gameInvitationRepository.findByTargetUserAccountIdAndState(
                currentUser.id!!,
                PENDING
            )
        return invitations.map { gameInvitationDtoMaker.convert(it) }
    }

    @Transactional
    fun gameInvitations(gameId: Long): List<GameInvitationDto> {
        val currentUser = currentUserService.getCurrentUserAccount()
        val game = gameSessionRepository.findById(gameId).orElseThrow()
        if (game.usersOfGameSession.any { it.userAccount.id == currentUser.id }) {
            val invitations = gameInvitationRepository.findByGameSessionIdAndState(
                gameId,
                PENDING
            )
            return invitations.map { gameInvitationDtoMaker.convert(it) }
        } else {
            throw ArkhamusServerRequestException(
                "current user is not a part of a $gameId", RELATED_ENTITY
            )
        }
    }

    @Transactional
    fun createInvitation(gameId: Long, targetUserId: Long): GameInvitationDto {
        val currentUser = currentUserService.getCurrentUserAccount()
        val game = gameSessionRepository.findById(gameId).orElseThrow {
            ArkhamusServerRequestException("Game not found", RELATED_ENTITY)
        }
        val sourceUserInvitations =
            gameInvitationRepository.findBySourceUserAccountIdAndState(
                currentUser.id!!,
                PENDING
            )
        validate(game, sourceUserInvitations, currentUser)

        val targetUser = userAccountRepository.findById(targetUserId).get()
        val invitation = createNewInvitation(game, currentUser, targetUser)
        return gameInvitationDtoMaker.convert(invitation)
    }

    @Transactional
    fun acceptInvitation(invitationId: Long): GameSessionDto {
        val currentUser = currentUserService.getCurrentUserAccount()
        val invitation = gameInvitationRepository.findById(invitationId).orElseThrow {
            ArkhamusServerRequestException("Invitation not found", RELATED_ENTITY)
        }
        if(currentUser.id != invitation.targetUserAccount?.id) {
            throw ArkhamusServerRequestException(
                "can't accept other user invitation", RELATED_ENTITY
            )
        }
        if(invitation.state != PENDING) {
            throw ArkhamusServerRequestException(
                "invitation is not pending", RELATED_ENTITY
            )
        }
        val gamesSession = customGameLogic.connectToGame(invitation.gameSession!!)
        invitation.state = ACCEPTED
        invitation.finishedTimestamp = Timestamp(System.currentTimeMillis())
        gameInvitationRepository.save(invitation)
        return gamesSession
    }

    @Transactional
    fun rejectInvitation(invitationId: Long): GameInvitationDto {
        val currentUser = currentUserService.getCurrentUserAccount()
        val invitation = gameInvitationRepository.findById(invitationId).orElseThrow {
            ArkhamusServerRequestException("Invitation not found", RELATED_ENTITY)
        }
        if(currentUser.id != invitation.targetUserAccount?.id) {
            throw ArkhamusServerRequestException(
                "can't reject other user invitation", RELATED_ENTITY
            )
        }
        if(invitation.state != PENDING) {
            throw ArkhamusServerRequestException(
                "invitation is not pending", RELATED_ENTITY
            )
        }
        invitation.state = REJECTED
        invitation.finishedTimestamp = Timestamp(System.currentTimeMillis())
        gameInvitationRepository.save(invitation)
        return gameInvitationDtoMaker.convert(invitation)
    }

    private fun validate(
        game: GameSession,
        sourceUserInvitations: List<GameInvitation>,
        currentUser: UserAccount
    ) {
        if (game.state !in setOf(GameState.NEW)) {
            throw ArkhamusServerRequestException(
                "Wrong game state", RELATED_ENTITY
            )
        }
        if (game.gameType !in setOf(GameType.CUSTOM)) {
            throw ArkhamusServerRequestException(
                "Wrong game type", RELATED_ENTITY
            )
        }
        if (sourceUserInvitations.size >= MAX_PENDING_INVITATIONS) {
            throw ArkhamusServerRequestException(
                "You have reached the maximum number of pending invitations", RELATED_ENTITY
            )
        }
        if (!game.usersOfGameSession.any { it.userAccount.id == currentUser.id }) {
            throw ArkhamusServerRequestException(
                "current user is not a part of a ${game.id}", RELATED_ENTITY
            )
        }
    }

    private fun createNewInvitation(
        session: GameSession,
        source: UserAccount,
        target: UserAccount
    ): GameInvitation = GameInvitation().apply {
        this.gameSession = session
        this.sourceUserAccount = source
        this.targetUserAccount = target
        this.state = PENDING
    }.let { gameInvitationRepository.save(it) }

}