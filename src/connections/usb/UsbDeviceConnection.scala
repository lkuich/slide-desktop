package connections.usb

import java.awt.{Toolkit, Rectangle, Robot}
import java.awt.image.BufferedImage
import java.io.{EOFException, ObjectInputStream, IOException}
import java.net.InetSocketAddress
import javax.imageio.ImageIO

import connections.BaseDeviceConnection
import davinci.Const
import enums.DeviceMessageType

class UsbDeviceConnection extends BaseDeviceConnection {

    private val inetAddress: InetSocketAddress = new InetSocketAddress("localhost", Const.USB_PORT)

    socket.connect(inetAddress, 2000)
    socket.setTcpNoDelay(true)
    // socket.setKeepAlive(true)

    private var input: ObjectInputStream = null
    try {
        input = new ObjectInputStream(socket.getInputStream)
    } catch {
        case e: Exception => e.printStackTrace()
    }

    @throws[IOException]
    override def connect(): Boolean = this.start()

    //** TODO: Below method is EXPARAMENTAL, should move into superclass
    override def handleMessage(message: Array[Short]): Unit = {
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
    }
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

    override def close(): Unit = {
        stopRunning()
        if (input != null) {
            input.close()
        }
        socket.close()
    }
}
