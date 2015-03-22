package connections.usb

import java.io.{File, IOException}

import com.android.ddmlib.AndroidDebugBridge.IDeviceChangeListener
import com.android.ddmlib.{AndroidDebugBridge, IDevice}
import davinci.{Const, FileManager, SystemInfo}
import enums.OperatingSystem

object Adb {

    var usbAvailable: Boolean = false

    var isAdbInstalled: Boolean = false
    var adbFilePath: String = "./"
    /** Make's sure init only gets called once */
    private var called: Boolean = false

    /**
     * Initializes Adb
     * @param fileManager File manager to download needed libraries.
     */
    def init(fileManager: FileManager): Unit = {
        if (!called) {
            SystemInfo.operatingSystem match {
                case OperatingSystem.WINDOWS =>
                    adbFilePath = Const.ADB + SystemInfo.systemExtension
                    if (!isAdbFilePresent) {
                        fileManager.downloadFile(Const.MAINT_BASE + "adb/win/adb.exe", "adb.exe")
                        fileManager.downloadFile(Const.MAINT_BASE + "adb/win/AdbWinApi.dll", "AdbWinApi.dll")
                        fileManager.downloadFile(Const.MAINT_BASE + "adb/win/AdbWinUsbApi.dll", "AdbWinUsbApi.dll")
                    }
                case OperatingSystem.OSX => adbFilePath = new File("adb").getCanonicalPath //"~/Documents/Slide.app/Contents/Resources/adb"
                case _ => adbFilePath += Const.ADB
            }
            called = true
        } else {
            throw new Exception("init can not be called more than once.")
        }

        var command: String = Const.ADB
        if (isAdbFilePresent && !isAdbInstalled) {
            command = adbFilePath
        }

        AndroidDebugBridge.init(false)
        val debugBridge: AndroidDebugBridge = AndroidDebugBridge.createBridge(command, true)
        isAdbInstalled = debugBridge.getDevices.length > 0

        if (isAdbAvailable) {
            AndroidDebugBridge.addDeviceChangeListener(new IDeviceChangeListener {
                override def deviceChanged(device: IDevice, arg: Int) {}

                override def deviceConnected(device: IDevice) {
                    usbAvailable = true
                }

                override def deviceDisconnected(device: IDevice) {
                    usbAvailable = false
                }
            })
        }
    }

    def isAdbAvailable: Boolean = isAdbInstalled || isAdbFilePresent

    def isAdbFilePresent: Boolean = new File(adbFilePath).exists()

    @throws(classOf[IOException])
    private def executeAdbProcess(process: ProcessBuilder): Process = {
        if (isAdbInstalled) {
            process.start()
        } else if (isAdbFilePresent) {
            if (SystemInfo.chmod != "") {
                new ProcessBuilder("chmod", SystemInfo.chmod, adbFilePath).start
            }

            try {
                process.start()
            } catch {
                case e: Exception =>
                    println("ADB is unavailable")
                    null
            }
        } else {
            null
        }
    }

    /**
     * Forwards ADB ports.
     * @throws java.io.IOException If there is trouble accessing ADB.
     */
    def startAdb(): Process = {
        executeAdbProcess(new ProcessBuilder(adbFilePath, "forward", "tcp:" + Const.USB_PORT, "tcp:" + Const.USB_PORT))
    }

    def adbDevices(): Process = {
        executeAdbProcess(new ProcessBuilder(adbFilePath, "devices"))
    }

    def restartAdb(): Process = {
        executeAdbProcess(new ProcessBuilder(adbFilePath, "kill-server"))
        executeAdbProcess(new ProcessBuilder(adbFilePath, "start-server"))
    }
}
