package com.arkhamusserver.arkhamus.model.database.entity

import com.arkhamusserver.arkhamus.model.enums.InvitationState
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.sql.Timestamp

/**
 * mutable part of game that user can actually change
 */
@Entity
data class GameInvitation(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,
    @ManyToOne
    @JoinColumn(name = "gameSessionId", nullable = false)
    var gameSession: GameSession? = null,
    @ManyToOne
    @JoinColumn(name = "sourceUserAccountId", nullable = true)
    var sourceUserAccount: UserAccount? = null,
    @ManyToOne
    @JoinColumn(name = "targetUserAccountId", nullable = true)
    var targetUserAccount: UserAccount? = null,
    @Enumerated(EnumType.STRING)
    var state: InvitationState,

    @CreationTimestamp
    var creationTimestamp: Timestamp? = null,
    var finishedTimestamp: Timestamp? = null,
) {
    // No-arg constructor for JPA
    constructor() : this(
        id = null,
        gameSession = null,
        sourceUserAccount = null,
        targetUserAccount = null,
        state = InvitationState.PENDING,
        creationTimestamp = null,
        finishedTimestamp = null,
    )
}
