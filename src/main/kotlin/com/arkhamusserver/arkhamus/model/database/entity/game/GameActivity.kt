package com.arkhamusserver.arkhamus.model.database.entity.game

import com.arkhamusserver.arkhamus.model.database.entity.GameSession
import com.arkhamusserver.arkhamus.model.database.entity.UserOfGameSession
import com.arkhamusserver.arkhamus.model.enums.ingame.ActivityType
import com.arkhamusserver.arkhamus.model.enums.ingame.GameObjectType
import jakarta.persistence.*

@Entity
data class GameActivity(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,
    var x: Double,
    var y: Double,
    var z: Double,
    val gameTime: Long,
    val activityType: ActivityType,
    @Enumerated(EnumType.STRING)
    val relatedGameObjectType: GameObjectType?,
    val relatedGameObjectId: Long?,
    val relatedEventId: Long?,
    @ManyToOne
    @JoinColumn(name = "gameSessionId", nullable = false)
    var gameSession: GameSession,
    @ManyToOne
    @JoinColumn(name = "userOfGameSessionId", nullable = false)
    var userOfGameSession: UserOfGameSession,
)