package connections.network

import java.io.{ObjectInputStream, IOException}
import java.net.InetSocketAddress

import connections.BaseDeviceConnection
import davinci.Const

class NetworkDeviceConnection(val remoteHost: String) extends BaseDeviceConnection {

    private val inetAddress: InetSocketAddress = new InetSocketAddress(remoteHost, Const.NET_PORT)

    socket.connect(inetAddress, 2000)
    socket.setTcpNoDelay(true)

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
