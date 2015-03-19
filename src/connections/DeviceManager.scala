package connections

import davinci.Device
import enums.ConnectionMode

abstract class DeviceManager {
    var device: Device = null

    def onUsbConnectionAdded(manager: DeviceManager, connectionMode: ConnectionMode): Unit = ???
    def onUsbConnectionRemoved(manager: DeviceManager, connectionMode: ConnectionMode): Unit = ???

    def onWifiConnectionAdded(manager: DeviceManager, connectionMode: ConnectionMode): Unit = ???
    def onWifiConnectionRemoved(manager: DeviceManager, connectionMode: ConnectionMode): Unit = ???
}
