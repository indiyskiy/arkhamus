package com.arkhamusserver.arkhamus.model.database.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.sql.Timestamp

@Entity
class UserAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null
    @CreationTimestamp
    var creationTimestamp: Timestamp? = null
    var nickName: String? = null
    var email: String? = null
    var password: String? = null

    @ManyToMany(cascade = [CascadeType.MERGE], fetch = FetchType.EAGER)
    var role: Set<Role> = emptySet()

    override fun toString(): String {
        return String.format(
            "UserAccount[id=%d, nickName='%s', email='%s']", id, nickName, email
        )
    }
}