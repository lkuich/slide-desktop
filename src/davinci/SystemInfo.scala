package davinci

import java.io.{FileOutputStream, File}
import java.nio.channels.{Channels, ReadableByteChannel}

import enums.OperatingSystem, java.net.{URLConnection, URL}

object SystemInfo {
    var systemExtension: String = ""
    var operatingSystem: OperatingSystem = OperatingSystem.UNKNOWN

    private var chmod: String = ""

    val os: String = System.getProperty("os.name").toUpperCase
    val bit: String = System.getProperty("sun.arch.data.model")

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

    def downloadFile(url: URL, path: File): Unit = {
        if (isConnected(url)) {
            try {
                val rbc: ReadableByteChannel = Channels.newChannel(url.openStream)

                val fos: FileOutputStream = new FileOutputStream(path)
                fos.getChannel.transferFrom(rbc, 0, Long.MaxValue)
                fos.close()
            }
        }
    }

    def isConnected(site: URL): Boolean = {
        try {
            val conn: URLConnection = site.openConnection
            conn.setConnectTimeout(5000)
            conn.getContent
            true
        }
        catch {
            case e2: Exception => false
        }
    }
}
