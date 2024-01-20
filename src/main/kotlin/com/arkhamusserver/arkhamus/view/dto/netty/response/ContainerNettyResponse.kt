package com.arkhamusserver.arkhamus.view.dto.netty.response

data class ContainerNettyResponse (
    var containerCells: List<NettyContainerCell> = emptyList()
):NettyResponseMessage