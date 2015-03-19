package connections.usb

import java.io.IOException
import connections.{ConnectionManager, DeviceManager}
import davinci.Device
import davinci.Main
import enums.ConnectionMode
import gui.Frame
import gui.img.ImageIcons

class UsbDeviceManager extends DeviceManager {

    private var udc: UsbDeviceConnection = null
    private var backgroundScannerRunning: Boolean = true

    @throws(classOf[IOException])
    def connect():Unit = {
        udc = new UsbDeviceConnection
        udc.connect()
    }

    def startBackgroundScanner():Unit = {
        val t: Thread = new Thread( new Runnable {
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
                        if (dcCount >= 2) {
                            if (device != null) {
                                if (ConnectionManager.hasConnection(ConnectionMode.USB)) {
                                    if (device != null) {
                                        onUsbConnectionRemoved()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        })
        if (!Adb.isAdbAvailable) {
            Frame.showErrorPrompt("Error", "ADB not found.")
        } else {
            Adb.startAdb()
            t.start()
        }
    }

    def stopBackgroundScanner():Unit = {
        backgroundScannerRunning = false
    }
}