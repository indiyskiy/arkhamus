package com.arkhamusserver.arkhamus.model.database.entity.game.leveldesign

import com.arkhamusserver.arkhamus.model.enums.ingame.tag.ContainerTag
import com.arkhamusserver.arkhamus.model.ingame.interfaces.WithPoint
import jakarta.persistence.*

@Entity
data class Container(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,
    var inGameId: Long,
    var x: Double,
    var y: Double,
    var z: Double,
    var interactionRadius: Double,

    @ElementCollection(fetch = FetchType.EAGER, targetClass = ContainerTag::class)
    @CollectionTable(name = "containerTags", joinColumns = [JoinColumn(name = "entity_id")])
    @Enumerated(EnumType.STRING)
    @Column(name = "containerTags")
    var containerTags: Set<ContainerTag> = emptySet(),

    @ManyToOne
    @JoinColumn(name = "levelId", nullable = false)
    var level: Level? = null
) : WithPoint {
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