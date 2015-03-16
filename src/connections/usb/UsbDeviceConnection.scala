package connections.usb

import java.awt.{Toolkit, Rectangle, Robot}
import java.awt.image.BufferedImage
import java.io.{ObjectInputStream, IOException}
import java.net.{Socket, InetSocketAddress}
import javax.imageio.ImageIO

import connections.BaseDeviceConnection
import davinci.Const
import enums.DeviceMessageType

class UsbDeviceConnection extends BaseDeviceConnection {

    var connection: Socket = null
    private var input: ObjectInputStream = null

    @throws[IOException]
    override def connect(): Boolean = {
        val inetAddress: InetSocketAddress = new InetSocketAddress("localhost", Const.USB_PORT)
        connection = new Socket
        connection.connect(inetAddress, 2000)
        connection.setTcpNoDelay(true)
        connection.setKeepAlive(true)
        this.input = new ObjectInputStream(connection.getInputStream)

        this.start()
    }

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
            ImageIO.write(image, "PNG", connection.getOutputStream)
        }
        catch {
            case e: IOException =>
                throw new RuntimeException("Failed to write screen shot.", e)
        }
    }

    @throws[IOException]
    @throws[ClassNotFoundException]
    override def nextMessage(): Array[Short] = new Array[Short](0)

    override def close(): Unit = {
        onClose()
        if (input != null) {
            input.close()
        }
        if (connection != null) {
            connection.close()
        }
    }
}
