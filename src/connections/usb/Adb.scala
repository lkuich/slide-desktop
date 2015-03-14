package connections.usb

import java.io.{InputStreamReader, BufferedReader, File, IOException}
import davinci.SystemInfo
import com.android.ddmlib.AndroidDebugBridge.IDeviceChangeListener
import com.android.ddmlib.{IDevice, AndroidDebugBridge}
import davinci.Const
import enums.OperatingSystem
import gui.Console

object Adb {
    private var isAdbInstalled: Boolean = false
    var usbAvailable: Boolean = false

    var adbFilePath: String = ""

    // def run(): Unit = {

    SystemInfo.operatingSystem match {
        case OperatingSystem.WINDOWS => adbFilePath = Const.ADB + SystemInfo.systemExtension
        case OperatingSystem.OSX => adbFilePath = "/Applications/Slide.app/Contents/Resources/" + Const.ADB
        case _ => adbFilePath = "./" + Const.ADB
    }

    var command: String = Const.ADB
    if (adbFileIsAvailable && !isAdbInstalled) {
        command = adbFilePath
    }

    AndroidDebugBridge.init(false)
    val debugBridge: AndroidDebugBridge = AndroidDebugBridge.createBridge(command, true)
    isAdbInstalled = debugBridge != null

    if (isAdbInstalled) {

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
    // }

    @throws(classOf[IOException])
    def startAdb() {
        if (isAdbInstalled) {
            new ProcessBuilder(Const.ADB, "forward", "tcp:" + Const.USB_PORT, "tcp:" + Const.USB_PORT).start
        }
        else if (adbFileIsAvailable) {
            if (SystemInfo.chmod != "") {
                new ProcessBuilder("chmod", SystemInfo.chmod, adbFilePath).start
            }
            new ProcessBuilder(adbFilePath, "forward", "tcp:" + Const.USB_PORT, "tcp:" + Const.USB_PORT).start
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
        try {
            var command: String = Const.ADB + " devices"

            if (adbFileIsAvailable && !isAdbInstalled) {
                command = adbFilePath + " devices"
            }

            val pr: Process = Runtime.getRuntime.exec(command)

            val stdInput: BufferedReader =
                new BufferedReader(new InputStreamReader(pr.getInputStream))
            val stdError:BufferedReader =
                new BufferedReader(new InputStreamReader(pr.getErrorStream))

            var consoleOut: String = null
            val consoleOutput:Console = new Console()

            while ({ consoleOut = stdInput.readLine(); consoleOut != null }) {
                if (consoleOut.contains("	device"))
                {
                    deviceAvailable = true
                }
                consoleOutput.consoleTextField.append(consoleOut + "\n")
            }

            var errorOut: String = null
            while ({ errorOut = stdError.readLine(); errorOut != null})
            {
                consoleOutput.consoleTextField.append(errorOut + "\n")
            }
            consoleOutput.consoleFrame.setVisible(true);
            }

            deviceAvailable
    }
}
