package slide

import java.net.NetworkInterface
import java.util

import enums.OperatingSystem

object SystemInfo {
    private val os: String = System.getProperty("os.name").toUpperCase
    private val bit: String = System.getProperty("sun.arch.data.model")
    var systemExtension: String = ""
    var operatingSystem: OperatingSystem = OperatingSystem.UNKNOWN
    var chmod: String = ""

    if (os.toUpperCase.contains("WIN")) {
        chmod = ""
        systemExtension = ".exe"
        operatingSystem = OperatingSystem.WINDOWS
        "win" + "_" + bit
    }
    else if (os.toUpperCase.contains("MAC")) {
        chmod = "+x"
        operatingSystem = OperatingSystem.OSX
        "osx" + "_" + bit
    }
    else {
        chmod = "+x"
        operatingSystem = OperatingSystem.NIX
        "nix" + "_" + bit
    }

    /**
     * @return Whether or not the system has a NIC
     */
    def isNetworkIsAvailable: Boolean = {
        val interfaces: util.Enumeration[NetworkInterface] = NetworkInterface.getNetworkInterfaces
        while (interfaces.hasMoreElements) {
            val interf: NetworkInterface = interfaces.nextElement()
            if (interf.isUp && !interf.isLoopback)
                return true
        }
        false
    }
}
