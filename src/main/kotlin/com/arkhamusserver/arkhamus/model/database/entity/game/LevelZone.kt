package com.arkhamusserver.arkhamus.model.database.entity.game

import com.arkhamusserver.arkhamus.model.enums.ingame.ZoneType
import jakarta.persistence.*

@Entity
data class LevelZone(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,
    var inGameId: Long,
    @Enumerated(EnumType.STRING)
    var zoneType: ZoneType,
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "levelZone")
    var tetragons: List<Tetragon> = emptyList(),
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "levelZone")
    var ellipse: List<Ellipse> = emptyList(),
    @ManyToOne
    @JoinColumn(name = "levelId", nullable = false)
    var level: Level
)