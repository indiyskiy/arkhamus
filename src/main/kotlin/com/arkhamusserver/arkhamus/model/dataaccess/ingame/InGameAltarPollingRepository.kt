package com.arkhamusserver.arkhamus.model.dataaccess.ingame

import com.arkhamusserver.arkhamus.model.dataaccess.ingame.interfaces.RamCrudRepository
import com.arkhamusserver.arkhamus.model.ingame.InGameAltarPolling
import org.springframework.stereotype.Repository

@Repository
class InGameAltarPollingRepository : RamCrudRepository<InGameAltarPolling>()