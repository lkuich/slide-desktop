package slide

import java.net.NetworkInterface
import enums.OperatingSystem

object SystemInfo {
    private val os: String = System.getProperty("os.name").toUpperCase
    var operatingSystem: OperatingSystem = OperatingSystem.UNKNOWN

    var systemExtension: String = ""
    var chmod: String = "+x"

    if (os.toUpperCase.contains("WIN")) {
        chmod = ""
        systemExtension = ".exe"
        operatingSystem = OperatingSystem.WINDOWS
    }
    else if (os.toUpperCase.contains("MAC"))
        operatingSystem = OperatingSystem.OSX
    else
        operatingSystem = OperatingSystem.NIX

    /**
     * @return Whether or not the system has a NIC
     */
    def isNetworkIsAvailable: Boolean = {
        val interfaces: java.util.Enumeration[NetworkInterface] = NetworkInterface.getNetworkInterfaces
        while (interfaces.hasMoreElements) {
            val interf: NetworkInterface = interfaces.nextElement()
            if (interf.isUp && !interf.isLoopback)
                return true
        }
        false
    }
}
