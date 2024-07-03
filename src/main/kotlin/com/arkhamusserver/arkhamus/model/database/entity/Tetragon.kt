package com.arkhamusserver.arkhamus.model.database.entity

import jakarta.persistence.*
import org.postgresql.geometric.PGpoint

@Entity
data class Tetragon(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,
    var inGameId: Long,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "levelZoneId", referencedColumnName = "id", nullable = false)
    var levelZone: LevelZone,
    var point0: PGpoint,
    var point1: PGpoint,
    var point2: PGpoint,
    var point3: PGpoint,
)