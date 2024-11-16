package com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign

import com.arkhamusserver.arkhamus.model.enums.LevelState
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.sql.Timestamp

@Entity
data class Level(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,

    @CreationTimestamp
    var creationTimestamp: Timestamp? = null,

    var version: Long,
    var levelId: Long,
    var levelHeight: Long,
    var levelWidth: Long,
    @Enumerated(EnumType.STRING)
    var state: LevelState
)