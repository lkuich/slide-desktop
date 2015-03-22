package connections.network

import java.io.IOException
import java.net.SocketException

import connections.{BaseDeviceManager, ConnectionManager}
import davinci.SystemInfo
import enums.ConnectionMode

class NetworkDeviceManager extends BaseDeviceManager {

    private var ndc: NetworkDeviceConnection = null
    private var backgroundScannerRunning: Boolean = true

    @throws(classOf[IOException])
    override def connect(ip: String): Unit = {
        ndc = new NetworkDeviceConnection(ip) {
            override def onClientOutOfDate(): Unit = {
                throwError("The client is out of date. Please upgrade it.")
            }
        }
        ndc.connect()
    }

    override def throwError(message: String): Unit = {}

    override def startBackgroundScanner(): Unit = {
        val t: Thread = new Thread(new Runnable {
            override def run(): Unit = {
                var dcCount: Int = 0

                var udpDiscovery: BroadcastManager = null
                try {
                    udpDiscovery = new BroadcastManager
                }
                catch {
                    case e: SocketException =>
                        throwError("Another instance of Slide is already running.")
                        System.exit(1)
                }

                while (backgroundScannerRunning) {
                    if (!SystemInfo.isNetworkIsAvailable) {
                        stopBackgroundScanner()
                    }

                    device = udpDiscovery.search
                    if (device != null) {
                        dcCount = 0
                        if (!ConnectionManager.hasConnection(ConnectionMode.WIFI)) {
                            onWifiConnectionAdded()
                        }
                    } else {
                        dcCount += 1
                        if (dcCount >= 4) {
                            if (ConnectionManager.hasConnection(ConnectionMode.WIFI)) {
                                onWifiConnectionRemoved()
                            }
                        }
                    }
                }
            }
        })
        t.start()
    }

    override def stopBackgroundScanner(): Unit =
        backgroundScannerRunning = false

    def ip: String = device.ip
}
