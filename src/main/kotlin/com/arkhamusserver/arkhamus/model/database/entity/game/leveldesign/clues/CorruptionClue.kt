package com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.clues

import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.Level
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithPoint
import jakarta.persistence.*

@Entity
class CorruptionClue(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,
    var x: Double,
    var y: Double,
    var z: Double,
    var inGameId: Long,
    var interactionRadius: Double,
    var name: String,
    @ManyToOne
    @JoinColumn(name = "levelId", nullable = false)
    var level: Level? = null
) : WithPoint {

    constructor() : this(null, 0.0, 0.0, 0.0, 0L, 0.0, "", null)

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