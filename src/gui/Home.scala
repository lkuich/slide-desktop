package gui

import java.awt.BorderLayout
import java.awt.event.{ActionEvent, ActionListener}
import java.io.IOException
import javax.swing.{JOptionPane, WindowConstants, JFrame}

import connections.network.NetworkDeviceManager
import connections.usb.UsbDeviceManager
import davinci.{Const, Master}
import enums.ConnectionMode

object Home extends JFrame {

    private val middle_x: Int = 250
    private val middle_y: Int = 100

    val deviceField: DeviceField = new DeviceField(middle_x, middle_y)

    private val usbMan: UsbDeviceManager = new UsbDeviceManager
    private val networkMan: NetworkDeviceManager = new NetworkDeviceManager

    private val errorMessage: ErrorMessage = new ErrorMessage(this, "Connection Error")

    // Initialize interface components
    this.setTitle("Slide")
    this.setResizable(false)
    this.setBounds(100, 100, 250, 210)
    this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)

    this.getContentPane.add(deviceField, BorderLayout.CENTER)

    // Start managers
    networkMan.startBackgroundScanner()
    usbMan.startBackgroundScanner()

    deviceField.actionListener = new ActionListener {
        override def actionPerformed(e: ActionEvent): Unit = {
            if (Master.hasConnection(ConnectionMode.USB) || Master.multipleConnections) {
                try {
                    usbMan.connect()
                }
                catch {
                    case e1: Exception =>
                        errorMessage.message_$eq("Could not connect over USB.\nCheck if your device is listed by pressing Alt+A")
                        errorMessage.showDialog()
                }
            }
            else if (Master.hasConnection(ConnectionMode.WIFI)) {
                try {
                    networkMan.connect(networkMan.ip, Const.NET_PORT)
                }
                catch {
                    case e @ (_: IOException | _: NullPointerException) =>
                        errorMessage.message = "Could not connect over LAN."
                        errorMessage.showDialog()
                }
            }
        }
    }

    def showHome(): Unit = this.setVisible(true)

    def showErrorPrompt(title: String, message: String): Unit = {
        errorMessage.title = title
        errorMessage.message = message
        errorMessage.showDialog()
    }

    def showPrompt(message: String): Unit =
        JOptionPane.showMessageDialog(this, message)
}
