package connections.usb

import java.io.IOException

import connections.{BaseDeviceManager, ConnectionManager}
import slide.Device
import enums.ConnectionMode
import gui.ImageIcons

class UsbDeviceManager extends BaseDeviceManager {

    private var udc: UsbDeviceConnection = null
    private var backgroundScannerRunning: Boolean = true

    @throws(classOf[IOException])
    def connect(ip: String): Unit = {
        udc = new UsbDeviceConnection(ip) {
            override def onClientOutOfDate(): Unit = {
                throwError("The client is out of date. Please upgrade it.")
            }
        }
        udc.connect()
    }

    def startBackgroundScanner(): Unit = {
        val t: Thread = new Thread(new Runnable {
            def run() {
                device = new Device(ImageIcons.usbIcon, Array[String]("USB", "USB", "USB"))
                var dcCount: Int = 0
                while (backgroundScannerRunning) {
                    Thread.sleep(1000)
                    if (Adb.usbAvailable) {
                        dcCount = 0
                        if (!ConnectionManager.hasConnection(ConnectionMode.USB)) {
                            onUsbConnectionAdded()
                        }
                    }
                    else {
                        dcCount += 1
                        if (dcCount >= 2 && device != null && ConnectionManager.hasConnection(ConnectionMode.USB)) {
                            onUsbConnectionRemoved()
                        }
                    }
                }
            }
        })
        if (!Adb.isAdbAvailable) {
            throwError("Adb not found")
        } else {
            t.start()
        }
    }

    override def throwError(message: String): Unit = {}

    def stopBackgroundScanner(): Unit = backgroundScannerRunning = false
}
