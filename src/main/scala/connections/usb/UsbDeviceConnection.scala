package connections.usb

import java.io.{IOException, ObjectInputStream}
import java.net.InetSocketAddress

import connections.BaseDeviceConnection
import slide.Const

class UsbDeviceConnection(val ip: String) extends BaseDeviceConnection {

    /** Forward ADB before every connection */
    Adb.startAdb()

    /** IP to connect to */
    private val inetAddress: InetSocketAddress = new InetSocketAddress(ip, Const.USB_PORT)

    /** Connect to IP, 2000ms timeout */
    super.socket.connect(inetAddress, 2000)

    private val input: ObjectInputStream = new ObjectInputStream(socket.getInputStream)

    @throws[IOException]
    override def connect(): Boolean = this.start()

    //** TODO: Below method is EXPARAMENTAL, should move into superclass
    /* override def handleMessage(message: Array[Short]): Unit = {
        if (DeviceMessageType.fromId(message(0)) == DeviceMessageType.SCREEN_SHOT) {
            this.onScreenShot()
        }
        else {
            super.handleMessage(message)
        }
    }

    def onScreenShot(): Unit = {
        val image: BufferedImage = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit.getScreenSize))
        try {
            ImageIO.write(image, "PNG", socket.getOutputStream)
        }
        catch {
            case e: IOException =>
                throw new RuntimeException("Failed to write screen shot.", e)
        }
    } */
    //** TODO: END EXPARAMENTAL

    @throws[IOException]
    @throws[ClassNotFoundException]
    override def nextMessage(): Array[Short] = {
        try {
            input.readObject.asInstanceOf[Array[Short]]
        } catch {
            case e: ClassNotFoundException => new Array[Short](1)
        }
    }

    /**
     * Closes the connection.
     */
    override def close(): Unit = {
        stopRunning()
        if (input != null) {
            input.close()
        }
        socket.close()
    }
}
