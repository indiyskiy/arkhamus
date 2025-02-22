package com.arkhamusserver.arkhamus.model.database.entity

import com.arkhamusserver.arkhamus.model.database.entity.user.UserAccount
import com.arkhamusserver.arkhamus.model.enums.ingame.core.ClassInGame
import com.arkhamusserver.arkhamus.model.enums.ingame.core.RoleTypeInGame
import jakarta.persistence.*

@Entity
data class UserOfGameSession(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "userAccountId", nullable = false)
    var userAccount: UserAccount,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gameSessionId", referencedColumnName = "id", nullable = false)
    var gameSession: GameSession,

    var host: Boolean,

    @Enumerated(EnumType.STRING)
    var roleInGame: RoleTypeInGame? = null,
    @Enumerated(EnumType.STRING)
    var classInGame: ClassInGame? = null,

    var won: Boolean? = null,
    var leftTheLobby: Boolean = false,
) {
    constructor() : this(
        id = null,
        userAccount = UserAccount(),
        gameSession = GameSession(),
        host = false,
        roleInGame = null,
        classInGame = null,
        won = null,
        leftTheLobby = false
    )

    override fun toString(): String {
        return "UserOfGameSession[" +
                "id=$id, " +
                "userAccount=$userAccount, " +
                "gameSessionId=${gameSession.id}, " +
                "host=$host, " +
                "roleInGame='$roleInGame', " +
                "classInGame='$classInGame'" +
                "]"
    }
}