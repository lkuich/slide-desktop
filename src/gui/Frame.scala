package gui

import java.awt.BorderLayout
import java.awt.event.{WindowEvent, ActionEvent, ActionListener, WindowListener}
import java.io.IOException
import javax.swing._
import connections.{DeviceManager, ConnectionManager}
import connections.network.NetworkDeviceManager
import connections.usb.{Adb, UsbDeviceManager}
import davinci.{Main, Device, FileManager}
import enums.ConnectionMode
import gui.img.ImageIcons

object Frame extends JFrame with WindowListener {

    this.setLookAndFeel()

    // Can implement onDownloadStart, and onDownloadFinish
    val fm: FileManager = new FileManager{}
    Adb.init(fm)

    val deviceField: DeviceField = new DeviceField(() => super.pack(),
        new ActionListener {
        override def actionPerformed(e: ActionEvent): Unit = {
            if (ConnectionManager.hasConnection(ConnectionMode.USB) || ConnectionManager.multipleConnections) {
                try {
                    usbMan.connect()
                }
                catch {
                    case e @ (_: IOException | _: NullPointerException) =>
                        errorMessage.message = "Could not connect over USB.\nCheck if your device is listed by pressing Alt+A"
                        errorMessage.showDialog()
                        e.printStackTrace() /* TODO: Remove */
                }
            }
            else if (ConnectionManager.hasConnection(ConnectionMode.WIFI)) {
                try {
                    networkMan.connect(networkMan.ip)
                }
                catch {
                    case e @ (_: IOException | _: NullPointerException) =>
                        errorMessage.message = "Could not connect over LAN."
                        errorMessage.showDialog()
                }
            }
        }
    })

    def onConnectionAdded(device: Device, connectionMode: ConnectionMode): Unit = {
        ConnectionManager.addConnection(connectionMode)

        if (ConnectionManager.multipleConnections) {
            device.icon = ImageIcons.usbIcon
        } else {
            connectionMode match {
                case ConnectionMode.USB =>
                    device.icon = ImageIcons.usbIcon
                case ConnectionMode.WIFI =>
                    device.icon = ImageIcons.wifiIcon
            }
        }

        deviceField.show()
        deviceField.setUi(device)
    }

    def onConnectionRemoved(device: Device, connectionMode: ConnectionMode): Unit = {
        if (ConnectionManager.multipleConnections) {
            deviceField.showDeviceField(visibility = true)

            connectionMode match {
                case ConnectionMode.USB =>
                    device.icon = ImageIcons.wifiIcon
                case ConnectionMode.WIFI =>
                    device.icon = ImageIcons.usbIcon
            }
        } else {
            deviceField.showDeviceField(visibility = false)
        }

        ConnectionManager.removeConnection(connectionMode)
        deviceField.setUi(device)
    }

    private val usbMan: UsbDeviceManager = new UsbDeviceManager {
        override def onUsbConnectionAdded(): Unit = {
            onConnectionAdded(device, ConnectionMode.USB)
        }

        override def onUsbConnectionRemoved(): Unit = {
            onConnectionRemoved(device, ConnectionMode.USB)
        }
    }
    
    private val networkMan: NetworkDeviceManager = new NetworkDeviceManager {
        override def onWifiConnectionAdded(): Unit = {
            onConnectionAdded(device, ConnectionMode.WIFI)
        }

        override def onWifiConnectionRemoved(): Unit = {
            onConnectionRemoved(device, ConnectionMode.WIFI)
        }
    }

    private val errorMessage: ErrorMessage = new ErrorMessage(this, "Connection Error")

    // Initialize interface components
    this.setTitle("Slide")
    this.setResizable(false)
    this.addWindowListener(this)
    this.setBounds(100, 100, 0, 0)
    this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)

    this.getContentPane.add(deviceField, BorderLayout.CENTER)

    // Start managers
    networkMan.startBackgroundScanner()
    usbMan.startBackgroundScanner()

    def showHome(): Unit = this.setVisible(true)

    def showErrorPrompt(title: String, message: String): Unit = {
        errorMessage.title = title
        errorMessage.message = message
        errorMessage.showDialog()
    }

    def showPrompt(message: String): Unit =
        JOptionPane.showMessageDialog(this, message)

    def setLookAndFeel(): Unit = UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName)

    override def windowOpened(e: WindowEvent): Unit = super.pack()

    override def windowDeiconified(e: WindowEvent): Unit = {}

    override def windowClosing(e: WindowEvent): Unit = {}

    override def windowClosed(e: WindowEvent): Unit = {}

    override def windowActivated(e: WindowEvent): Unit = {}

    override def windowDeactivated(e: WindowEvent): Unit = {}

    override def windowIconified(e: WindowEvent): Unit = {}
}
