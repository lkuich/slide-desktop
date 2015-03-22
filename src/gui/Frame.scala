package gui

import java.awt.BorderLayout
import java.awt.event.{ActionEvent, ActionListener, WindowEvent, WindowListener}
import java.io.IOException
import javax.swing._

import connections.ConnectionManager
import connections.network.NetworkDeviceManager
import connections.usb.{Adb, UsbDeviceManager}
import davinci.{Device, FileManager, SystemInfo}
import enums.ConnectionMode
import gui.img.ImageIcons

object Frame extends JFrame with WindowListener {

    {
        /** Setup interface */

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName)

        this.setTitle("Slide")
        this.setResizable(false)
        this.addWindowListener(this)
        this.setBounds(100, 100, 0, 0)
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)

        val menuBar = new MenuBar{
            override def showAdb(): Unit = {
                new Console().runAdbProcess(Adb.adbDevices())
            }

            override def restartAdb(): Unit = {
                Adb.restartAdb()
            }
        }
        this.setJMenuBar(menuBar)

        if (!SystemInfo.isNetworkIsAvailable)
            showErrorPrompt("Error", "No suitable network interface found.\nWiFi connection will be unavailable.")
    }

    /** Can implement onDownloadStart, and onDownloadFinish */
    private val fm: FileManager = new FileManager {}
    Adb.init(fm)

    /**
     * Field where device information will appear.
     */
    private val deviceField: DeviceField = new DeviceField(() => super.pack(),
        new ActionListener {
            override def actionPerformed(e: ActionEvent): Unit = {
                if (ConnectionManager.hasConnection(ConnectionMode.USB) || ConnectionManager.multipleConnections) {
                    try {
                        usbMan.connect("localhost")
                    }
                    catch {
                        case e@(_: IOException | _: NullPointerException) =>
                            showErrorPrompt("Error", "Could not connect over USB.\nCheck if your device is listed by pressing Alt+A")
                    }
                }
                else if (ConnectionManager.hasConnection(ConnectionMode.WIFI)) {
                    try {
                        networkMan.connect(networkMan.ip)
                    } catch {
                        case e@(_: IOException | _: NullPointerException) =>
                            showErrorPrompt("Error", "Could not connect over LAN.\nPlease disable any interfering software and try again.")
                    }
                }
            }
        })
    this.getContentPane.add(deviceField, BorderLayout.CENTER)

    /** Controls USB connections. */
    private val usbMan: UsbDeviceManager = new UsbDeviceManager {
        override def onUsbConnectionAdded(): Unit = {
            onConnectionAdded(device, ConnectionMode.USB)
        }

        override def onUsbConnectionRemoved(): Unit = {
            onConnectionRemoved(device, ConnectionMode.USB)
        }

        override def throwError(message: String): Unit = {
            showErrorPrompt("Error", message)
        }
    }

    /** Controls WiFi connections. */
    private val networkMan: NetworkDeviceManager = new NetworkDeviceManager {
        override def onWifiConnectionAdded(): Unit = {
            onConnectionAdded(device, ConnectionMode.WIFI)
        }

        override def onWifiConnectionRemoved(): Unit = {
            onConnectionRemoved(device, ConnectionMode.WIFI)
        }

        override def throwError(message: String): Unit = {
            showErrorPrompt("Error", message)
        }
    }

    /**
     * Called when a device is connected.
     * @param device            Device information.
     * @param connectionMode    Mode of connection.
     */
    private def onConnectionAdded(device: Device, connectionMode: ConnectionMode): Unit = {
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

        pack()
    }

    /**
     * Called when a device is removed.
     * @param device            Device information
     * @param connectionMode    Mode of connection
     */
    private def onConnectionRemoved(device: Device, connectionMode: ConnectionMode): Unit = {
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
        if (device == null) {
            deviceField.showDeviceField(visibility = false)
        } else {
            deviceField.setUi(device)
        }

        pack()
    }

    // Start managers
    networkMan.startBackgroundScanner()
    usbMan.startBackgroundScanner()

    /**
     * Displays error prompt.
     * @param title     Title of the dialog.
     * @param message   Contents of the error message.
     */
    def showErrorPrompt(title: String, message: String): Unit = {
        val errorMessage: ErrorMessage = new ErrorMessage(this, "Connection Error")
        errorMessage.title = title
        errorMessage.message = message
        errorMessage.showDialog()
    }

    override def windowOpened(e: WindowEvent): Unit = super.pack()

    override def windowDeiconified(e: WindowEvent): Unit = {}

    override def windowClosing(e: WindowEvent): Unit = {}

    override def windowClosed(e: WindowEvent): Unit = {}

    override def windowActivated(e: WindowEvent): Unit = {}

    override def windowDeactivated(e: WindowEvent): Unit = {}

    override def windowIconified(e: WindowEvent): Unit = {}
}
