package com.arkhamusserver.arkhamus.model.dataaccess.ingame

import com.arkhamusserver.arkhamus.model.dataaccess.ingame.interfaces.RamCrudRepository
import com.arkhamusserver.arkhamus.model.ingame.InGameContainer
import org.springframework.stereotype.Repository

@Repository
class InGameContainerRepository : RamCrudRepository<InGameContainer>()