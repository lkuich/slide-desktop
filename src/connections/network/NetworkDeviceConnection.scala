package connections.network

import java.io.{IOException, ObjectInputStream}
import java.net.InetSocketAddress

import connections.BaseDeviceConnection
import slide.Const

class NetworkDeviceConnection(val ip: String) extends BaseDeviceConnection {

    private val inetAddress: InetSocketAddress = new InetSocketAddress(ip, Const.NET_PORT)

    super.socket.connect(inetAddress, 4000)

    private val input = new ObjectInputStream(socket.getInputStream)

    @throws[IOException]
    override def connect(): Boolean = {
        this.start()
    }

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
        input.close()
        socket.close()
    }
}
