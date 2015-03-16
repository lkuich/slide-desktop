package gui

import java.awt.BorderLayout
import java.awt.event.{WindowEvent, ActionEvent, ActionListener, WindowListener}
import java.io.IOException
import javax.swing._
import connections.network.NetworkDeviceManager
import connections.usb.UsbDeviceManager
import davinci.Master
import enums.ConnectionMode

object Frame extends JFrame with WindowListener {

    this.setLookAndFeel()

    private val middle_x: Int = 250
    private val middle_y: Int = 100

    val deviceField: DeviceField = new DeviceField(middle_x, middle_y, new ActionListener {
        override def actionPerformed(e: ActionEvent): Unit = {
            if (Master.hasConnection(ConnectionMode.USB) || Master.multipleConnections) {
                try {
                    usbMan.connect()
                }
                catch {
                    case e @ (_: IOException | _: NullPointerException) =>
                        errorMessage.message = "Could not connect over USB.\nCheck if your device is listed by pressing Alt+A"
                        errorMessage.showDialog()
                        e.printStackTrace()
                }
            }
            else if (Master.hasConnection(ConnectionMode.WIFI)) {
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

    deviceField.onControlsVisible()

    private val usbMan: UsbDeviceManager = new UsbDeviceManager
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

    override def windowOpened(e: WindowEvent): Unit =
        this.setSize(deviceField.maxWidth() + getInsets.left, deviceField.maxHeight() + getInsets.top)

    override def windowDeiconified(e: WindowEvent): Unit = {}

    override def windowClosing(e: WindowEvent): Unit = {}

    override def windowClosed(e: WindowEvent): Unit = {}

    override def windowActivated(e: WindowEvent): Unit = {}

    override def windowDeactivated(e: WindowEvent): Unit = {}

    override def windowIconified(e: WindowEvent): Unit = {}
}
