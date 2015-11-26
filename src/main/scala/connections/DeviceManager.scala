package connections

import java.io.IOException

trait DeviceManager {
    @throws(classOf[IOException])
    protected def connect(ip: String): Unit

    protected def startBackgroundScanner(): Unit

    protected def stopBackgroundScanner(): Unit

    protected def throwError(message: String): Unit
}
