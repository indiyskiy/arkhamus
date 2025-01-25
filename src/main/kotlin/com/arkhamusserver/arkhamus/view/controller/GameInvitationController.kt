package com.arkhamusserver.arkhamus.view.controller

import com.arkhamusserver.arkhamus.config.UpdateUserState
import com.arkhamusserver.arkhamus.config.UserState
import com.arkhamusserver.arkhamus.logic.GameInvitationLogic
import com.arkhamusserver.arkhamus.view.dto.GameInvitationDto
import com.arkhamusserver.arkhamus.view.dto.GameSessionDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("game/invitation")
class GameInvitationController(
    private val gameInvitationLogic: GameInvitationLogic,
) {
    @UpdateUserState(UserState.ONLINE)
    @GetMapping
    fun myInvitations(): ResponseEntity<List<GameInvitationDto>> {
        val invitations = gameInvitationLogic.myInvitations()
        return ResponseEntity.ok(invitations)
    }

    @UpdateUserState(UserState.IN_LOBBY)
    @GetMapping("{gameId}")
    fun getGameInvitations(@PathVariable gameId: Long): ResponseEntity<List<GameInvitationDto>> {
        val invitations = gameInvitationLogic.gameInvitations(gameId)
        return ResponseEntity.ok(invitations)
    }

    @UpdateUserState(UserState.IN_LOBBY)
    @PostMapping("{gameId}/{userId}")
    fun inviteUser(
        @PathVariable gameId: Long,
        @PathVariable userId: Long,
    ): ResponseEntity<GameInvitationDto> {
        val invitation = gameInvitationLogic.createInvitation(gameId, userId)
        return ResponseEntity.ok(invitation)
    }

    @UpdateUserState(UserState.ONLINE)
    @PostMapping("{invitationId}/accept")
    fun acceptInvitation(
        @PathVariable invitationId: Long,
    ): ResponseEntity<GameSessionDto> {
        val invitation = gameInvitationLogic.acceptInvitation(invitationId)
        return ResponseEntity.ok(invitation)
    }

    @UpdateUserState(UserState.ONLINE)
    @PostMapping("{invitationId}/reject")
    fun declineInvitation(
        @PathVariable invitationId: Long,
    ): ResponseEntity<GameInvitationDto> {
        val invitation = gameInvitationLogic.rejectInvitation(invitationId)
        return ResponseEntity.ok(invitation)
    }

}