package gui

import java.awt.BorderLayout
import java.awt.event.{WindowEvent, ActionEvent, ActionListener, WindowListener}
import java.io.IOException
import javax.swing._
import connections.ConnectionManager
import connections.network.NetworkDeviceManager
import connections.usb.{Adb, UsbDeviceManager}
import davinci.{Device, FileManager}
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
                        e.printStackTrace() /* TODO: Remove */
                }
            }
        }
    })

    def onConnectionAdded(device: Device, connectionMode: ConnectionMode): Unit = {
        deviceField.show()
        if (connectionMode == ConnectionMode.USB) {
            device.icon = ImageIcons.usbIcon
            deviceField.setUi(device)
        }
        else {
            device.icon = ImageIcons.wifiIcon
            deviceField.setUi(device)
        }
    }

    protected val onConnectionRemoved: () => Unit = ???

    private val usbMan: UsbDeviceManager = new UsbDeviceManager {
        override
    }
    private val networkMan: NetworkDeviceManager = new NetworkDeviceManager

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
