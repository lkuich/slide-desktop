package connections

import davinci.Device
import enums.ConnectionMode

abstract class DeviceManager {
    var device: Device = null

    def onUsbConnectionAdded(): Unit = ???
    def onUsbConnectionRemoved(): Unit = ???

    def onWifiConnectionAdded(): Unit = ???
    def onWifiConnectionRemoved(): Unit = ???
}
