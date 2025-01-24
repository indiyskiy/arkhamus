package com.arkhamusserver.arkhamus.logic.steam

import com.arkhamusserver.arkhamus.view.dto.user.SteamUserShortDto
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/steam")
class SteamController(
    private val steamReaderLogic: SteamReaderLogic
) {
    @GetMapping("/friends")
    fun getServerSteamID(): List<SteamUserShortDto> {
        return steamReaderLogic.readFriendList()
    }
}