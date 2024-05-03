package com.arkhamusserver.arkhamus.model.database.entity

import com.arkhamusserver.arkhamus.model.enums.ingame.CrafterType
import jakarta.persistence.*

@Entity
data class Crafter (
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,
    var inGameId: Long? = null,
    var interactionRadius: Double? = null,
    var x: Double? = null,
    var y: Double? = null,
    @ManyToOne
    @JoinColumn(name = "levelId", nullable = false)
    var level: Level? = null,
    var crafterType: CrafterType? = null,
)