package com.arkhamusserver.arkhamus.logic.user.steam

import com.codedisaster.steamworks.*
import org.slf4j.LoggerFactory

class SteamGameServerCallbackImpl(
    private val handler: SteamHandler // Reference to update server state in handler
) : SteamGameServerCallback {

    companion object {
        private val logger = LoggerFactory.getLogger(SteamGameServerCallbackImpl::class.java)
    }

    override fun onSteamServersConnected() {
        logger.info("Steam servers connected!")
        handler.setLoggedOn(true) // Update the logged-in state
        handler.updateServerSteamID() // Fetch and store the server's SteamID
    }

    override fun onSteamServerConnectFailure(result: SteamResult, retry: Boolean) {
        logger.error("Failed to connect to Steam servers. Result: {}, Retry: {}", result, retry)
    }

    override fun onSteamServersDisconnected(result: SteamResult) {
        logger.warn("Steam servers disconnected! Result: {}", result)
        handler.setLoggedOn(false) // Update the logged-in state
    }

    override fun onValidateAuthTicketResponse(
        steamID: SteamID,
        response: SteamAuth.AuthSessionResponse,
        ownerSteamID: SteamID
    ) {
        logger.info(
            "Auth ticket validation received. Client SteamID: {}, Response: {}, Owner SteamID: {}",
            steamID, response, ownerSteamID
        )
    }

    override fun onClientApprove(clientSteamID: SteamID, ownerSteamID: SteamID) {
        logger.info("Client approved! Client SteamID: {}, Owner SteamID: {}", clientSteamID, ownerSteamID)
    }

    override fun onClientDeny(clientSteamID: SteamID, denyReason: SteamGameServer.DenyReason, optionalMessage: String) {
        logger.warn("Client denied. SteamID: {}, Reason: {}, Message: {}", clientSteamID, denyReason, optionalMessage)
    }

    override fun onClientKick(clientSteamID: SteamID, denyReason: SteamGameServer.DenyReason) {
        logger.warn("Client kicked. SteamID: {}, Reason: {}", clientSteamID, denyReason)
    }

    override fun onClientGroupStatus(
        clientSteamID: SteamID,
        groupSteamID: SteamID,
        isMember: Boolean,
        isOfficer: Boolean
    ) {
        logger.info(
            "Client group status received. Client SteamID: {}, Group SteamID: {}, IsMember: {}, IsOfficer: {}",
            clientSteamID, groupSteamID, isMember, isOfficer
        )
    }

    override fun onAssociateWithClanResult(result: SteamResult) {
        logger.info("Associate with Clan Result: {}", result)
    }

    override fun onComputeNewPlayerCompatibilityResult(
        result: SteamResult,
        playersThatDontLikeCandidate: Int,
        playersThatCandidateDoesntLike: Int,
        clanPlayersThatDontLikeCandidate: Int,
        candidateSteamID: SteamID
    ) {
        logger.info(
            "Player compatibility check completed. Result: {}, Candidate: {}, PlayersThatDontLikeCandidate: {}, " +
                    "PlayersThatCandidateDoesntLike: {}, ClanPlayersThatDontLikeCandidate: {}",
            result, candidateSteamID, playersThatDontLikeCandidate, playersThatCandidateDoesntLike,
            clanPlayersThatDontLikeCandidate
        )
    }
}