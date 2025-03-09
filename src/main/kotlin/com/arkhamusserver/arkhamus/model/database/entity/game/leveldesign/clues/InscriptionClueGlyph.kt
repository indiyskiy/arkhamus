package com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.clues

import com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign.Level
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithPoint
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
data class InscriptionClueGlyph (
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,
    @ManyToOne
    @JoinColumn(name = "relatedClueId", nullable = false)
    var relatedClue: InscriptionClue?,
    var x: Double,
    var y: Double,
    var z: Double,
    var inGameId: Long,
    @ManyToOne
    @JoinColumn(name = "levelId", nullable = false)
    var level: Level? = null
) : WithPoint {

    constructor() : this(null, null, 0.0, 0.0, 0.0, 0, null)

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