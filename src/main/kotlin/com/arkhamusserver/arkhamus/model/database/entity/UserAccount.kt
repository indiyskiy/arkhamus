package com.arkhamusserver.arkhamus.model.database.entity

import com.arkhamusserver.arkhamus.model.database.entity.game.Role
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.sql.Timestamp

@Entity
data class UserAccount(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,
    var steamId: String? = null,
    @CreationTimestamp
    var creationTimestamp: Timestamp? = null,
    var nickName: String = "",
    var email: String? = null,
    var password: String? = null,
    @ManyToMany(cascade = [CascadeType.MERGE], fetch = FetchType.EAGER)
    var role: Set<Role> = emptySet(),
    @OneToOne(mappedBy = "userAccount", cascade = [CascadeType.PERSIST, CascadeType.MERGE], orphanRemoval = true)
    var userSkinSettings: UserSkinSettings? = null
) {
    constructor() : this(
        id = null,
        steamId = null,
        creationTimestamp = null,
        nickName = "",
        email = null,
        password = null,
        role = emptySet(),
        userSkinSettings = null
    )

    override fun toString(): String {
        return String.format(
            "UserAccount[id=%d, nickName='%s', email='%s', roles='%s']",
            id,
            nickName,
            email,
            role.joinToString { it.id.toString() + "-" + it.name }
        )
    }

}