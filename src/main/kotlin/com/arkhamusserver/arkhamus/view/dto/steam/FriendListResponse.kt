package com.arkhamusserver.arkhamus.view.dto.steam

data class FriendListResponse(
    val friendslist: FriendsList? // Mark nullable in case it's missing
)

data class FriendsList(
    val friends: List<Friend>? // Mark nullable in case there are no friends
)

data class Friend(
    val steamid: String, // The ID of the friend
    val relationship: String, // Likely "friend"
    val friend_since: Long // Timestamp for when the friendship began
)
