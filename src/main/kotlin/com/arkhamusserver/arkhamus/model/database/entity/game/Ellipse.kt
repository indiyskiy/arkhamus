package com.arkhamusserver.arkhamus.model.database.entity.game

import jakarta.persistence.*
import org.postgresql.geometric.PGpoint

@Entity
data class Ellipse(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,
    var inGameId: Long,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "levelZoneId", referencedColumnName = "id", nullable = false)
    var levelZone: LevelZone,
    var point: PGpoint,
    var height: Double,
    var width: Double,
)