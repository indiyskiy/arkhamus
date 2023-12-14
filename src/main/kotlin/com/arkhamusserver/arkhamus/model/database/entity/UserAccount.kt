package com.arkhamusserver.arkhamus.model.database.entity

import com.arkhamusserver.arkhamus.model.enums.Role
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
class UserAccount {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    var id: Long? = null
    var nickName: String? = null
    var email: String? = null
    var password: String? = null
    var role: Role? = null

    override fun toString(): String {
        return String.format(
            "UserAccount[id=%d, nickName='%s', email='%s']",
            id, nickName, email
        )
    }
}