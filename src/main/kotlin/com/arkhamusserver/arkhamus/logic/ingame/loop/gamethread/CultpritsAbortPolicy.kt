package com.arkhamusserver.arkhamus.logic.ingame.loop.gamethread

import com.arkhamusserver.arkhamus.logic.ingame.loop.ArkhamusOneTickLogicImpl
import com.arkhamusserver.arkhamus.util.logging.LoggingUtils
import org.slf4j.LoggerFactory
import java.util.concurrent.RejectedExecutionHandler
import java.util.concurrent.ThreadPoolExecutor

class CultpritsAbortPolicy : RejectedExecutionHandler {

    companion object {
        private val logger = LoggingUtils.getLogger<CultpritsAbortPolicy>()
    }

    private val abortPolicy = ThreadPoolExecutor.AbortPolicy()

    override fun rejectedExecution(r: Runnable, executor: ThreadPoolExecutor) {
        logger.warn("Task $r rejected from $executor")
        abortPolicy.rejectedExecution(r, executor)
    }
}