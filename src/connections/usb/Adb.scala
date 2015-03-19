package connections.usb

import java.io.{InputStreamReader, BufferedReader, File, IOException}
import davinci.{FileManager, SystemInfo, Const}
import com.android.ddmlib.AndroidDebugBridge.IDeviceChangeListener
import com.android.ddmlib.{IDevice, AndroidDebugBridge}
import enums.OperatingSystem
import gui.Console

object Adb {

    private var isAdbInstalled: Boolean = false
    var usbAvailable: Boolean = false

    var adbFilePath: String = ""

    private var called: Boolean = false
    def init(fileManager: FileManager): Unit = {
        if (!called) {
            SystemInfo.operatingSystem match {
                case OperatingSystem.WINDOWS =>
                    adbFilePath = Const.ADB + SystemInfo.systemExtension
                    if (!adbFileIsAvailable) {
                        fileManager.downloadFile(Const.MAINT_BASE + "adb/win/adb.exe", "adb.exe")
                        fileManager.downloadFile(Const.MAINT_BASE + "adb/win/AdbWinApi.dll", "AdbWinApi.dll")
                        fileManager.downloadFile(Const.MAINT_BASE + "adb/win/AdbWinUsbApi.dll", "AdbWinUsbApi.dll")
                    }
                case OperatingSystem.OSX => adbFilePath = "/Applications/Slide.app/Contents/Resources/" + Const.ADB
                case _ => adbFilePath = "./" + Const.ADB
            }
            called = true
        }

        var command: String = Const.ADB
        if (adbFileIsAvailable && !isAdbInstalled) {
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

    @throws(classOf[IOException])
    def startAdb() {
        if (isAdbInstalled) {
            new ProcessBuilder(Const.ADB, "forward", "tcp:" + Const.USB_PORT, "tcp:" + Const.USB_PORT).start
        }
        else if (adbFileIsAvailable) {
            if (SystemInfo.chmod != "") {
                new ProcessBuilder("chmod", SystemInfo.chmod, adbFilePath).start
            }

            try {
                new ProcessBuilder(adbFilePath, "forward", "tcp:" + Const.USB_PORT, "tcp:" + Const.USB_PORT).start
            } catch {
                case e: Exception => println("ADB is unavailable")
            }
        }
    }

    def adbFileIsAvailable: Boolean = {
        val adbFile: File = new File(adbFilePath)
        adbFile.exists
    }

    def isAdbAvailable: Boolean = {
        isAdbInstalled || adbFileIsAvailable
    }

    def showAdbDevices(): Boolean = {
        var deviceAvailable: Boolean = false
        var command: String = Const.ADB + " devices"

        if (adbFileIsAvailable && !isAdbInstalled) {
            command = adbFilePath + " devices"
        }

        val consoleOutput: Console = new Console()

        var consoleOut: String = null
        var stdInput: BufferedReader = null
        var stdError: BufferedReader = null
        if (adbFileIsAvailable) {
            val pr: Process = Runtime.getRuntime.exec(command)

            stdInput = new BufferedReader(new InputStreamReader(pr.getInputStream))
            stdError = new BufferedReader(new InputStreamReader(pr.getErrorStream))

            while ({ consoleOut = stdInput.readLine(); consoleOut != null }) {
                if (consoleOut.contains("	device"))
                {
                    deviceAvailable = true
                }
                consoleOutput.append(consoleOut)
            }

            var errorOut: String = null
            while ({ errorOut = stdError.readLine(); errorOut != null})
            {
                consoleOutput.append(errorOut)
            }
        } else {
            consoleOutput.append("Error: ADB is not installed")
        }
        consoleOutput.show()

        deviceAvailable
    }
}
