package connections

import slide.Device

abstract class BaseDeviceManager extends DeviceManager {
    var device: Device = null

    def onUsbConnectionAdded(): Unit = {}

    def onUsbConnectionRemoved(): Unit = {}

    def onWifiConnectionAdded(): Unit = {}

    def onWifiConnectionRemoved(): Unit = {}
}
