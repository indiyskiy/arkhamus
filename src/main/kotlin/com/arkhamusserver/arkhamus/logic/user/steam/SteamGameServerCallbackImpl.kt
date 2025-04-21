package com.arkhamusserver.arkhamus.logic.user.steam

import com.arkhamusserver.arkhamus.util.logging.LoggingUtils
import com.codedisaster.steamworks.*

class SteamGameServerCallbackImpl(
    private val handler: SteamHandler // Reference to update server state in handler
) : SteamGameServerCallback {

    companion object {
        private val logger = LoggingUtils.getLogger<SteamGameServerCallbackImpl>()
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
        response: SteamAuth.AuthSessionResponse?,
        ownerSteamID: SteamID
    ) {
        try {
            logger.info("Auth ticket validation received for SteamID: {}, Response: {}", steamID, response)
            // Only process the response if it's not null
            if (response != null) {
                // Handle the response based on its value
                when (response) {
                    SteamAuth.AuthSessionResponse.OK -> logger.info("Auth ticket is valid")
                    SteamAuth.AuthSessionResponse.UserNotConnectedToSteam -> logger.warn("User not connected to Steam")
                    SteamAuth.AuthSessionResponse.NoLicenseOrExpired -> logger.warn("No license or expired")
                    SteamAuth.AuthSessionResponse.VACBanned -> logger.warn("VAC banned")
                    SteamAuth.AuthSessionResponse.LoggedInElseWhere -> logger.warn("Logged in elsewhere")
                    SteamAuth.AuthSessionResponse.VACCheckTimedOut -> logger.warn("VAC check timed out")
                    SteamAuth.AuthSessionResponse.AuthTicketCanceled -> logger.warn("Auth ticket canceled")
                    SteamAuth.AuthSessionResponse.AuthTicketInvalidAlreadyUsed -> logger.warn("Auth ticket invalid - already used")
                    SteamAuth.AuthSessionResponse.AuthTicketInvalid -> logger.warn("Auth ticket invalid")
                    SteamAuth.AuthSessionResponse.PublisherIssuedBan -> logger.warn("Publisher issued ban")
                }
            } else {
                logger.warn("Received null auth session response for SteamID: {}", steamID)
            }
        } catch (e: Exception) {
            logger.error("Error processing auth ticket validation: {}", e.message)
        }
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
