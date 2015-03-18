package connections.usb

import java.io.IOException
import davinci.Device
import davinci.Main
import enums.ConnectionMode
import gui.Frame
import gui.img.ImageIcons

class UsbDeviceManager {
    private var udc: UsbDeviceConnection = null
    private var usbDevice: Device = null
    private var backgroundScannerRunning: Boolean = true

    @throws(classOf[IOException])
    def connect():Unit = {
        udc = new UsbDeviceConnection
        udc.connect()
    }

    def startBackgroundScanner():Unit = {
        val t: Thread = new Thread( new Runnable {
            def run() {
                usbDevice = new Device(ImageIcons.usbIcon, Array[String]("USB", "USB", "USB"))
                var dcCount: Int = 0
                while (backgroundScannerRunning) {
                    Thread.sleep(1000)
                    if (Adb.usbAvailable) {
                        dcCount = 0
                        if (!Main.hasConnection(ConnectionMode.USB)) {
                            Main.addConnection(ConnectionMode.USB)
                            adjustGui(ConnectionMode.USB)
                        }
                    }
                    else {
                        dcCount += 1
                        if (dcCount >= 2) {
                            if (usbDevice != null) {
                                if (Main.hasConnection(ConnectionMode.USB)) {
                                    Main.removeConnection(ConnectionMode.USB)
                                    if (Main.hasConnection(ConnectionMode.WIFI)) {
                                        adjustGui(ConnectionMode.WIFI)
                                    }
                                    else {
                                        adjustGui(hidden = false)
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
        }
        else {
            Adb.startAdb()
            t.start()
        }
    }

    def stopBackgroundScanner():Unit = {
        backgroundScannerRunning = false
    }

    private def adjustGui(connectionMode: ConnectionMode) {
        Frame.deviceField.show()
        if (connectionMode == ConnectionMode.USB) {
            usbDevice.icon = ImageIcons.usbIcon
            Frame.deviceField.setUi(usbDevice)
        }
        else {
            usbDevice.icon = ImageIcons.wifiIcon
            Frame.deviceField.setUi(usbDevice)
        }
    }

    private def adjustGui(hidden: Boolean) {
        Frame.deviceField.showDeviceField(hidden)
    }
}