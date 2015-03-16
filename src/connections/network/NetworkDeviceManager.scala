package connections.network

import java.io.IOException
import java.net.SocketException

import davinci.{Master, Device}
import enums.ConnectionMode
import gui.Frame
import gui.img.ImageIcons

class NetworkDeviceManager() {
    private var ndc: NetworkDeviceConnection = null

    private var backgroundScannerRunning: Boolean = true
    private var device: Device = null

    @throws(classOf[IOException])
    def connect(ip: String): Unit = {
        ndc = new NetworkDeviceConnection(ip)
        ndc.connect()
    }

    def startBackgroundScanner(): Unit = {
        val t: Thread = new Thread(new Runnable {
            override def run(): Unit = {
                var dcCount: Int = 0

                var udpDiscovery: BroadcastManager = null
                try {
                    udpDiscovery = new BroadcastManager
                }
                catch {
                    case e: SocketException =>
                        Frame.showErrorPrompt("Error", "Another instance of Slide is already running.")
                        System.exit(1)
                }

                while (backgroundScannerRunning) {
                    if (!udpDiscovery.networkIsAvailable) {
                        stopBackgroundScanner()
                    }

                    device = udpDiscovery.search
                    if (device != null) {
                        dcCount = 0
                        if (!Master.hasConnection(ConnectionMode.WIFI)) {
                            Master.addConnection(ConnectionMode.WIFI)
                            if (Master.multipleConnections) {
                                adjustGui(ConnectionMode.USB)
                            }
                            else {
                                adjustGui(ConnectionMode.WIFI)
                            }
                        }
                    } else {
                        dcCount += 1
                        if (dcCount >= 4) {
                            if (Master.hasConnection(ConnectionMode.WIFI)) {
                                Master.removeConnection(ConnectionMode.WIFI)
                                if (Master.hasConnection(ConnectionMode.USB)) {
                                    adjustGui(ConnectionMode.USB)
                                }
                                else {
                                    adjustGui(hidden = false)
                                }
                            }
                        }
                    }
                }
            }
        })
        t.start()
    }

    def stopBackgroundScanner(): Unit =
        backgroundScannerRunning = false

    def ip: String = device.ip

    private def adjustGui(connectionMode: ConnectionMode) {
        Frame.deviceField.show()
        if (connectionMode == ConnectionMode.USB) {
            if (device != null) {
                device.icon = ImageIcons.usbIcon
                Frame.deviceField.setUi(device)
            }
        }
        else {
            device.icon = ImageIcons.wifiIcon
            Frame.deviceField.setUi(device)
        }
    }

    private def adjustGui(hidden: Boolean) {
        Frame.deviceField.showDeviceField(hidden)
    }
}
