package connections.network

import java.net.{DatagramPacket, DatagramSocket}

import davinci.Device
import gui.img.ImageIcons

// TODO: Don't want to reference the GUI package

class BroadcastManager extends DatagramSocket(5000) {

    this.setSoTimeout(1000)

    /**
     * Starts searching for devices on the network.
     * @return  First detected device.
     */
    def search: Device = {
        val receiveData: Array[Byte] = new Array[Byte](1024)

        try {
            val receivePacket: DatagramPacket = new DatagramPacket(receiveData, receiveData.length)

            this.receive(receivePacket)

            val message: String = new String(receivePacket.getData).trim
            new Device(ImageIcons.wifiIcon, message.split(","))
        } catch {
            case e: Exception => null
        }
    }
}
