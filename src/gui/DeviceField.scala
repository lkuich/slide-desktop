package gui

import java.awt.{Color, Rectangle}
import java.awt.event.{ActionListener, KeyEvent}
import javax.swing._

import connections.usb.Adb
import davinci.Device

class DeviceField(val posX: Int, val posY: Int, var actionListener: ActionListener) extends JPanel {

    def this(posX: Int, posY: Int) = this(posX, posY, null)

    this.setLayout(null)

    private var ip: String = "localhost"
    private var name: String = ""
    private var manufacturer: String = ""

    private val fieldBounds = new Rectangle(SwingConstants.CENTER, SwingConstants.VERTICAL, posX, posY)

    private var imageLabel: JLabel = null
    private val connectButton: JButton = new JButton("Connect")
    private val nameLabel: JLabel = new JLabel
    private val manuLabel: JLabel = new JLabel
    private val ipAddressLabel: JLabel = new JLabel

    private val lblNoDevicesDetected: JLabel = new JLabel(
        "<html><center>No devices detected<br/><br/>Scanning for devices...</center></html>")
    this.lblNoDevicesDetected.setHorizontalAlignment(SwingConstants.CENTER)
    this.lblNoDevicesDetected.setBounds(this.fieldBounds)

    this.add(lblNoDevicesDetected)
    this.add(manuLabel)
    this.add(nameLabel)
    this.add(ipAddressLabel)
    this.add(connectButton)

    val alta: KeyBinder = new KeyBinder(KeyEvent.VK_ALT, KeyEvent.VK_A) {
        override def onKeysDown(): Unit = Adb.showAdbDevices()
    }
    val altl: KeyBinder = new KeyBinder(KeyEvent.VK_ALT, KeyEvent.VK_L) {
        override def onKeysDown(): Unit = Licence.showLicense()
    }

    connectButton.addKeyListener(alta)
    connectButton.addKeyListener(altl)

    this.setVisible(true)

    def setUi(d: Device): Unit = {
        if (imageLabel != null) {
            this.remove(this.imageLabel)
        }
        val icon: ImageIcon = d.icon
        if (d.ip != "USB") {
            ip = d.ip
            name = d.model
            manufacturer = d.manufacturer
        }
        imageLabel = new JLabel(icon)
        this.add(imageLabel)
        setComponents()
    }

    private def setComponents() {
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER)
        imageLabel.setBounds(this.fieldBounds)
        imageLabel.setBounds(SwingConstants.CENTER, SwingConstants.VERTICAL, posX, posY)

        nameLabel.setText(this.name)
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER)
        nameLabel.setBackground(Color.white)
        nameLabel.setBounds(SwingConstants.CENTER, SwingConstants.VERTICAL, posX, posY + 80)

        manuLabel.setText(manufacturer)
        manuLabel.setBackground(Color.white)
        manuLabel.setHorizontalAlignment(SwingConstants.CENTER)
        manuLabel.setBounds(SwingConstants.CENTER, SwingConstants.VERTICAL, posX, posY + 110)

        ipAddressLabel.setText(ip)
        ipAddressLabel.setBackground(Color.white)
        ipAddressLabel.setHorizontalAlignment(SwingConstants.CENTER)
        ipAddressLabel.setBounds(SwingConstants.CENTER, SwingConstants.VERTICAL, posX, posY + 140)

        connectButton.setHorizontalAlignment(SwingConstants.CENTER)
        connectButton.setBounds(78, 140, 100, 30)

        setListeners()
        this.updateUI()
    }

    def setListeners() {
        if (connectButton.getActionListeners.length <= 0) {
            connectButton.addActionListener(actionListener)
        }
    }

    override def show(): Unit = { showDeviceField(visibility = true) }
    def showDeviceField(visibility: Boolean): Unit = {
        this.lblNoDevicesDetected.setVisible(!visibility)
        if (this.imageLabel != null) {
            this.imageLabel.setVisible(visibility)
        }
        if (this.nameLabel != null) {
            this.nameLabel.setVisible(visibility)
            this.nameLabel.setText("")
        }
        if (this.manuLabel != null) {
            this.manuLabel.setVisible(visibility)
            this.manuLabel.setText("")
        }
        if (this.ipAddressLabel != null) {
            this.ipAddressLabel.setVisible(visibility)
            this.ipAddressLabel.setText("localhost")
        }
        if (this.connectButton != null) {
            this.connectButton.setVisible(visibility)
        }
    }
}
