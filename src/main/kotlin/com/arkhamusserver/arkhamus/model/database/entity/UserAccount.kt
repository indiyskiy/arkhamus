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
    var nickName: String,
    var email: String? = null,
    var password: String? = null,
    @ManyToMany(cascade = [CascadeType.MERGE], fetch = FetchType.EAGER)
    var role: Set<Role> = emptySet(),
) {
    override fun toString(): String {
        return String.format(
            "UserAccount[id=%d, nickName='%s', email='%s']", id, nickName, email
        )
    }
}