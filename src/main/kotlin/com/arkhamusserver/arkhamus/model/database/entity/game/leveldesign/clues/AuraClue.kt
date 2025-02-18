package com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.clues

import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.Level
import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.LevelZone
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithPoint
import jakarta.persistence.*

@Entity
class AuraClue(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,
    var x: Double,
    var y: Double,
    var z: Double,
    var inGameId: Long,
    @ManyToOne
    @JoinColumn(name = "levelId", nullable = false)
    var level: Level? = null,
    @ManyToOne
    @JoinColumn(name = "zoneId", nullable = false)
    var zone: LevelZone? = null,
    val minSpawnRadius: Double,
    val maxSpawnRadius: Double,
    val interactionRadius: Double,
) : WithPoint {

    constructor() : this(null, 0.0, 0.0, 0.0, 0, null, null, 0.0, 0.0, 0.0)

    override fun x(): Double {
        return x
    }

    override fun y(): Double {
        return y
    }

    override fun z(): Double {
        return z
    }
}